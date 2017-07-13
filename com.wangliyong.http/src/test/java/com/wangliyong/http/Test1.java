package com.wangliyong.http;

import java.text.ParseException;

import com.wangliyong.http.client.HttpClient;
import com.wangliyong.http.client.HttpResponse;


public class Test1{
	
	public static void main(String[] args) throws ParseException{
		HttpClient client = HttpClient.getInstance();
		
		try {
			HttpResponse response =  client.get("http://127.0.0.1:8080");
			System.out.println("content:" + new String(response.getBody(),"utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
