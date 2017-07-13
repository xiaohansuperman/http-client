package com.wangliyong.http.client;

import java.net.SocketAddress;

public class HttpClient {
    
	private volatile static HttpClient httpClient;
	
	private static final int DEFAULT_TIMEOUT = 1000000;
	
	private HttpClient(){
		
	}
	
	public static HttpClient getInstance(){
		
		if(httpClient == null){
			synchronized (HttpClient.class) {
				if(httpClient == null){
					httpClient = new HttpClient();
				}
			}
		}
		
		return httpClient;
	}
	
	public HttpResponse get(String url) throws Exception{
		
		return get(url,DEFAULT_TIMEOUT);
	}
	
	public HttpResponse get(String url,int timeout) throws Exception{
		return get(url,timeout,null);
	}
	
	public HttpResponse get(String url,SocketAddress proxy) throws Exception{
		return get(url,DEFAULT_TIMEOUT,proxy);
	}
	
	public HttpResponse get(String url,byte[] bt) throws Exception{
		
		HttpRequest request = new HttpRequest(url);
		
		request.setBody(bt);
		
		return request(request, DEFAULT_TIMEOUT, null);
	}
	
	public HttpResponse get(String url,int timeout,SocketAddress proxy) throws Exception{
		
		HttpRequest request = new HttpRequest(url);
		
		return request(request, timeout, proxy);
	}
	
	private HttpResponse request(HttpRequest request,int timeout,SocketAddress proxy) throws Exception{
		
		HttpConnection connection = null;
		try {
			connection = new HttpConnection(request,timeout,proxy);
			connection.send(request);
			return connection.receive();
		} catch (Exception e) {
			throw e;
		}finally {
			if(connection != null){
				connection.close();
			}
		}
	}
	
	public HttpResponse post(String url,byte[] content) throws Exception{
		
		return post(url,DEFAULT_TIMEOUT,content);
	}
	
	public HttpResponse post(String url,int timeout,byte[] content) throws Exception{
		
		return post(url, timeout,null,content);
	}
	
	public HttpResponse post(String url,int timeout,SocketAddress proxy,byte[] content) throws Exception{
		
		
		HttpRequest request = new HttpRequest(url);
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setBody(content);
		
		return request(request, timeout, proxy);
	}
}

