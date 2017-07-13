package com.wangliyong.http.client;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpRequest {
   
	/**
     * 请求的url
     */
	private String url;
	
	/**
	 * 请求行 
	 */
	private Map<String, String> firstLine = new HashMap<String, String>();
	
	/**
	 * 请求头部
	 */
	private Map<String, String> headers = new HashMap<String, String>();
	
	/**
	 * 请求正文
	 */
	private byte[] body;
	
	/**
	 * 请求的host
	 */
	private String host;
	
	/**
	 * 请求的端口
	 */
	private int port;
	
	/**
	 * 编码格式
	 */
	private String encoding = "utf-8";
    
	/**
	 * http method
	 */
	private String method = "GET";
	
	/**
	 * http version
	 */
	private String httpVersion = "HTTP/1.1";
	
	private String contentType = "text/html";
	
	public HttpRequest(String url) throws Exception{
		setUrl(url);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) throws Exception {
		
		if(!url.startsWith("http")){
			throw new Exception("url must start with http");
		}
		
		this.url = url;
		
		URL tempUrl = new URL(url); 
		this.host = tempUrl.getHost();
		
		if(tempUrl.getPath() == null || "".equalsIgnoreCase(tempUrl.getPath())){
			firstLine.put("path", "/");
		}else if(tempUrl.getQuery() != null){
			firstLine.put("path", tempUrl.getPath()+"?"+tempUrl.getQuery());
		}else{
			firstLine.put("path", tempUrl.getPath());
		}
		
		if(url.startsWith("https")){
			this.port = 443;
		}else{
			this.port = (tempUrl.getPort() == -1 ? 80 : tempUrl.getPort());
		}
	}

	public byte[] getFirstLine() throws UnsupportedEncodingException {
		
		firstLine.put("method", this.method);
		firstLine.put("httpVersion", this.httpVersion);
		
		StringBuffer sb = new StringBuffer();
		sb.append(firstLine.get("method"));
		sb.append(" ");
		sb.append(firstLine.get("path"));
		if (this.httpVersion != null) {
		    sb.append(" ").append(this.httpVersion).append("\r\n");
		} else {
		    sb.append(" HTTP/1.1\r\n");
		}
		
		return sb.toString().getBytes(encoding);
	}

	public void setFirstLine(Map<String, String> firstLine) {
		this.firstLine = firstLine;
	}

	public byte[] getHeaders() throws UnsupportedEncodingException {
		
		if(this.headers.size() == 0){
			headers.put("Host", this.host);
			headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
			headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			headers.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			headers.put("Accept-Encoding", "gzip, deflate");
			headers.put("Pragma", "no-cache");
			headers.put("Cache-Control", "no-cache");
			headers.put("Content-Type", this.contentType);
			headers.put("Connection", "Keep-Alive");
	        
	        if (this.body != null) {
	            headers.put("Content-Length", String.valueOf(this.body.length));
	        } else {
	            headers.put("Content-Length", "0");
	        }
		}
		
		StringBuffer sb = new StringBuffer();
		Iterator<Map.Entry<String, String>> headerItr = headers.entrySet().iterator();
		while (headerItr.hasNext()) {
	        @SuppressWarnings({ "unchecked", "rawtypes" })
			Map.Entry<String, String> entry = (Map.Entry)headerItr.next();
	        sb.append((String)entry.getKey());
	        sb.append(": ");
	        sb.append((String)entry.getValue());
	        sb.append("\r\n");
		}
	    sb.append("\r\n");
	    
		return sb.toString().getBytes(encoding);
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
