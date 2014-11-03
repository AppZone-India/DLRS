package com.sim2dial.dialer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class FullX509TrustManager implements X509TrustManager
{

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
	{
		// Oh, I am easy!
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
	{
		// Oh, I am easy!
	}

	@Override
	public X509Certificate[] getAcceptedIssuers()
	{
		return null;
	}
}