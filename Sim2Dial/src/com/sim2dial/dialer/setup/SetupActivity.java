package com.sim2dial.dialer.setup;

import org.json.JSONObject;
import org.linphone.mediastream.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sim2dial.dialer.Engine;
import com.sim2dial.dialer.LinphoneLauncherActivity;
import com.sim2dial.dialer.LinphoneManager;
import com.sim2dial.dialer.LinphoneUtils;
import com.sim2dial.dialer.R;

public class SetupActivity extends FragmentActivity implements OnClickListener
{
	private static SetupActivity	instance;
	private RelativeLayout			back, next, cancel;
	private SetupFragments			currentFragment;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (getResources().getBoolean(R.bool.isTablet) && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		setContentView(R.layout.setup);

		/*
		 * if (!LinphoneManager.isInstanciated()) {
		 * Log.e("No service running: avoid crash by starting the launcher",
		 * this.getClass().getName()); // super.onCreate called earlier
		 * finish(); startActivity(getIntent().setClass(this,
		 * LinphoneLauncherActivity.class)); return; }
		 * 
		 * 
		 * checkfirstlogin();
		 */

		boolean isHighBandwidthConnection = LinphoneUtils.isHightBandwidthConnection(this);
		if (findViewById(R.id.fragmentContainer) != null)
		{
			if (savedInstanceState == null)
			{
				if (isHighBandwidthConnection)
				{
					GenericLoginFragment welcomeFragment = new GenericLoginFragment();
					getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, welcomeFragment).commit();
					currentFragment = SetupFragments.GENERIC_LOGIN;
				}
				else
				{
					GenericLoginFragment welcomeFragment = new GenericLoginFragment();
					getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, welcomeFragment).commit();
					currentFragment = SetupFragments.GENERIC_LOGIN;
					Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
				}

			}
			else
			{
				currentFragment = (SetupFragments) savedInstanceState.getSerializable("CurrentFragment");
			}
		}

		initUI();
		instance = this;
	};

	public void checkfirstlogin()
	{
		boolean useFirstLoginActivity = getResources().getBoolean(R.bool.use_first_login_activity);

		if (!useFirstLoginActivity)
		{
			startActivity(new Intent(SetupActivity.this, LinphoneLauncherActivity.class));
			finish();
			// startActivityForResult(new Intent().setClass(this,
			// SetupActivity.class), FIRST_LOGIN_ACTIVITY);
		}
		else
		{
			/*
			 * startActivity(new
			 * Intent(SetupActivity.this,SetupActivity.class)); finish();
			 */
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putSerializable("CurrentFragment", currentFragment);
		super.onSaveInstanceState(outState);
	}

	public static SetupActivity instance()
	{
		return instance;
	}

	private void initUI()
	{
		/*
		 * back = (RelativeLayout) findViewById(R.id.setup_back);
		 * back.setOnClickListener(this); next = (RelativeLayout)
		 * findViewById(R.id.setup_next); next.setOnClickListener(this); cancel
		 * = (RelativeLayout) findViewById(R.id.setup_cancel);
		 * cancel.setOnClickListener(this);
		 * 
		 * next.setVisibility(View.GONE); back.setVisibility(View.GONE);
		 * cancel.setVisibility(View.GONE);
		 */

	}

	private void changeFragment(Fragment newFragment)
	{
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// transaction.addToBackStack("");
		transaction.replace(R.id.fragmentContainer, newFragment);

		transaction.commitAllowingStateLoss();
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();

		/*
		 * if (id == R.id.setup_cancel) { finish(); } else if (id ==
		 * R.id.setup_next) { if (currentFragment == SetupFragments.WELCOME) {
		 * MenuFragment fragment = new MenuFragment(); changeFragment(fragment);
		 * currentFragment = SetupFragments.MENU;
		 * 
		 * next.setVisibility(View.GONE); back.setVisibility(View.GONE);
		 * displayLoginGeneric(); } else if (currentFragment ==
		 * SetupFragments.WIZARD_CONFIRM) { finish(); } } else if (id ==
		 * R.id.setup_back) { handleBackEvent(); }
		 */
	}

	private void handleBackEvent()
	{
		if (currentFragment == SetupFragments.SHOW_STATES)
		{
			ShowCountry fragment = new ShowCountry();
			changeFragment(fragment);
			currentFragment = SetupFragments.SHOW_COUNTRY;

			/*
			 * next.setVisibility(View.GONE); back.setVisibility(View.GONE);
			 */
		}
		else if (currentFragment == SetupFragments.SHOW_COUNTRY)
		{
			GenericLoginFragment fragment = new GenericLoginFragment();
			changeFragment(fragment);
			currentFragment = SetupFragments.GENERIC_LOGIN;

			/*
			 * next.setVisibility(View.GONE); back.setVisibility(View.GONE);
			 */
		}
		if (currentFragment == SetupFragments.SHOW_CITIES)
		{
//			ShowStates fragment = new ShowStates();
//			changeFragment(fragment);
			currentFragment = SetupFragments.SHOW_STATES;

			/*
			 * next.setVisibility(View.GONE); back.setVisibility(View.GONE);
			 */
		}
		else if (currentFragment == SetupFragments.GENERIC_LOGIN)
		{
			finish();
		}

	}

	private void launchEchoCancellerCalibration(boolean sendEcCalibrationResult)
	{
		if (LinphoneManager.getLc().needsEchoCalibration() && !Engine.getPref().getBoolean(getString(R.string.first_launch_suceeded_once_key), false))
		{
			EchoCancellerCalibrationFragment fragment = new EchoCancellerCalibrationFragment();
			fragment.enableEcCalibrationResultSending(sendEcCalibrationResult);
			changeFragment(fragment);
			currentFragment = SetupFragments.ECHO_CANCELLER_CALIBRATION;
			/*
			 * back.setVisibility(View.VISIBLE); next.setVisibility(View.GONE);
			 * next.setEnabled(false); cancel.setEnabled(false);
			 */
		}
		else
		{
			success();
		}
	}

	private void logIn(String username, String password, String domain, boolean sendEcCalibrationResult)
	{
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && getCurrentFocus() != null)
		{
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}

		saveCreatedAccount(username, password, domain);

		LinphoneManager.getInstance().initializePayloads();

		try
		{
			LinphoneManager.getInstance().initFromConf();
		}
		catch (Throwable e)
		{
			Log.e(e, "Error while initializing from config in first login activity");
			Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
		}

		startActivity(new Intent(SetupActivity.this, LinphoneLauncherActivity.class));
		/*
		 * if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
		 * launchEchoCancellerCalibration(sendEcCalibrationResult); }
		 */
	}

	public void linphoneLogIn(String username, String password)
	{
		logIn(username, password, getString(R.string.default_domain), true);
	}

	public void genericLogIn(String username, String password, String domain)
	{
		logIn(username, password, domain, false);
	}

	private void writePreference(int key, String value)
	{
		Engine.getEditor().putString(getString(key), value).commit();
	}

	private void writePreference(String key, String value)
	{
		Engine.getEditor().putString(key, value).commit();
	}

	private void writePreference(int key, int value)
	{
		Engine.getEditor().putInt(getString(key), value).commit();
	}

	private void writePreference(int key, boolean value)
	{
		Engine.getEditor().putBoolean(getString(key), value).commit();
	}

	public void displayLoginGeneric()
	{
		GenericLoginFragment fragment = new GenericLoginFragment();
		changeFragment(fragment);
		currentFragment = SetupFragments.GENERIC_LOGIN;
	}

	public void displayLoginLinphone()
	{
		LinphoneLoginFragment fragment = new LinphoneLoginFragment();
		changeFragment(fragment);
		currentFragment = SetupFragments.LINPHONE_LOGIN;
	}

	public void displayWizard()
	{
		WizardFragment fragment = new WizardFragment();
		changeFragment(fragment);
		currentFragment = SetupFragments.WIZARD;
	}

	public void displayCountry(String clist)
	{
		Bundle args=new Bundle();
		args.putString("listcountry", clist);
		ShowCountry fragment = new ShowCountry();
		fragment.setArguments(args);
		changeFragment(fragment);
		currentFragment = SetupFragments.SHOW_COUNTRY;
	}

	public void displayStates()
	{
//		ShowStates fragment = new ShowStates();
//		changeFragment(fragment);
		currentFragment = SetupFragments.SHOW_STATES;
	}

	public void displaycities()
	{
		ShowCities fragment = new ShowCities();
		changeFragment(fragment);
		currentFragment = SetupFragments.SHOW_CITIES;

	}

	public void saveCreatedAccount(String username, String password, String domain)
	{
		int newAccountId = Engine.getPref().getInt(getString(R.string.pref_extra_accounts), 0);
		if (newAccountId == -1) newAccountId = 0;
		writePreference(R.string.pref_extra_accounts, newAccountId + 1);

		/*
		 * if (newAccountId == 0) {
		 */writePreference(R.string.pref_username_key, username);
		writePreference(R.string.pref_passwd_key, password);
		writePreference(R.string.pref_domain_key, domain);
		writePreference(R.bool.use_first_login_activity, true);

		Engine.getEditor().putBoolean(getString(R.string.first_launch_suceeded_once_key), true).commit();

		/*
		 * boolean isMainAccountLinphoneDotOrg =
		 * domain.equals(getString(R.string.default_domain)); if
		 * (!isMainAccountLinphoneDotOrg) { if
		 * (getResources().getBoolean(R.bool.
		 * disable_all_security_features_for_markets)) {
		 * writePreference(R.string.pref_proxy_key, domain + ":5228");
		 * writePreference(R.string.pref_transport_key,
		 * getString(R.string.pref_transport_tcp_key)); } else {
		 * writePreference(R.string.pref_proxy_key, domain + ":5223");
		 * writePreference(R.string.pref_transport_key,
		 * getString(R.string.pref_transport_tls_key)); }
		 * 
		 * writePreference(R.string.pref_expire_key, "604800"); // 3600*24*7
		 * writePreference(R.string.pref_enable_outbound_proxy_key, true);
		 * writePreference(R.string.pref_stun_server_key,
		 * getString(R.string.default_stun));
		 * writePreference(R.string.pref_ice_enable_key, true);
		 * writePreference(R.string.pref_push_notification_key, true); } } else
		 * { writePreference(getString(R.string.pref_username_key) +
		 * newAccountId, username);
		 * writePreference(getString(R.string.pref_passwd_key) + newAccountId,
		 * password); writePreference(getString(R.string.pref_domain_key) +
		 * newAccountId, domain); }
		 */
	}

	public void displayWizardConfirm(String username)
	{
		WizardConfirmFragment fragment = new WizardConfirmFragment();

		Bundle extras = new Bundle();
		extras.putString("Username", username);
		fragment.setArguments(extras);
		changeFragment(fragment);

		currentFragment = SetupFragments.WIZARD_CONFIRM;

		/*
		 * next.setVisibility(View.VISIBLE); next.setEnabled(false);
		 * back.setVisibility(View.GONE);
		 */
	}

	public void isAccountVerified()
	{
		Toast.makeText(this, getString(R.string.setup_account_validated), Toast.LENGTH_LONG).show();

		LinphoneManager.getInstance().initializePayloads();

		try
		{
			LinphoneManager.getInstance().initFromConf();
		}
		catch (Throwable e)
		{
			Log.e(e, "Error while initializing from config in first login activity");
			Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
		}

		launchEchoCancellerCalibration(true);
	}

	public void isEchoCalibrationFinished()
	{
		success();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			handleBackEvent();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void success()
	{
		writePreference(R.string.first_launch_suceeded_once_key, true);
		setResult(Activity.RESULT_OK);
		finish();
	}

}
