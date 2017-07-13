package com.wangliyong.http.client;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    
	private String firstLine;
	
	private Map<String, String> headers = new HashMap<String, String>();
	
	private byte[] body;

	private String encoding = "UTF-8";
	
	public String getFirstLine() {
		return firstLine;
	}

	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}

	public Map<String, String> getHeaders() {
		return headers;
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

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
