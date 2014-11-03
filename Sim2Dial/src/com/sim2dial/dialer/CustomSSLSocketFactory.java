package com.sim2dial.dialer;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class CustomSSLSocketFactory extends org.apache.http.conn.ssl.SSLSocketFactory
{
	SSLContext sslContext = SSLContext.getInstance("TLS");
	
	 public CustomSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	        super(truststore);

	        TrustManager[] tm = new TrustManager[] { new FullX509TrustManager () };
	        /*{
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };*/

	        sslContext.init (null, tm, new SecureRandom ());
	    }

	 
	 @Override
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }

	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }
	
	 
//private SSLSocketFactory FACTORY = HttpsURLConnection.getDefaultSSLSocketFactory ();
/*
public CustomSSLSocketFactory () throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException
    {
    super(null);
    try
        {
//        SSLContext context = SSLContext.getInstance ("TLS");
        TrustManager[] tm = new TrustManager[] { new FullX509TrustManager () };
        context.init (null, tm, new SecureRandom ());

        FACTORY = context.getSocketFactory ();
        }
    catch (Exception e)
        {
        e.printStackTrace();
        }
    }

public Socket createSocket() throws IOException
{
    return FACTORY.createSocket();
}
*/


 // TODO: add other methods like createSocket() and getDefaultCipherSuites().
 // Hint: they all just make a call to member FACTORY 
}
