package com.sim2dial.dialer;

import static android.content.Intent.ACTION_MAIN;

import org.linphone.mediastream.Log;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import com.sim2dial.dialer.compatibility.Compatibility;
import com.sim2dial.dialer.tutorials.TutorialLauncherActivity;


public class LinphoneLauncherActivity extends Activity
{

	private Handler				mHandler;
	private ServiceWaitThread	mThread;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		new Log(getResources().getString(R.string.app_name), !getResources().getBoolean(R.bool.disable_every_log));

		// Hack to avoid to draw twice LinphoneActivity on tablets
		if (getResources().getBoolean(R.bool.isTablet))
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		else
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.launcher);

		mHandler = new Handler();

//		if (getResources().getBoolean(R.bool.enable_push_id))
//		{
//			Compatibility.initPushNotificationService(this);
//		}

	//	boolean useFirstLoginActivity = getResources().getBoolean(R.bool.use_first_login_activity);
		/*
		 * sp =
		 * PreferenceManager.getDefaultSharedPreferences(LinphoneLauncherActivity
		 * .this); u = sp.getString("loginid", ""); p = sp.getString("pass",
		 * "");
		 */
		if (LinphoneService.isReady())
		{
			onServiceReady();
		}
		else
		{
			// start linphone as background
			startService(new Intent(ACTION_MAIN).setClass(LinphoneLauncherActivity.this, LinphoneService.class));
			mThread = new ServiceWaitThread();
			mThread.start();
		}

		/*
		 * if (useFirstLoginActivity) { new GetLogin().execute(u, p); }
		 */

		// checkfirstlogin();
	}

	/*
	 * public void checkfirstlogin() { boolean useFirstLoginActivity =
	 * getResources().getBoolean(R.bool.use_first_login_activity);
	 * SharedPreferences pref =
	 * PreferenceManager.getDefaultSharedPreferences(this); if
	 * (useFirstLoginActivity &&
	 * !pref.getBoolean(getString(R.string.first_launch_suceeded_once_key),
	 * false)) { startActivity(new Intent(LinphoneLauncherActivity.this,
	 * SetupActivity.class)); finish(); // startActivityForResult(new
	 * Intent().setClass(this, // SetupActivity.class), FIRST_LOGIN_ACTIVITY); }
	 * else {
	 * 
	 * startActivity(new Intent(SetupActivity.this,SetupActivity.class));
	 * finish();
	 * 
	 * } }
	 */

	protected void onServiceReady()
	{
		final Class<? extends Activity> classToStart;
		if (getResources().getBoolean(R.bool.show_tutorials_instead_of_app))
		{
			classToStart = TutorialLauncherActivity.class;
		}
		else
		{
			classToStart = LinphoneActivity.class;
		}

		LinphoneService.instance().setActivityToLaunchOnIncomingReceived(classToStart);
		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				startActivity(new Intent().setClass(LinphoneLauncherActivity.this, classToStart).setData(getIntent().getData()));
				finish();
			}
		}, 1000);
	}

	private class ServiceWaitThread extends Thread
	{
		public void run()
		{
			while (!LinphoneService.isReady())
			{
				try
				{
					sleep(30);
				}
				catch (InterruptedException e)
				{
					throw new RuntimeException("waiting thread sleep() has been interrupted");
				}
			}

			mHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					onServiceReady();
				}
			});
			mThread = null;
		}
	}

	/*
	 * public class GetLogin extends AsyncTask<String, Void, String> {
	 * 
	 * // private HashMap<String, String> hmap=new HashMap<String, String>();
	 * ProgressDialog pb;
	 * 
	 * @Override protected void onPreExecute() { super.onPreExecute(); pb = new
	 * ProgressDialog(LinphoneLauncherActivity.this); // pb.show();
	 * pb.setContentView(new ProgressBar(LinphoneLauncherActivity.this), new
	 * LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	 * 
	 * }
	 * 
	 * @Override protected String doInBackground(String... params) { String url
	 * = LinphoneUtils.getLogin(params[0], params[1]); JSONObject jobj; try {
	 * jobj = new JSONObject(url); String result = jobj.getString("result");
	 * String sippassword = jobj.getString("sippassword"); String idclient =
	 * jobj.getString("idClient"); return result + "|" + sippassword + "|" +
	 * idclient; } catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * return null; }
	 * 
	 * @Override protected void onPostExecute(String result) {
	 * super.onPostExecute(result); // pb.dismiss(); if (result != null) {
	 * String res[] = result.split("[|]"); if (res.length > 1) { if (res[1] !=
	 * null) { String u1, p1; u1 = u; p1 = res[1]; sp.edit().putString(
	 * getString(R.string.pref_username_key), u1); sp.edit().putString(
	 * getString(R.string.pref_passwd_key), p1); sp.edit().putString("loginid",
	 * u1); sp.edit().putString("pass", p);
	 * 
	 * // ed.putString("country",country.getText().toString());
	 * sp.edit().commit();
	 * 
	 * if (LinphoneService.isReady()) { onServiceReady(); } else { // start
	 * linphone as background startService(new Intent(ACTION_MAIN).setClass(
	 * LinphoneLauncherActivity.this, LinphoneService.class)); mThread = new
	 * ServiceWaitThread(); mThread.start(); } //
	 * LinphoneUtils.getBalance(getActivity()); finish(); } } else {
	 * Toast.makeText(LinphoneLauncherActivity.this, "Authentication Failed",
	 * 20).show(); } } else { Toast.makeText(LinphoneLauncherActivity.this,
	 * "Data not found", 20).show(); } } }
	 */

}
