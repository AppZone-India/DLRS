package com.sim2dial.dialer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class WebService
{
	// members
	private ClientConnectionManager	clientConnectionManager;
	HttpClient								client;
	private HttpContext							context;
	private HttpParams							params;
	String													username, password;

	// DefaultHttpClient client;
	// constructor
	public WebService()
	{
		//setup();
	}

	// prepare for the https connection
	// call this in the constructor of the class that does the connection if
	// it's used multiple times
	/*private void setup()
	{
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		SchemeRegistry schemeRegistry = new SchemeRegistry();

		try
		{
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			CustomSSLSocketFactory sf = new CustomSSLSocketFactory(trustStore);
			sf.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);

			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", sf, 443));

		} catch (KeyManagementException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		params = new BasicHttpParams();
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(1));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		HttpConnectionParams.setSoTimeout(params, 10000);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "utf8");

		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		// set the user credentials for our site "example.com"
		// credentialsProvider.setCredentials(new AuthScope("example.com",
		// AuthScope.ANY_PORT),
		// new UsernamePasswordCredentials("UserNameHere", "UserPasswordHere"));
		credentialsProvider.setCredentials(new AuthScope("http://193.42.223.82", AuthScope.ANY_PORT), new UsernamePasswordCredentials("", ""));
		clientConnectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
		context = new BasicHttpContext();
		context.setAttribute("http.auth.credentials-provider", credentialsProvider);
		client = new DefaultHttpClient(clientConnectionManager, params);

		// Set verifier
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

	}*/

	// public HttpResponse getResponseFromUrl(String url) throws
	// ClientProtocolException, IOException{
	public String getResponseFromUrl(String url)
	{
		// connection (client has to be created for every new connection)
		//System.out.println(url);
		String line = "no response";
		HttpResponse response = null;
		try
		{
			HttpGet get = new HttpGet(url);
			client=(HttpClient) getNewHttpClient();
			response = client.execute(get, context);
			HttpEntity httpEntity = response.getEntity();
			int status_cd = response.getStatusLine().getStatusCode();
			String st_code = Integer.toString(status_cd);

			// line = EntityUtils.toString(httpEntity);
			if (status_cd != 200)
			{
				line = "";
				// System.out.println(st_code);
			} else
			{
				line = EntityUtils.toString(httpEntity);
				// System.out.println(st_code);
			}
			// line = st_code;
		} catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			System.out.println("ClientProtocolException: " + e.toString());
			line = "ERROR: " + "Invalid API or Invalid URL!";
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			System.out.println("Parse Exception: " + e.toString());
			line = "ERROR: " + "Invalid API or Wrong URL!";
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			System.out.println("UnsupportedEncodingException: " + e.toString());
			line = "ERROR: " + "Invalid API or Invalid URL!";
		} catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			System.out.println("MalformedURLException: " + e.toString());
			line = "ERROR: " + "Invalid API or Invalid URL!";
		} catch (SocketTimeoutException e)
		{
			// TODO Auto-generated catch block
			System.out.println("IOException: " + e.toString());
			line = "ERROR: " + "Socket Timeout!";
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			System.out.println("IOException: " + e.toString());
			line = "ERROR: " + "Invalid API or Invalid URL!";
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			System.out.println("Exception: " + e.toString());
			line = "ERROR: " + "Invalid API or Invalid URL!";
		}
		return line;
	}
	
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new CustomSSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}

}