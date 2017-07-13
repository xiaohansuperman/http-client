package com.wangliyong.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.SocketAddress;
import com.wangliyong.http.client.HttpClient;
import com.wangliyong.http.client.HttpRequest;
import com.wangliyong.http.client.HttpResponse;

public class Test {
	
	@org.junit.Test
	public void download() throws Exception{
		
	}
	
	@org.junit.Test
	public void upload() throws Exception{
		
		HttpClient client = HttpClient.getInstance(); 
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		byte[] content = new byte[1024*8];
		try {
			fis = new FileInputStream("H:/batchprice.xls");
			bis = new BufferedInputStream(fis);
			bos = new ByteArrayOutputStream();
			int len = 0;
			while((len=bis.read(content)) != -1){
				bos.write(content, 0, len);
			}
			//http://fileexcel.youche.com/templet.xls
			String url = "http://fileexcel.youche.com/batchprice2.xls";
			
			/*HttpRequest request = new HttpRequest(url);
			request.setMethod(HttpMethod.POST);
			request.setContent(bos.toByteArray());
			HttpResponse response = client.request(null, request, 10000, request.getHeaderBuf(),request.getContent())*/
			
			HttpResponse response = client.post("http://fileexcel.youche.com/batchprice2.xls",bos.toByteArray());
			
			System.out.println(new String(response.getBody(),"utf-8"));
		} catch (Exception e) {
			throw e;
		}finally {
			if(bos != null){
				bos.close();
			}
			if(bis != null){
				bis.close();
			}
			if(fis != null){
				fis.close();
			}
		}
	}
	
}
