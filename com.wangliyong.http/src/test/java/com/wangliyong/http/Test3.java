package com.wangliyong.http;

import com.wangliyong.http.client.HttpClient;
import com.wangliyong.http.client.HttpResponse;

public class Test3 {
	
	@org.junit.Test
	public void test(){
		
		HttpClient client = HttpClient.getInstance(); 
		try {
			HttpResponse response = client.get("http://127.0.0.1:1000");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
