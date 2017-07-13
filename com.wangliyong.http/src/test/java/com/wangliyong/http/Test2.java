package com.wangliyong.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import com.wangliyong.http.client.HttpClient;
import com.wangliyong.http.client.HttpResponse;

public class Test2 {
    
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
