package com.sim2dial.dialer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;



@TargetApi(5)
public class Balance
{
	/*private static PreferencesProviderWrapper	prefsWrapper;

	public static String getCheckApi(Context ctx, String api) throws ParseException, UnsupportedEncodingException, ClientProtocolException, IOException, Exception
	{
		prefsWrapper = new PreferencesProviderWrapper(ctx);
		System.out.println("khalid Checking api");
		String url = api + "?action=checkapi";

		XMLParser parser = new XMLParser(url.split("/")[0].trim());
		String xml = parser.getXmlFromUrl(url);
		if (xml != null)
		{
			Document doc = parser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName("response");
			Element e = (Element) nl.item(0);
			System.out.println("khalid balance is " + parser.getValue(e, "status"));
			return parser.getValue(e, "status");
		}
		else return null;

	}

	public static HashMap<String, String> getLogin(Context ctx) throws ParseException, UnsupportedEncodingException, ClientProtocolException, IOException, Exception
	{
		prefsWrapper = new PreferencesProviderWrapper(ctx);
		return getLogin(ctx, prefsWrapper.getPreferenceStringValue("username"), prefsWrapper.getPreferenceStringValue("password"), prefsWrapper.getPreferenceStringValue("api"));
	}

	public static HashMap<String, String> getCredentials(Context ctx, String id, String pass, String uri) throws ParseException, UnsupportedEncodingException, ClientProtocolException, IOException,
			Exception
	{
		HashMap<String, String> hm = new HashMap<String, String>();
		XMLParser parser = new XMLParser(uri);
		String url = uri + "&username=" + id + "&password=" + pass;
		String xml = parser.getXmlFromUrl(url);
		
		 * <field1>7855344353</field1> <field2>7205487865</field2>
		 * <field3>billing2.novatel.com.au</field3>
		 
		if (xml != null)
		{
			Document doc = parser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName("result");
			Element e = (Element) nl.item(0);
			System.out.println("khalid username is " + parser.getValue(e, "field1"));
			// return parser.getValue(e, "status");
			hm.put("uname", parser.getValue(e, "field1"));
			hm.put("pswd", parser.getValue(e, "field2"));
			hm.put("server", parser.getValue(e, "field3"));
			return hm;

		}
		else return null;
	}*/

	public static HashMap<String, String> getLogin(Context ctx, String id, String pass, String api) throws ParseException, UnsupportedEncodingException, ClientProtocolException, IOException,
			Exception
	{
		HashMap<String, String> hm = new HashMap<String, String>();
		XMLParser parser = new XMLParser(api);
		String url = api + "?type=sip&action=login&username=" + id + "&password=" + pass;
		String xml = parser.getXmlFromUrl(url);
		if (xml != null)
		{
			Document doc = parser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName("response");
			Element e = (Element) nl.item(0);
			System.out.println("khalid balance is " + parser.getValue(e, "status"));
			// return parser.getValue(e, "status");
			hm.put("status", parser.getValue(e, "status"));
			hm.put("bal", parser.getValue(e, "bal"));
			return hm;

		}
		else return null;
	}

	/*
	 * public static HashMap<String, String> getFevContacts(Context ctx, String
	 * id, String pass, String api) throws ParseException,
	 * UnsupportedEncodingException, ClientProtocolException, IOException,
	 * Exception { HashMap<String, String> hm = new HashMap<String, String>();
	 * XMLParser parser = new XMLParser(api); String url = api +
	 * "?type=sip&action=login&username=" +
	 * id + "&password=" + pass; String xml = parser.getXmlFromUrl(url); if (xml
	 * != null) { Document doc = parser.getDomElement(xml); NodeList nl =
	 * doc.getElementsByTagName("response"); Element e = (Element) nl.item(0);
	 * System.out.println("khalid balance is " + parser.getValue(e, "status"));
	 * // return parser.getValue(e, "status"); hm.put("status",
	 * parser.getValue(e, "status")); hm.put("bal", parser.getValue(e, "bal"));
	 * return hm;
	 * 
	 * } else return null; }
	 * 
	 * 
	 * public static HashMap<String, String> getMsg(Context ctx, String path)
	 * throws ParseException, UnsupportedEncodingException,
	 * ClientProtocolException, IOException, Exception { HashMap<String, String>
	 * hm = new HashMap<String, String>();
	 * 
	 * System.out.println("khalid getting balance"); URL u = new URL(path);
	 * XMLParser parser = new XMLParser(u.getHost()); String url = path; String
	 * xml = parser.getXmlFromUrl(url); if (xml != null) { Document doc =
	 * parser.getDomElement(xml); NodeList nl =
	 * doc.getElementsByTagName("response"); Element e = (Element) nl.item(0);
	 * System.out.println("khalid balance is " + parser.getValue(e, "status"));
	 * hm.put("msg", parser.getValue(e, "text"));
	 * 
	 * return hm; } else return null; }
	 

	public static HashMap<String, String> getProfile(Context ctx) throws ParseException, UnsupportedEncodingException, ClientProtocolException, IOException, Exception
	{
		prefsWrapper = new PreferencesProviderWrapper(ctx);
		return getProfile(ctx, prefsWrapper.getPreferenceStringValue("username"), prefsWrapper.getPreferenceStringValue("password"), prefsWrapper.getPreferenceStringValue("api"));
	}

	public static HashMap<String, String> getProfile(Context ctx, String id, String pass, String api) throws ParseException, UnsupportedEncodingException, ClientProtocolException, IOException,
			Exception
	{
		HashMap<String, String> hm = new HashMap<String, String>();
		System.out.println("khalid getting balance");
		URL u = new URL(api);
		XMLParser parser = new XMLParser(u.getHost());
		String url = api + "?action=profile&username=" + id + "&password=" + pass + "&type=sip";
		String xml = parser.getXmlFromUrl(url);
		if (xml != null)
		{
			Document doc = parser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName("response");

			Element e = (Element) nl.item(0);
			hm.put("bal", parser.getValue(e, "bal"));
			hm.put("cur", parser.getValue(e, "cur"));
			hm.put("acc", parser.getValue(e, "acc"));
			hm.put("fn", parser.getValue(e, "fname"));
			hm.put("ln", parser.getValue(e, "lname"));

			return hm;
		}
		else return null;
	}

	
	 * 
	 * public static HashMap<String, String> getgetAdminMsg(Context ctx,String
	 * api) throws ParseException, UnsupportedEncodingException,
	 * ClientProtocolException, IOException, Exception { HashMap<String, String>
	 * hm = new HashMap<String, String>();
	 * System.out.println("khalid getting balance"); URL u=new URL(api);
	 * XMLParser parser = new XMLParser(u.getHost()); String url = api; String
	 * xml = parser.getXmlFromUrl(url); if(xml!=null){ Document doc =
	 * parser.getDomElement(xml); NodeList nl =
	 * doc.getElementsByTagName("response");
	 * 
	 * Element e = (Element) nl.item(0); hm.put("bal", parser.getValue(e,
	 * "bal")); hm.put("cur", parser.getValue(e, "cur")); hm.put("acc",
	 * parser.getValue(e, "acc")); hm.put("fn", parser.getValue(e, "fname"));
	 * hm.put("ln", parser.getValue(e, "lname"));
	 * 
	 * return hm; }else return null; }
	 
*/
	/*public static String getCallBack(Context ctx, String num1, String num2) throws ParseException, UnsupportedEncodingException, ClientProtocolException, IOException, Exception
	{
		prefsWrapper = new PreferencesProviderWrapper(ctx);
		return getCallBack(ctx, prefsWrapper.getPreferenceStringValue("username"), prefsWrapper.getPreferenceStringValue("password"), num1, num2, prefsWrapper.getPreferenceStringValue("api"));

	}*/
/*
	public static class CallDetails
	{

		private String	id;
		private String	name;
		private String	number;
		private int		newnum;
		private int		callType;
		private int		numType;

		private int		duration;
		private long	date;
		private String	numberlable;
		private int		acc_id;
		private int		statusCode;
		private String	status;

		public ContentValues getContentValues()
		{
			ContentValues cv = new ContentValues();
			cv.put(CallLog.Calls.CACHED_NUMBER_TYPE, numType);
			cv.put(CallLog.Calls.CACHED_NUMBER_LABEL, numberlable);
			cv.put(CallLog.Calls.CACHED_NAME, name);
			cv.put(CallLog.Calls.DURATION, duration);
			cv.put(CallLog.Calls.NEW, newnum);
			cv.put(CallLog.Calls.DATE, date);
			cv.put(CallLog.Calls.TYPE, callType);
			cv.put(CallLog.Calls.NUMBER, number);
			cv.put(SipManager.CALLLOG_PROFILE_ID_FIELD, acc_id);
			cv.put(SipManager.CALLLOG_STATUS_CODE_FIELD, statusCode);
			cv.put(SipManager.CALLLOG_STATUS_TEXT_FIELD, status);
			return cv;

		}
	}

	public static CallDetails getContactID(Context ctx, String number)
	{
		ContentResolver contentResolver = ctx.getContentResolver();
		CallDetails cdcd = null;
		HashMap<String, CallDetails> hm = new HashMap<String, CallDetails>();
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		Cursor cursor = contentResolver.query(uri, new String[]
		{ PhoneLookup.DISPLAY_NAME, PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.TYPE, PhoneLookup.LABEL }, null, null, null);

		if (cursor != null && cursor.getCount() != 0)
		{
			if (!cursor.isFirst()) cursor.moveToFirst();
			do
			{
				CallDetails cd = new CallDetails();
				try
				{
					cd.numberlable = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup.LABEL));
				}
				catch (Exception e)
				{
				}
				try
				{
					cd.numType = cursor.getInt(cursor.getColumnIndexOrThrow(PhoneLookup.TYPE));
				}
				catch (Exception e)
				{
				}
				try
				{
					cd.name = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
				}
				catch (Exception e)
				{
				}
				try
				{
					cd.number = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup.NUMBER));
				}
				catch (Exception e)
				{
				}
				try
				{
					if (cd.number.startsWith("+")) cd.number = cd.number.substring(1);
					cd.id = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup._ID));
					cd.newnum = 0;
				}
				catch (Exception e)
				{
				}

				cdcd = cd;
				hm.put(cd.number, cd);

			}
			while (cursor.moveToNext());
			cursor.close();
		}
		else
		{
			cdcd = new CallDetails();
			cdcd.name = "";
			cdcd.number = number;
			cdcd.newnum = 1;

		}
		if (number.startsWith("+")) number = number.substring(1);

		if (hm.containsKey(number)) return hm.get(number);
		else return cdcd;

	}*/

	/*private static String getCallBack(Context ctx, String id, String pass, String num1, String num2, String api) throws ParseException, UnsupportedEncodingException, ClientProtocolException,
			IOException, Exception
	{
		CallDetails cd = getContactID(ctx, num2);
		HashMap<String, String> hm = new HashMap<String, String>();
		System.out.println("khalid getting callback");
		URL u = new URL(api);
		XMLParser parser = new XMLParser(u.getHost());
		String url = api + "?action=callback&username=" + id + "&password=" + pass + "&hash=" + MD5("myclick2call" + id) + "&num1=" + num1 + "&num2=" + num2
				+ "&type=sip";
		String xml = parser.getXmlFromUrl(url);
		
		 * Document doc = parser.getDomElement(xml); NodeList nl =
		 * doc.getElementsByTagName("response"); Element e = (Element)
		 * nl.item(0);
		 
		
		 * hm.put("error", parser.getValue(e, "error")); hm.put("newbal",
		 * parser.getValue(e, "newbal"));
		 
		if (xml != null)
		{
			String temp[] = xml.split(":");
			if (temp[0].trim().equals("Error"))
			{
				cd.statusCode = 00;
				cd.callType = 4;
			}
			else if (temp[0].equals("Success"))
			{
				cd.statusCode = 11;
				cd.callType = 5;
			}
			else
			{
				cd.statusCode = 00;
				cd.callType = 4;
			}
			cd.status = temp[1];
			cd.acc_id = 1;
			cd.date = System.currentTimeMillis();
			cd.duration = 0;
		}
		ctx.getContentResolver().insert(SipManager.CALLLOG_URI, cd.getContentValues());
		return xml;
		 else return null; 

	}*/

	/*public static HashMap<String, String> getRefill(Context ctx, String vid) throws ParseException, UnsupportedEncodingException, ClientProtocolException, IOException, Exception
	{
		prefsWrapper = new PreferencesProviderWrapper(ctx);
		return getRefill(ctx, prefsWrapper.getPreferenceStringValue("username"), prefsWrapper.getPreferenceStringValue("password"), vid, prefsWrapper.getPreferenceStringValue("api"));

	}*/

	public static String MD5(String input) throws NoSuchAlgorithmException
	{
		String result = input;
		if (input != null)
		{
			MessageDigest md = MessageDigest.getInstance("MD5"); // or "SHA-1"
			md.update(input.getBytes());
			BigInteger hash = new BigInteger(1, md.digest());
			result = hash.toString(16);
			while (result.length() < 32)
			{
				result = "0" + result;
			}
		}
		return result;
	}

	/*private static HashMap<String, String> getRefill(Context ctx, String id, String pass, String vid, String api) throws ParseException, UnsupportedEncodingException, ClientProtocolException,
			IOException, Exception
	{
		HashMap<String, String> hm = new HashMap<String, String>();
		System.out.println("khalid getting balance");
		URL u = new URL(api);
		XMLParser parser = new XMLParser(u.getHost());
		String url = api + "?action=voucher-refill&username=" + id + "&password=" + pass + "&hash=" + MD5("MYaNdroid_rEfill76453" + vid) + "&voucher=" + vid
				+ "&type=sip";
		String xml = parser.getXmlFromUrl(url);
		if (xml != null)
		{
			Document doc = parser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName("response");
			Element e = (Element) nl.item(0);
			hm.put("error", parser.getValue(e, "error"));
			hm.put("newbal", parser.getValue(e, "newbal"));
			return hm;
		}
		else return null;

	}*/

	public static HashMap<String, String> getRates(Context ctx, String num) throws ParseException, Exception
	{
		//prefsWrapper = new PreferencesProviderWrapper(ctx);
		return null;//getRates(ctx, prefsWrapper.getPreferenceStringValue("username"), prefsWrapper.getPreferenceStringValue("password"), num, prefsWrapper.getPreferenceStringValue("api"));
	}

	public static HashMap<String, String> getRates(Context ctx, String id, String pass, String num, String api) throws ParseException, Exception
	{

		HashMap<String, String> hm = new HashMap<String, String>();
		System.out.println("khalid getting balance");
		URL u = new URL(api);
		XMLParser parser = new XMLParser(u.getHost());
		String url = api + "?action=rates&username=" + id + "&password=" + pass + "&number=" + num + "&type=sip";
	
		String xml = parser.getXmlFromUrl(url);
		if (xml != null)
		{
			Document doc = parser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName("response");
			Element e = (Element) nl.item(0);
			System.out.println("khalid balance is " + parser.getValue(e, "status"));
			if (!parser.getValue(e, "status").equals("-1"))
			{
				hm.put("sel", parser.getValue(e, "Sell"));
				hm.put("des", parser.getValue(e, "Destination"));
				hm.put("dp", parser.getValue(e, "Dialprefix"));
				hm.put("cur", parser.getValue(e, "Currency"));
				return hm;
			}
			else
			{
				return null;
			}
		}
		else return null;

	}

	/*public static ArrayList<HashMap<String, String>> getRefillHistory(Context ctx) throws ParseException, UnsupportedEncodingException, ClientProtocolException, IOException, Exception
	{
		prefsWrapper = new PreferencesProviderWrapper(ctx);
		return getRefillHistory(ctx, prefsWrapper.getPreferenceStringValue("username"), prefsWrapper.getPreferenceStringValue("password"), prefsWrapper.getPreferenceStringValue("api"));
	}

	public static ArrayList<HashMap<String, String>> getRefillHistory(Context ctx, String id, String pass, String api) throws ParseException, UnsupportedEncodingException, ClientProtocolException,
			IOException, Exception
	{
		// http://64.235.55.22/androida2billing/index.php
		ArrayList<HashMap<String, String>> arr = new ArrayList<HashMap<String, String>>();
		System.out.println("khalid getting balance");
		URL u = new URL(api);
		XMLParser parser = new XMLParser(u.getHost());
		String url = api + "?action=refill-history&username=" + id + "&password=" + pass + "&type=sip";
		String xml = parser.getXmlFromUrl(url);
		if (xml != null)
		{
			Document doc = parser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName("response");
			HashMap<String, String> hd = new HashMap<String, String>();
			hd.put("dt", "Date");
			hd.put("vo", "Voucher");
			hd.put("cr", "Credit");
			arr.add(hd);
			for (int i = 0; i < nl.getLength(); i++)
			{
				HashMap<String, String> hm = new HashMap<String, String>();
				Element e = (Element) nl.item(i);
				hm.put("dt", parser.getValue(e, "Date"));
				hm.put("vo", parser.getValue(e, "Voucher"));
				hm.put("cr", parser.getValue(e, "Credit"));
				arr.add(hm);
			}

			return arr;
		}
		else return null;

	}

	*//**
	 * @throws Exception
	 ******************************************************************************************************************************//*
	public static String getBalancefromURL(Context ctx) throws Exception
	{
		BufferedReader reader = null;
		prefsWrapper = new PreferencesProviderWrapper(ctx);
		String url = "http://balance.salaamcall.com/balsalaam/balance.aspx?user=" + prefsWrapper.getPreferenceStringValue("username");

		reader = read(url);
		return reader.readLine();

	}
*/
	public static BufferedReader read(String url) throws Exception
	{
		return new BufferedReader(new InputStreamReader(new URL(url).openStream()));
	}
}
