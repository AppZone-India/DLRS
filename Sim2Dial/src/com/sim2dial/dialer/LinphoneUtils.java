/*
	SoftVolume.java
Copyright (C) 2011  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.sim2dial.dialer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCore;
import org.linphone.mediastream.Log;
import org.linphone.mediastream.Version;
import org.linphone.mediastream.video.capture.hwconf.Hacks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * Helpers.
 * 
 * @author Guillaume Beraudo
 * 
 */
public final class LinphoneUtils
{

	private LinphoneUtils()
	{
	}

	private static boolean		preventVolumeBarToDisplay	= false;
	private static final String	sipAddressRegExp			= "^(sip:)?(\\+)?[a-z0-9]+([_\\.-][a-z0-9]+)*@([a-z0-9]+([\\.-][a-z0-9]+)*)+\\.[a-z]{2,}(:[0-9]{2,5})?$";
	private static final String	strictSipAddressRegExp		= "^sip:(\\+)?[a-z0-9]+([_\\.-][a-z0-9]+)*@([a-z0-9]+([\\.-][a-z0-9]+)*)+\\.[a-z]{2,}$";

	public static boolean isSipAddress(String numberOrAddress)
	{
		Pattern p = Pattern.compile(sipAddressRegExp);
		Matcher m = p.matcher(numberOrAddress);
		return m != null && m.matches();
	}

	public static boolean isStrictSipAddress(String numberOrAddress)
	{
		Pattern p = Pattern.compile(strictSipAddressRegExp);
		Matcher m = p.matcher(numberOrAddress);
		return m != null && m.matches();
	}

	public static String getUsernameFromAddress(String address)
	{
		if (address.contains("sip:")) address = address.replace("sip:", "");

		if (address.contains("@")) address = address.split("@")[0];

		if (address.contains("%23")) address = address.replace("%23", "#");

		return address;
	}

	public static boolean onKeyBackGoHome(Activity activity, int keyCode, KeyEvent event)
	{
		if (!(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) { return false; // continue
		}

		activity.startActivity(new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
		return true;
	}

	public static boolean onKeyVolumeAdjust(int keyCode)
	{
		if (!((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) && (Hacks.needSoftvolume()) || Build.VERSION.SDK_INT >= 15)) { return false; // continue
		}

		if (!LinphoneService.isReady())
		{
			Log.i("Couldn't change softvolume has service is not running");
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
		{
			LinphoneManager.getInstance().adjustVolume(1);
		}
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
		{
			LinphoneManager.getInstance().adjustVolume(-1);
		}
		return preventVolumeBarToDisplay;
	}

	/**
	 * @param contact
	 *            sip uri
	 * @return url/uri of the resource
	 */
	// public static Uri
	// findUriPictureOfContactAndSetDisplayName(LinphoneAddress address,
	// ContentResolver resolver) {
	// return Compatibility.findUriPictureOfContactAndSetDisplayName(address,
	// resolver);
	// }

	public static Uri findUriPictureOfContactAndSetDisplayName(LinphoneAddress address, ContentResolver resolver)
	{
		ContactHelper helper = new ContactHelper(address, resolver);
		helper.query();
		return helper.getUri();
	}

	public static Bitmap downloadBitmap(Uri uri)
	{
		URL url;
		InputStream is = null;
		try
		{
			url = new URL(uri.toString());
			is = url.openStream();
			return BitmapFactory.decodeStream(is);
		}
		catch (MalformedURLException e)
		{
			Log.e(e, e.getMessage());
		}
		catch (IOException e)
		{
			Log.e(e, e.getMessage());
		}
		finally
		{
			try
			{
				is.close();
			}
			catch (IOException x)
			{
			}
		}
		return null;
	}

	public static void setImagePictureFromUri(Context c, ImageView view, Uri uri, int notFoundResource)
	{
		if (uri == null)
		{
			view.setImageResource(notFoundResource);
			return;
		}
		if (uri.getScheme().startsWith("http"))
		{
			Bitmap bm = downloadBitmap(uri);
			if (bm == null) view.setImageResource(notFoundResource);
			view.setImageBitmap(bm);
		}
		else
		{
			if (Version.sdkAboveOrEqual(Version.API06_ECLAIR_201))
			{
				view.setImageURI(uri);
			}
			else
			{
				@SuppressWarnings("deprecation")
				Bitmap bitmap = android.provider.Contacts.People.loadContactPhoto(c, uri, notFoundResource, null);
				view.setImageBitmap(bitmap);
			}
		}
	}

	public static final List<LinphoneCall> getLinphoneCallsNotInConf(LinphoneCore lc)
	{
		List<LinphoneCall> l = new ArrayList<LinphoneCall>();
		for (LinphoneCall c : lc.getCalls())
		{
			if (!c.isInConference())
			{
				l.add(c);
			}
		}
		return l;
	}

	public static final List<LinphoneCall> getLinphoneCallsInConf(LinphoneCore lc)
	{
		List<LinphoneCall> l = new ArrayList<LinphoneCall>();
		for (LinphoneCall c : lc.getCalls())
		{
			if (c.isInConference())
			{
				l.add(c);
			}
		}
		return l;
	}

	public static final List<LinphoneCall> getLinphoneCalls(LinphoneCore lc)
	{
		// return a modifiable list
		return new ArrayList<LinphoneCall>(Arrays.asList(lc.getCalls()));
	}

	public static final boolean hasExistingResumeableCall(LinphoneCore lc)
	{
		for (LinphoneCall c : getLinphoneCalls(lc))
		{
			if (c.getState() == State.Paused) { return true; }
		}
		return false;
	}

	public static final List<LinphoneCall> getCallsInState(LinphoneCore lc, Collection<State> states)
	{
		List<LinphoneCall> foundCalls = new ArrayList<LinphoneCall>();
		for (LinphoneCall call : getLinphoneCalls(lc))
		{
			if (states.contains(call.getState()))
			{
				foundCalls.add(call);
			}
		}
		return foundCalls;
	}

	public static final List<LinphoneCall> getRunningOrPausedCalls(LinphoneCore lc)
	{
		return getCallsInState(lc, Arrays.asList(State.Paused, State.PausedByRemote, State.StreamsRunning));
	}

	public static final int countConferenceCalls(LinphoneCore lc)
	{
		int count = lc.getConferenceSize();
		if (lc.isInConference()) count--;
		return count;
	}

	public static int countVirtualCalls(LinphoneCore lc)
	{
		return lc.getCallsNb() - countConferenceCalls(lc);
	}

	public static int countNonConferenceCalls(LinphoneCore lc)
	{
		return lc.getCallsNb() - countConferenceCalls(lc);
	}

	public static void setVisibility(View v, int id, boolean visible)
	{
		v.findViewById(id).setVisibility(visible ? VISIBLE : GONE);
	}

	public static void setVisibility(View v, boolean visible)
	{
		v.setVisibility(visible ? VISIBLE : GONE);
	}

	public static void enableView(View root, int id, OnClickListener l, boolean enable)
	{
		View v = root.findViewById(id);
		v.setVisibility(enable ? VISIBLE : GONE);
		v.setOnClickListener(l);
	}

	public static int pixelsToDpi(Resources res, int pixels)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) pixels, res.getDisplayMetrics());
	}

	public static boolean isCallRunning(LinphoneCall call)
	{
		if (call == null) { return false; }

		LinphoneCall.State state = call.getState();

		return state == LinphoneCall.State.Connected || state == LinphoneCall.State.CallUpdating || state == LinphoneCall.State.CallUpdatedByRemote || state == LinphoneCall.State.StreamsRunning
				|| state == LinphoneCall.State.Resuming;
	}

	public static boolean isCallEstablished(LinphoneCall call)
	{
		if (call == null) { return false; }

		LinphoneCall.State state = call.getState();

		return isCallRunning(call) || state == LinphoneCall.State.Paused || state == LinphoneCall.State.PausedByRemote || state == LinphoneCall.State.Pausing;
	}

	public static boolean isHightBandwidthConnection(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
	}

	private static boolean isConnectionFast(int type, int subType)
	{
		if (type == ConnectivityManager.TYPE_WIFI)
		{
			return true;
		}
		else if (type == ConnectivityManager.TYPE_MOBILE)
		{
			switch (subType)
			{
				case TelephonyManager.NETWORK_TYPE_1xRTT:
					return false; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_CDMA:
					return false; // ~ 14-64 kbps
				case TelephonyManager.NETWORK_TYPE_EDGE:
					return false; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_GPRS:
					return false; // ~ 100 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
					return false; // ~25 kbps
				case TelephonyManager.NETWORK_TYPE_LTE:
					return true; // ~ 400-1000 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
					return true; // ~ 600-1400 kbps
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					return true; // ~ 2-14 Mbps
				case TelephonyManager.NETWORK_TYPE_HSPA:
					return true; // ~ 700-1700 kbps
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					return true; // ~ 1-23 Mbps
				case TelephonyManager.NETWORK_TYPE_UMTS:
					return true; // ~ 400-7000 kbps
				case TelephonyManager.NETWORK_TYPE_EHRPD:
					return true; // ~ 1-2 Mbps
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
					return true; // ~ 5 Mbps
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					return true; // ~ 10-20 Mbps
				case TelephonyManager.NETWORK_TYPE_IDEN:
					return true; // ~ 10+ Mbps
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				default:
					return false;
			}
		}
		else
		{
			return false;
		}
	}

	public static void showAlertOkMessage(Context ctxt, String msg, String title)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(ctxt);
		build.setTitle(Html.fromHtml("<font color='#ffffff'>" + title + "</font>"));
		build.setMessage(msg);
		build.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub

			}
		});
		AlertDialog alert = build.create();
		alert.show();
	}

	/*public static void getBalance(Context ctxt)
	{
		new getBal(ctxt).execute(LinphoneUtils.getUsername(ctxt), LinphoneUtils.getPass(ctxt), "sip");
	}*/

	public static String getUsername(Context ctxt)
	{

		String keyUsername = ctxt.getString(R.string.pref_username_key);
		String username = Engine.getPref().getString(keyUsername, "");
		return username;
	}

	public static String getPass(Context ctxt)
	{

		String keypass = ctxt.getString(R.string.pref_passwd_key);
		String pass = Engine.getPref().getString(keypass, "");
		return pass;
	}

	private static WebService	ws			= new WebService();
	private static String		response;
	public static String		SIP_SERVER	= "mobisip.ringtoindia.com";
	public static String		API_URL		= "https://www.mycallhistory.com/vportal/API/SIM2DIAL/";
	
	public static String hitBalance(String user, String pass, String sip)
	{
		final String url = API_URL + "getBalance?user=" + user + "&clientID=SIM2duk";
		/*
		 * StringBuilder str=new StringBuilder(); try { URL hit=new URL(url);
		 * InputStream is=hit.openConnection().getInputStream(); BufferedReader
		 * bread=new BufferedReader(new InputStreamReader(is)); String line;
		 * while((line=bread.readLine())!=null){ str.append(line); }
		 * 
		 * } catch (MalformedURLException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		response = ws.getResponseFromUrl(url);

		return response;
	}

	// getCallHistory?clientID=SIM2duk&account=4&fromDate=09/01/2012&toDate=11/01/2013&clientType=32
	public static String getCallHistory(String frm, String to, String id)
	{
		final String url = API_URL + "getCallHistoryTopN?clientID=SIM2duk&account=" + id + "&clientType=32";
		response = ws.getResponseFromUrl(url);
		return response;
	}

	// mycallhistory.net/r2in/getTopupHistory?clientID=SIM2duk&account=3&fromDate=09/01/2011&toDate=11/01/2013&clientType=32
	public static String getCreditHistory(String frm, String to, String id)
	{
		final String url = API_URL + "getTopupHistoryTopN?clientID=SIM2duk&account=" + id + "&clientType=32";
		response = ws.getResponseFromUrl(url);
		return response;
	}

	// https://mycallhistory.net/r2in/getCountries?clientID=SIM2duk
	public static String getCountry()
	{
		final String url = API_URL + "getCountries?clientID=SIM2duk";
		response = ws.getResponseFromUrl(url);
		return response;
	}

	public static String getStates(String ctry)
	{
		final String url = API_URL + "getListOfStates?clientID=SIM2duk&country=" + ctry;
		response = ws.getResponseFromUrl(url);
		return response;
	}

	public static String getCities(String ctry, String state)
	{
		final String url = API_URL + "getListOfCity?clientID=SIM2duk&country=" + ctry + "&state=" + state;
		response = ws.getResponseFromUrl(url);
		return response;
	}

	public static String getDDI(String ctry, String state, String city, String id, String destin)
	{
		final String url = API_URL + "getDDI?clientID=SIM2duk&country=" + ctry + "&state=" + state + "&city=" + city + "&clientType=32&idClient=" + id + "&destination=" + destin;
		response = ws.getResponseFromUrl(url);
		return response;
	}

	public static String getRates(String user, String pass, String num)
	{
		String url = API_URL + "rates&username=" + user + "&password=" + pass + "&number=" + num + "&type=sip";

		response = ws.getResponseFromUrl(url);

		return response;
	}

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

	public static String getRefill(String user, String pass, String vid)
	{
		String url;
		try
		{
			url = API_URL + "voucher-refill&username=" + user + "&password=" + pass + "&hash=" + MD5("MYaNdroid_rEfill76453" + vid) + "&voucher=" + vid;
			response = ws.getResponseFromUrl(url);
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	public static String getRefillHistory(String user, String pass)
	{
		String url;

		url = "http://server.nextstag.com/androida2billing/index.php?action=" + "refill-history&username=217896&password=755844&type=sip";
		response = ws.getResponseFromUrl(url);

		return response;
	}

	public static String getCallBack(String user, String pass, String num1, String num2) throws NoSuchAlgorithmException
	{
		String url;

		url = API_URL + "callback&username=" + user + "&password=" + pass + "&hash=" + MD5("myclick2call" + user) + "&num1=" + num1 + "&num2=" + num2 + "&type=sip";
		response = ws.getResponseFromUrl(url);

		return response;
	}

	/*public static String getLogin(String user, String pass)
	{
		String url;
		url = API_URL + "login_api.php?user=" + user + "&password=" + pass + "&aid=" + Engine.getPref().getString("aid", "");

		response = ws.getResponseFromUrl(url);

		return response;
	}*/

	public static String getCallLogduration(Context ctxt, String num)
	{
		StringBuffer sb = new StringBuffer();
		Uri contacts = CallLog.Calls.CONTENT_URI;
		Cursor managedCursor = ctxt.getContentResolver().query(contacts, null, CallLog.Calls.NUMBER + "=?", new String[] { num }, null);
		if (managedCursor != null)
		{
			int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
			int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
			int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
			int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
			// sb.append("Call Details :");
			while (managedCursor.moveToNext())
			{

				String phNumber = managedCursor.getString(number);
				String callType = managedCursor.getString(type);
				String callDate = managedCursor.getString(date);
				String callDayTime = new Date(Long.valueOf(callDate)).toString();
				// long timestamp = convertDateToTimestamp(callDayTime);
				String callDuration = managedCursor.getString(duration);
				String dir = null;
				int dircode = Integer.parseInt(callType);
				switch (dircode)
				{
					case CallLog.Calls.OUTGOING_TYPE:
						dir = "OUTGOING";
					break;

					case CallLog.Calls.INCOMING_TYPE:
						dir = "INCOMING";
					break;

					case CallLog.Calls.MISSED_TYPE:
						dir = "MISSED";
					break;
				}
				/*
				 * sb.append("\nPhone Number:--- " + phNumber +
				 * " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime
				 * + " \nCall duration in sec :--- " + callDuration);
				 * sb.append("\n----------------------------------");
				 */
				sb.append(callDuration);
			}
			managedCursor.close();
		}
		// System.out.println(sb);
		return sb.toString();
	}

}
