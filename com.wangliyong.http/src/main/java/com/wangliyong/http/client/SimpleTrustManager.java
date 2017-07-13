package com.wangliyong.http.client;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class SimpleTrustManager implements X509TrustManager{

	 SimpleTrustManager() {}
	
	public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
			throws CertificateException {
		
	}

	public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
			throws CertificateException {
		
	}

	public X509Certificate[] getAcceptedIssuers() {

		return null;
	}

}
