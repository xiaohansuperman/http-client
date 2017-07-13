package com.wangliyong.http.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpConnection {
    
	private Socket socket = null;
	private static SSLContext sslCTX;
	private BufferedInputStream bufferedInputStream = null;
	private BufferedOutputStream bufferedOutputStream = null;
	
	static{
		try {
			X509TrustManager xtm = new SimpleTrustManager();
			TrustManager[] tm = { xtm };
			sslCTX = SSLContext.getInstance("SSL");
			sslCTX.init(null, tm, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public HttpConnection(HttpRequest request,int timeout,SocketAddress proxy) throws Exception{
		
		socket = createSocket(request.getUrl());
		socket.setSoTimeout(timeout);
		socket.setTcpNoDelay(true);
		
		SocketAddress address = null;
		if(proxy != null){
			address = proxy;
		}else{
			address = new InetSocketAddress(InetAddress.getByName(request.getHost()), request.getPort());
		}

		socket.connect(address, timeout);
	}
	
	public void send(HttpRequest request) throws IOException{
	
		bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
		bufferedOutputStream.write(request.getFirstLine());
		bufferedOutputStream.write(request.getHeaders());
		if(null != request.getBody()){
			bufferedOutputStream.write(request.getBody());
		}
		bufferedOutputStream.flush();
	}
	
	public HttpResponse receive() throws Exception {
		
		HttpResponse response = new HttpResponse();
		bufferedInputStream = new BufferedInputStream(socket.getInputStream());
		readFirstLineAndHeader(response);
		readBody(response);
		return response;
	}
	
	private int readFirstLineAndHeader(HttpResponse response) throws IOException{
		
		Map<String,String> map = new HashMap<String, String>();
		
		byte[] bt = new byte[524288];
		String line;
		boolean firstLine = true;
		int length = 0;
		
		while ((line = readLine(bt)) != null) {
			length += line.length();
			if(firstLine){
				response.setFirstLine(line);
				firstLine = false;
			}else{
				
				int split = line.indexOf(":");
				String key = line.substring(0, split);
				String value = "";
				
				if(line.length() > split+2){
					value = line.substring(split+2,line.length());
				}
				
				if(response.getHeaders().containsKey(key)) {
					String v = (String)response.getHeaders().get(key) + "\r\n" + value;
		            System.out.println("key:"+key);
		            System.out.println("value:"+value);
					map.put(key, v);
		        }else{
		           	map.put(key, value);
		        }
				
				response.setHeaders(map);
			}
		}
		
		return length;
	}
	
	private int readBody(HttpResponse response) throws Exception{
		
		String contentLength = response.getHeaders().get("Content-Length");
		String chunk = response.getHeaders().get("Transfer-Encoding");
		
		int length = 0;
		
		if(chunk != null && "chunked".equalsIgnoreCase(chunk)){
			
			byte[] bt = new byte[1000];
			byte[] bt2 = new byte[524288];
			String line;
			
			while((line = readLine(bt)) != null){
				
				int chunkLength = Integer.parseInt(line,16);
				//chunked编码以零结束
				if(chunkLength == 0){
					break;
				}
				
                int off =0;
				int count = 0;
				
				while ((chunkLength - off > 0) && (count != -1)) {
					//由于不是一次传完，用这个方法，tcp的分段传输
					count = bufferedInputStream.read(bt2, length, chunkLength - off);
					off += count;
					length += count;
				}
				
				readLine(bt);
			}
			
			response.setBody(toByteArray(bt2, length));
			
		} else if (contentLength != null){
			
			length = Integer.parseInt(contentLength);
			
			int off =0;
			int count = 0;
			byte[] bt2 = new byte[524288];
			
			while((length-off > 0) && count!=-1){
				count = bufferedInputStream.read(bt2,off,length-off);
				off += count;
			}
			
			response.setBody(toByteArray(bt2, length));
		}else{
			
			byte[] bt2 = new byte[524288];
			int count = 0;
			
			while ((count = bufferedInputStream.read()) != -1) {
				 bt2[length] = (byte)count;
				 length++;
			}
			
			response.setBody(toByteArray(bt2, length));
		}
		
		return length;
	}
	
	private String readLine(byte[] bt) throws IOException {
		byte last = 0;
		int b = 0;
        int length = 0;
		while (b != -1) {
			b = bufferedInputStream.read();
			bt[length] = (byte)b;
			length++;
			if((last == 13) && (b == 10)) {
				if(length>2){
					String line = new String(toByteArray(bt, length-2), "utf-8");
				    return line;
				}
		        return null;
			}
			last = (byte)b;
		}
		return null;
	}
	
	private byte[] toByteArray(byte[] bt,int length){
		//由于byte数组太大，复制一个，传真实的
		byte[] buf = new byte[length];
		System.arraycopy(bt, 0, buf, 0, length);
		
		return buf;
	}
	
	private Socket createSocket(String url) throws IOException{
		
		if(url.startsWith("https")){
			return sslCTX.getSocketFactory().createSocket();
		} else {
			return new Socket();
		}
		
	}
	
	public void close(){
		try {
			if (this.bufferedInputStream != null) {
				bufferedInputStream.close();
			}
			if (this.bufferedOutputStream != null) {
				bufferedOutputStream.close();
			}
			if (this.socket != null) {
				socket.close();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
