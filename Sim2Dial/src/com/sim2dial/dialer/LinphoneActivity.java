package com.sim2dial.dialer;

/*
 * LinphoneActivity.java Copyright (C) 2012 Belledonne Communications, Grenoble,
 * France
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
import static android.content.Intent.ACTION_MAIN;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCallLog.CallStatus;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.OnlineStatus;
import org.linphone.mediastream.Log;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sim2dial.dialer.LinphoneManager.AddressType;
import com.sim2dial.dialer.LinphoneManager.LinphoneConfigException;
import com.sim2dial.dialer.LinphoneSimpleListener.LinphoneOnCallStateChangedListener;
import com.sim2dial.dialer.LinphoneSimpleListener.LinphoneOnRegistrationStateChangedListener;
import com.sim2dial.dialer.compatibility.Compatibility;
import com.sim2dial.dialer.db.HistoryStatus;
import com.sim2dial.dialer.db.MyContentProvider;
import com.sim2dial.dialer.setup.GenericLoginFragment;
import com.sim2dial.dialer.setup.SetupActivity;
import com.sim2dial.dialer.ui.AddressText;
import com.sim2dial.dialer.util.Theme;

public class LinphoneActivity extends FragmentActivity implements OnClickListener, ContactPicked, LinphoneOnCallStateChangedListener, /*
																																	 * LinphoneOnMessageReceivedListener
																																	 * ,
																																	 */
LinphoneOnRegistrationStateChangedListener, LoaderManager.LoaderCallbacks<Cursor>
{
	public static final String			PREF_FIRST_LAUNCH		= "pref_first_launch";
	private static final int			SETTINGS_ACTIVITY		= 123;
	private static final int			FIRST_LOGIN_ACTIVITY	= 101;
	private static final int			callActivity			= 19;

	public static MyHSimpleCAdapter		adap;
	private static LinphoneActivity		instance;

	private StatusFragment				statusFragment;
	// private TextView missedCalls, missedChats;
	public ImageView					dialer;
	private LinearLayout				menu/*, mark;*/;
	private ImageView					contacts, history, settings, credit, aboutChat, aboutSettings;
	private FragmentsAvailable			currentFragment, nextFragment;
	private Fragment					dialerFragment, messageListenerFragment, messageListFragment, friendStatusListenerFragment;
	private SavedState					dialerSavedState;
	private ChatStorage					chatStorage;
	private boolean						preferLinphoneContacts	= false, isAnimationDisabled = false, isContactPresenceDisabled = true;
	private Handler						mHandler				= new Handler();
	private List<Contact>				contactList, sipContactList;
	private Cursor						contactCursor, sipContactCursor;
	private OrientationEventListener	mOrientationHelper;
	String[]							projection				= { HistoryStatus.Column_ID, HistoryStatus.Column_Name, HistoryStatus.Column_Date, HistoryStatus.Column_Duration,
			HistoryStatus.Column_No, HistoryStatus.Column_Status, HistoryStatus.Column_Time, HistoryStatus.Column_Timestamp };
	Timer								timer;

	static final boolean isInstanciated()
	{
		return instance != null;
	}

	public static final LinphoneActivity instance()
	{
		if (instance != null) return instance;
		throw new RuntimeException("LinphoneActivity not instantiated yet");
	}

	private void applyTheme()
	{
		contacts.setImageDrawable(Theme.selectorDrawable("ic_profile"));
		history.setImageDrawable(Theme.selectorDrawable("ic_recent"));
		dialer.setImageDrawable(Theme.selectorDrawable("ic_dialpad"));
		credit.setImageDrawable(Theme.selectorDrawable("ic_credit"));
		settings.setImageDrawable(Theme.selectorDrawable("ic_navsetting"));

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if (isTablet() && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		if (!LinphoneManager.isInstanciated())
		{
			Log.e("No service running: avoid crash by starting the launcher", this.getClass().getName());
			finish();
			startActivity(getIntent().setClass(this, LinphoneLauncherActivity.class));
			return;
		}

		boolean useFirstLoginActivity = getResources().getBoolean(R.bool.use_first_login_activity);

		if (useFirstLoginActivity && !Engine.getPref().getBoolean(getString(R.string.first_launch_suceeded_once_key), false))
		{
			startActivity(new Intent(LinphoneActivity.this, SetupActivity.class));
			startActivityForResult(new Intent().setClass(this, SetupActivity.class), FIRST_LOGIN_ACTIVITY);
			finish();
		}
		else
		{
			/*
			 * startActivity(new
			 * Intent(SetupActivity.this,SetupActivity.class)); finish();
			 */
		}

		/*
		 * boolean useFirstLoginActivity =
		 * getResources().getBoolean(R.bool.use_first_login_activity);
		 * SharedPreferences pref =
		 * PreferenceManager.getDefaultSharedPreferences(this); if
		 * (useFirstLoginActivity &&
		 * !pref.getBoolean(getString(R.string.first_launch_suceeded_once_key),
		 * false)) { if (pref.getInt(getString(R.string.pref_extra_accounts),
		 * -1) > -1) {
		 * pref.edit().putBoolean(getString(R.string.first_launch_suceeded_once_key
		 * ), true); } else { startActivityForResult(new Intent().setClass(this,
		 * SetupActivity.class), FIRST_LOGIN_ACTIVITY); } }
		 */

		setContentView(R.layout.main);
		initButtons();
		applyTheme();
		instance = this;

		currentFragment = nextFragment = FragmentsAvailable.DIALER;
		if (savedInstanceState == null)
		{
			if (findViewById(R.id.fragmentContainer) != null)
			{
				dialerFragment = new DialerFragment();
				dialerFragment.setArguments(getIntent().getExtras());
				getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, dialerFragment).commit();
				selectMenu(FragmentsAvailable.DIALER);
			}
		}

		// int missedCalls = LinphoneManager.getLc().getMissedCallsCount();
		// displayMissedCalls(missedCalls);

		int rotation = Compatibility.getRotation(getWindowManager().getDefaultDisplay());
		// Inverse landscape rotation to initiate linphoneCore correctly
		if (rotation == 270) rotation = 90;
		else if (rotation == 90) rotation = 270;

		LinphoneManager.getLc().setDeviceRotation(rotation);
		mAlwaysChangingPhoneAngle = rotation;

		updateAnimationsState();
	}

	/*
	 * public void closeKeyboard(IBinder windowToken) { InputMethodManager mgr =
	 * (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	 * mgr.hideSoftInputFromWindow(windowToken, 0); }
	 */

	private void initButtons()
	{
		menu = (LinearLayout) findViewById(R.id.menu);
	//	mark = (LinearLayout) findViewById(R.id.mark);
		contacts = (ImageView) findViewById(R.id.contacts);
		history = (ImageView) findViewById(R.id.history);
		dialer = (ImageView) findViewById(R.id.dialer);
		credit = (ImageView) findViewById(R.id.credit_his);
		settings = (ImageView) findViewById(R.id.settings);

		contacts.setOnClickListener(this);
		dialer.setOnClickListener(this);
		settings.setOnClickListener(this);
		credit.setOnClickListener(this);
		history.setOnClickListener(this);

		// aboutChat = (ImageView) findViewById(R.id.about_chat);
		// aboutSettings = (ImageView) findViewById(R.id.about_settings);

		// chat.setVisibility(View.GONE);
		// aboutChat.setVisibility(View.GONE);
		// aboutSettings.setVisibility(View.GONE);

		/*
		 * if (getResources().getBoolean(R.bool.replace_chat_by_about)) {
		 * chat.setVisibility(View.GONE); chat.setOnClickListener(null);
		 * findViewById(R.id.completeChat).setVisibility(View.GONE);
		 * aboutChat.setVisibility(View.VISIBLE);
		 * aboutChat.setOnClickListener(this); } if
		 * (getResources().getBoolean(R.bool.replace_settings_by_about)) {
		 * settings.setVisibility(View.GONE); settings.setOnClickListener(null);
		 * aboutSettings.setVisibility(View.VISIBLE);
		 * aboutSettings.setOnClickListener(this); }
		 * 
		 * missedCalls = (TextView) findViewById(R.id.missedCalls); missedChats
		 * = (TextView) findViewById(R.id.missedChats);
		 */

		getSupportLoaderManager().initLoader(0, null, this);
		adap = new MyHSimpleCAdapter(this, R.layout.history_cell_simple, null, new String[] { HistoryStatus.Column_No }, new int[] { R.id.sipUri });
	}

	public static int	timesec	= -1;
	boolean				running	= true;

	public Thread		t		= new Thread(new Runnable()
								{

									@Override
									public void run()
									{
										while (running)
										{
											try
											{
												Thread.sleep(1000);
												timesec++;
											}
											catch (Exception e)
											{
												Log.i("timesec", e.getMessage());
											}
										}
									}
								});

	public void startTimer()
	{
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				timesec++;
			}
		}, 1, 1000);
	}

	public void stopTimer()
	{
		timer.cancel();
	}

	private boolean isTablet()
	{
		return getResources().getBoolean(R.bool.isTablet);
	}

	public void hideStatusBar()
	{
		if (isTablet()) { return; }

		findViewById(R.id.status).setVisibility(View.GONE);
		findViewById(R.id.fragmentContainer).setPadding(0, 0, 0, 0);
	}

	private void showStatusBar()
	{
		if (isTablet()) { return; }

		findViewById(R.id.status).setVisibility(View.VISIBLE);
		if (statusFragment != null && !statusFragment.isVisible())
		{
			// Hack to ensure statusFragment is visible after coming back to
			// dialer from chat
			statusFragment.getView().setVisibility(View.VISIBLE);
		}
		// findViewById(R.id.fragmentContainer).setPadding(0,
		// LinphoneUtils.pixelsToDpi(getResources(), 20), 0, 0);
	}

	private void changeCurrentFragment(FragmentsAvailable newFragmentType, Bundle extras)
	{
		changeCurrentFragment(newFragmentType, extras, false);
	}

	@SuppressWarnings("incomplete-switch")
	private void changeCurrentFragment(FragmentsAvailable newFragmentType, Bundle extras, boolean withoutAnimation)
	{
		/*
		 * if (newFragmentType == currentFragment && newFragmentType !=
		 * FragmentsAvailable.CHAT) { return; }
		 */
		nextFragment = newFragmentType;

		if (currentFragment == FragmentsAvailable.DIALER)
		{
			try
			{
				dialerSavedState = getSupportFragmentManager().saveFragmentInstanceState(dialerFragment);
			}
			catch (Exception e)
			{
			}
		}

		Fragment newFragment = null;

		switch (newFragmentType)
		{
			case HISTORY:
//				if (getResources().getBoolean(R.bool.use_simple_history))
//				{
					newFragment = new HistorySimpleFragment();
//				}
//				else
//				{
//					newFragment = new HistoryFragment();
//				}
			break;
			case HISTORY_DETAIL:
				newFragment = new HistoryDetailFragment();
			break;
			case CONTACTS:
				newFragment = new ContactsFragment();
				friendStatusListenerFragment = newFragment;
			break;
//			case CONTACT:
//				newFragment = new ContactFragment();
			//break;
			case EDIT_CONTACT:
				newFragment = new EditContactFragment();
			break;
			case DIALER:
				newFragment = new DialerFragment();
				if (extras == null)
				{
					newFragment.setInitialSavedState(dialerSavedState);
				}
				dialerFragment = newFragment;
			break;
//			case CREDIT_HISTORY:
//				newFragment = new CreditHistFragment();
//			break;
			case SETTINGS:
				newFragment = new PreferencesFragment();
			break;
			case M_SETTINGS:
				newFragment = new MyAccount();
			break;
			case ACCOUNT_SETTINGS:
				newFragment = new GenericLoginFragment();
				extras.putString("type", "update");
			break;
			case TOP_UP:
				newFragment = new RechargeFragment();
			//	extras.putString("type", "update");
			break;
//			case SHOW_COUNTRY:
//				newFragment = new ShowCountry();
//			break;
//			case SHOW_STATES:
//			// newFragment = new ShowStates();
//			break;
//			case SHOW_CITIES:
//				newFragment = new ShowCities();
			//break;
			case NEWS:
				newFragment = new News();
			break;
			case ABOUT:
			case ABOUT_INSTEAD_OF_CHAT:
			case ABOUT_INSTEAD_OF_SETTINGS:
				newFragment = new AboutFragment();
			break;
			// case CHAT:
			// newFragment = new ChatFragment();
			// messageListenerFragment = newFragment;
			// break;
//			case CHATLIST:
//				newFragment = new ChatListFragment();
//				messageListFragment = new Fragment();
//			break;
		}

		if (newFragment != null)
		{
			newFragment.setArguments(extras);
			if (isTablet())
			{
				changeFragmentForTablets(newFragment, newFragmentType, withoutAnimation);
			}
			else
			{
				changeFragment(newFragment, newFragmentType, withoutAnimation);
			}
		}
	}

	private void updateAnimationsState()
	{

		isAnimationDisabled = getResources().getBoolean(R.bool.disable_animations) || !Engine.getPref().getBoolean(getString(R.string.pref_animation_enable_key), false);
		isContactPresenceDisabled = !getResources().getBoolean(R.bool.enable_linphone_friends);
	}

	public boolean isAnimationDisabled()
	{
		return isAnimationDisabled;
	}

	public boolean isContactPresenceDisabled()
	{
		return isContactPresenceDisabled;
	}

	private void changeFragment(Fragment newFragment, FragmentsAvailable newFragmentType, boolean withoutAnimation)
	{
		if (getResources().getBoolean(R.bool.show_statusbar_only_on_dialer))
		{
			if (newFragmentType == FragmentsAvailable.DIALER)
			{
				showStatusBar();
				// hideStatusBar();
			}
			else
			{
				hideStatusBar();
			}
		}

		// if (statusFragment != null)
		// {
		// statusFragment.closeStatusBar();
		// }

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		if (!withoutAnimation && !isAnimationDisabled && currentFragment.shouldAnimate())
		{
			if (newFragmentType.isRightOf(currentFragment))
			{
				transaction.setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left, R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right);
			}
			else
			{
				transaction.setCustomAnimations(R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right, R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left);
			}
		}
		try
		{
			getSupportFragmentManager().popBackStackImmediate(newFragmentType.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		catch (java.lang.IllegalStateException e)
		{

		}

		transaction.addToBackStack(newFragmentType.toString());
		transaction.replace(R.id.fragmentContainer, newFragment);
		transaction.commitAllowingStateLoss();
		getSupportFragmentManager().executePendingTransactions();

		currentFragment = newFragmentType;
	}

	private void changeFragmentForTablets(Fragment newFragment, FragmentsAvailable newFragmentType, boolean withoutAnimation)
	{
		if (getResources().getBoolean(R.bool.show_statusbar_only_on_dialer))
		{
			if (newFragmentType == FragmentsAvailable.DIALER)
			{
				showStatusBar();
			}
			else
			{
				hideStatusBar();
			}
		}
		// if (statusFragment != null)
		// {
		// statusFragment.closeStatusBar();
		// }

		//LinearLayout ll = (LinearLayout) findViewById(R.id.fragmentContainer2);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (newFragmentType.shouldAddItselfToTheRightOf(currentFragment))
		{
		//	ll.setVisibility(View.VISIBLE);

			transaction.addToBackStack(newFragmentType.toString());
			//transaction.replace(R.id.fragmentContainer2, newFragment);
		}
		else
		{
//			if (newFragmentType == FragmentsAvailable.DIALER || newFragmentType == FragmentsAvailable.ABOUT || newFragmentType == FragmentsAvailable.ABOUT_INSTEAD_OF_CHAT
//					|| newFragmentType == FragmentsAvailable.ABOUT_INSTEAD_OF_SETTINGS || newFragmentType == FragmentsAvailable.SETTINGS || newFragmentType == FragmentsAvailable.ACCOUNT_SETTINGS)
			//{
			//	ll.setVisibility(View.GONE);
			//}
			//else
			//{
			//	ll.setVisibility(View.INVISIBLE);
			//}

			if (!withoutAnimation && !isAnimationDisabled && currentFragment.shouldAnimate())
			{
				if (newFragmentType.isRightOf(currentFragment))
				{
					transaction.setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left, R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right);
				}
				else
				{
					transaction.setCustomAnimations(R.anim.slide_in_left_to_right, R.anim.slide_out_left_to_right, R.anim.slide_in_right_to_left, R.anim.slide_out_right_to_left);
				}
			}

			try
			{
				getSupportFragmentManager().popBackStackImmediate(newFragmentType.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
			catch (java.lang.IllegalStateException e)
			{

			}

			transaction.addToBackStack(newFragmentType.toString());
			transaction.replace(R.id.fragmentContainer, newFragment);
		}
		transaction.commitAllowingStateLoss();
		getSupportFragmentManager().executePendingTransactions();

		currentFragment = newFragmentType;
	}

	public void displayHistoryDetail(String sipUri, LinphoneCallLog log)
	{
		LinphoneAddress lAddress = LinphoneCoreFactory.instance().createLinphoneAddress(sipUri);
		Uri uri = LinphoneUtils.findUriPictureOfContactAndSetDisplayName(lAddress, getContentResolver());

		String displayName = lAddress.getDisplayName();
		String pictureUri = uri == null ? null : uri.toString();

		String status;
		if (log.getDirection() == CallDirection.Outgoing)
		{
			status = "Outgoing";
		}
		else
		{
			if (log.getStatus() == CallStatus.Missed)
			{
				status = "Missed";
			}
			else
			{
				status = "Incoming";
			}
		}

		String callTime = secondsToDisplayableString(log.getCallDuration());
		String callDate = String.valueOf(log.getTimestamp());

//		Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer2);
//		if (fragment2 != null && fragment2.isVisible() && currentFragment == FragmentsAvailable.HISTORY_DETAIL)
//		{
//			HistoryDetailFragment historyDetailFragment = (HistoryDetailFragment) fragment2;
//			historyDetailFragment.changeDisplayedHistory(sipUri, displayName, pictureUri, status, callTime, callDate);
//		}
//		else
		{
			Bundle extras = new Bundle();
			extras.putString("SipUri", sipUri);
			if (displayName != null)
			{
				extras.putString("DisplayName", displayName);
				extras.putString("PictureUri", pictureUri);
			}
			extras.putString("CallStatus", status);
			extras.putString("CallTime", callTime);
			extras.putString("CallDate", callDate);

			changeCurrentFragment(FragmentsAvailable.HISTORY_DETAIL, extras);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String secondsToDisplayableString(int secs)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.set(0, 0, 0, 0, 0, secs);
		return dateFormat.format(cal.getTime());
	}

//	public void displayDialer(Contact contact, boolean chatOnly)
//	{
//		Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer2);
//		if (fragment2 != null && fragment2.isVisible() && currentFragment == FragmentsAvailable.CONTACT)
//		{
//			ContactFragment contactFragment = (ContactFragment) fragment2;
//			contactFragment.changeDisplayedContact(contact);
//		}
//		else
//		{
//			Bundle extras = new Bundle();
//			extras.putSerializable("Contact", contact);
//			extras.putBoolean("ChatAddressOnly", chatOnly);
//			changeCurrentFragment(FragmentsAvailable.CONTACT, extras);
//		}
//	}

//	public void displayContact(Contact contact, boolean chatOnly)
//	{
//		Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer2);
//		if (fragment2 != null && fragment2.isVisible() && currentFragment == FragmentsAvailable.CONTACT)
//		{
//			ContactFragment contactFragment = (ContactFragment) fragment2;
//			contactFragment.changeDisplayedContact(contact);
//		}
//		else
//		{
//			Bundle extras = new Bundle();
//			extras.putSerializable("Contact", contact);
//			extras.putBoolean("ChatAddressOnly", chatOnly);
//			changeCurrentFragment(FragmentsAvailable.CONTACT, extras);
//		}
//	}

	public void displayContacts(boolean chatOnly)
	{
		if (chatOnly)
		{
			preferLinphoneContacts = true;
		}

		Bundle extras = new Bundle();
		extras.putBoolean("ChatAddressOnly", chatOnly);
		changeCurrentFragment(FragmentsAvailable.CONTACTS, extras);
		preferLinphoneContacts = false;
	}

	public void displayContactsForEdition(String sipAddress)
	{
		Bundle extras = new Bundle();
		extras.putBoolean("EditOnClick", true);
		extras.putString("SipAddress", sipAddress);
		changeCurrentFragment(FragmentsAvailable.CONTACTS, extras);
	}

	public void displayAbout()
	{
		Bundle extras = new Bundle();
		extras.putString("about", "http://www.ringtoindia.com/mabout.html");
		changeCurrentFragment(FragmentsAvailable.ABOUT, extras);
	}

	public void displayNews()
	{
		Bundle extras = new Bundle();
		extras.putString("news", "http://www.ringtoworld.com/mnews.html");
		changeCurrentFragment(FragmentsAvailable.NEWS, extras);
	}

	// public void displayChat(String sipUri)
	// {
	// if (getResources().getBoolean(R.bool.disable_chat)) { return; }
	//
	// LinphoneAddress lAddress =
	// LinphoneCoreFactory.instance().createLinphoneAddress(sipUri);
	// Uri uri =
	// LinphoneUtils.findUriPictureOfContactAndSetDisplayName(lAddress,
	// getContentResolver());
	// String displayName = lAddress.getDisplayName();
	// String pictureUri = uri == null ? null : uri.toString();
	//
	// if (currentFragment == FragmentsAvailable.CHATLIST || currentFragment ==
	// FragmentsAvailable.CHAT)
	// {
	// Fragment fragment2 =
	// getSupportFragmentManager().findFragmentById(R.id.fragmentContainer2);
	// if (fragment2 != null && fragment2.isVisible() && currentFragment ==
	// FragmentsAvailable.CHAT)
	// {
	// ChatFragment chatFragment = (ChatFragment) fragment2;
	// chatFragment.changeDisplayedChat(sipUri, displayName, pictureUri);
	// }
	// else
	// {
	// Bundle extras = new Bundle();
	// extras.putString("SipUri", sipUri);
	// if (lAddress.getDisplayName() != null)
	// {
	// extras.putString("DisplayName", displayName);
	// extras.putString("PictureUri", pictureUri);
	// }
	// changeCurrentFragment(FragmentsAvailable.CHAT, extras);
	// }
	// }
	// else
	// {
	// changeCurrentFragment(FragmentsAvailable.CHATLIST, null);
	// displayChat(sipUri);
	// }
	// LinphoneService.instance().resetMessageNotifCount();
	// LinphoneService.instance().removeMessageNotification();
	// displayMissedChats(getChatStorage().getUnreadMessageCount());
	// }

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		resetSelection();

		if (id == R.id.history)
		{
			changeCurrentFragment(FragmentsAvailable.HISTORY, null);
			history.setSelected(true);
			LinphoneManager.getLc().resetMissedCallsCount();
			// displayMissedCalls(0);
		}
		else if (id == R.id.contacts)
		{
			changeCurrentFragment(FragmentsAvailable.CONTACTS, null);
			contacts.setSelected(true);
		}
		else if (id == R.id.dialer)
		{
			changeCurrentFragment(FragmentsAvailable.DIALER, null);
			dialer.setSelected(true);
		}
		else if (id == R.id.settings)
		{
			changeCurrentFragment(FragmentsAvailable.M_SETTINGS, null);
			settings.setSelected(true);
		}
		else if (id == R.id.credit_his)
		{
			// Bundle b = new Bundle();
			// b.putSerializable("About",
			// FragmentsAvailable.ABOUT_INSTEAD_OF_CHAT);
			// changeCurrentFragment(FragmentsAvailable.ABOUT_INSTEAD_OF_CHAT,
			// b);
			changeCurrentFragment(FragmentsAvailable.TOP_UP, null);
			credit.setSelected(true);
		}
//		else if (id == R.id.about_settings)
//		{
//			Bundle b = new Bundle();
//			b.putSerializable("About", FragmentsAvailable.ABOUT_INSTEAD_OF_SETTINGS);
//			changeCurrentFragment(FragmentsAvailable.ABOUT_INSTEAD_OF_SETTINGS, b);
//			aboutSettings.setSelected(true);
//		}
		/*
		 * else if (id == R.id.chat) {
		 * changeCurrentFragment(FragmentsAvailable.CHATLIST, null);
		 * chat.setSelected(true); }
		 */
	}

	private void resetSelection()
	{
		history.setSelected(false);
		contacts.setSelected(false);
		dialer.setSelected(false);
		settings.setSelected(false);
		credit.setSelected(false);
		// aboutChat.setSelected(false);
		// aboutSettings.setSelected(false);
	}

	// @SuppressWarnings("incomplete-switch")
	public void selectMenu(FragmentsAvailable menuToSelect)
	{
		currentFragment = menuToSelect;
		resetSelection();

		switch (menuToSelect)
		{
			case HISTORY:
			case HISTORY_DETAIL:
				history.setSelected(true);
			break;
			case CONTACTS:
				// case CONTACT:
				// case EDIT_CONTACT:
				contacts.setSelected(true);
			break;
			case DIALER:
				dialer.setSelected(true);
			break;
			case CREDIT_HISTORY:
				dialer.setSelected(true);
			break;
			// case SETTINGS:
			case M_SETTINGS:
				settings.setSelected(true);
			break;
			case ABOUT_INSTEAD_OF_CHAT:
				aboutChat.setSelected(true);
			break;
			case ABOUT_INSTEAD_OF_SETTINGS:
				aboutSettings.setSelected(true);
			break;
			case CHAT:
			// case CHATLIST:
			// chat.setSelected(true);
			break;
		}
	}

	public void updateDialerFragment(DialerFragment fragment)
	{
		resetSelection();
		dialerFragment = fragment;
		dialer.setSelected(true);
		// Hack to maintain soft input flags
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	// public void updateChatFragment(ChatFragment fragment)
	// {
	// messageListenerFragment = fragment;
	// // Hack to maintain soft input flags
	// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
	// | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	// }

//	public void updateChatListFragment(ChatListFragment fragment)
//	{
//		messageListFragment = fragment;
//	}

	public void hideMenu(boolean hide)
	{
		menu.setVisibility(hide ? View.GONE : View.VISIBLE);
		//mark.setVisibility(hide ? View.GONE : View.VISIBLE);
	}

	public void updateStatusFragment(StatusFragment fragment)
	{
		statusFragment = fragment;

		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null && lc.getDefaultProxyConfig() != null)
		{
			statusFragment.registrationStateChanged(LinphoneManager.getLc().getDefaultProxyConfig().getState());
		}
	}

	public void displaySettings()
	{
		changeCurrentFragment(FragmentsAvailable.SETTINGS, null);
		settings.setSelected(true);
	}

	public void displayShowCountry(String str)
	{
		Bundle bundle = new Bundle();
		bundle.putString("listcountry", str);
		changeCurrentFragment(FragmentsAvailable.SHOW_COUNTRY, null);
		settings.setSelected(true);
	}

	public void displayStates()
	{
		// ShowStates fragment = new ShowStates();
		// changeCurrentFragment(FragmentsAvailable.SHOW_STATES, null);

	}

//	public void displaycities()
//	{
//		ShowCities fragment = new ShowCities();
//		changeCurrentFragment(FragmentsAvailable.SHOW_CITIES, null);
//
//	}

	public void displayMenuSettings()
	{
		changeCurrentFragment(FragmentsAvailable.M_SETTINGS, null);
		settings.setSelected(true);
	}

	public void displayAccSettings()
	{
		Bundle bundle = new Bundle();
		changeCurrentFragment(FragmentsAvailable.ACCOUNT_SETTINGS, bundle);
		settings.setSelected(true);
	}
	public void displayRecharge()
	{
		
		changeCurrentFragment(FragmentsAvailable.TOP_UP, null);
		settings.setSelected(true);
	}
	public void applyConfigChangesIfNeeded()
	{
		if (nextFragment != FragmentsAvailable.M_SETTINGS && nextFragment != FragmentsAvailable.ACCOUNT_SETTINGS)
		{
			reloadConfig();
			updateAnimationsState();
		}
	}

	private void reloadConfig()
	{
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();

		if (lc != null && (lc.isInComingInvitePending() || lc.isIncall()))
		{
			Log.w("Call in progress => settings not applied");
			return;
		}

		try
		{
			LinphoneManager.getInstance().initFromConf();
			lc.setVideoPolicy(LinphoneManager.getInstance().isAutoInitiateVideoCalls(), LinphoneManager.getInstance().isAutoAcceptCamera());
		}
		catch (LinphoneException e)
		{
			if (!(e instanceof LinphoneConfigException))
			{
				Log.e(e, "Cannot update config");
				return;
			}

			LinphoneActivity.instance().showPreferenceErrorDialog(e.getMessage());
		}
	}

	public void displayAccountSettings(int accountNumber)
	{
		Bundle bundle = new Bundle();
		bundle.putInt("Account", accountNumber);
		changeCurrentFragment(FragmentsAvailable.ACCOUNT_SETTINGS, bundle);
		settings.setSelected(true);
	}

	public StatusFragment getStatusFragment()
	{
		return statusFragment;
	}

	public List<String> getChatList()
	{
		return getChatStorage().getChatList();
	}

	public List<String> getDraftChatList()
	{
		return getChatStorage().getDrafts();
	}

	public List<ChatMessage> getChatMessages(String correspondent)
	{
		return getChatStorage().getMessages(correspondent);
	}

	public void removeFromChatList(String sipUri)
	{
		getChatStorage().removeDiscussion(sipUri);
	}

	public void removeFromDrafts(String sipUri)
	{
		getChatStorage().deleteDraft(sipUri);
	}

	// @Override
	// public void onMessageReceived(LinphoneAddress from, LinphoneChatMessage
	// message, int id)
	// {
	// ChatFragment chatFragment = ((ChatFragment) messageListenerFragment);
	// if (messageListenerFragment != null &&
	// messageListenerFragment.isVisible() &&
	// chatFragment.getSipUri().equals(from.asStringUriOnly()))
	// {
	// chatFragment.onMessageReceived(id, from, message);
	// getChatStorage().markMessageAsRead(id);
	// }
	// else if (LinphoneService.isReady())
	// {
	// displayMissedChats(getChatStorage().getUnreadMessageCount());
	// if (messageListFragment != null && messageListFragment.isVisible())
	// {
	// ((ChatListFragment) messageListFragment).refresh();
	// }
	// }
	// }

	// public void updateMissedChatCount()
	// {
	// displayMissedChats(getChatStorage().getUnreadMessageCount());
	// }

	public int onMessageSent(String to, String message)
	{
		getChatStorage().deleteDraft(to);
		return getChatStorage().saveMessage("", to, message);
	}

	public int onMessageSent(String to, Bitmap image, String imageURL)
	{
		getChatStorage().deleteDraft(to);
		return getChatStorage().saveMessage("", to, image);
	}

	public void onMessageStateChanged(String to, String message, int newState)
	{
		getChatStorage().updateMessageStatus(to, message, newState);
	}

	public void onImageMessageStateChanged(String to, int id, int newState)
	{
		getChatStorage().updateMessageStatus(to, id, newState);
	}

	static long	tm	= 0L;

	@Override
	public void onRegistrationStateChanged(RegistrationState state)
	{
		if (System.currentTimeMillis() - tm < 4000) return;
		tm = System.currentTimeMillis();
		if (statusFragment != null)
		{
			LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
			if (lc != null && lc.getDefaultProxyConfig() != null) statusFragment.registrationStateChanged(lc.getDefaultProxyConfig().getState());
		}
	}

	/*
	 * private void displayMissedCalls(final int missedCallsCount) {
	 * mHandler.post(new Runnable() {
	 * 
	 * @Override public void run() { if (missedCallsCount > 0) {
	 * missedCalls.setText(missedCallsCount + "");
	 * missedCalls.setVisibility(View.VISIBLE); if (!isAnimationDisabled) {
	 * missedCalls
	 * .startAnimation(AnimationUtils.loadAnimation(LinphoneActivity.this,
	 * R.anim.bounce)); } } else { missedCalls.clearAnimation();
	 * missedCalls.setVisibility(View.GONE); } } });
	 */
	// }

	// private void displayMissedChats(final int missedChatCount)
	// {
	// mHandler.post(new Runnable()
	// {
	// @Override
	// public void run()
	// {
	// if (missedChatCount > 0)
	// {
	// missedChats.setText(missedChatCount + "");
	// if (missedChatCount > 99)
	// {
	// missedChats.setTextSize(12);
	// }
	// else
	// {
	// missedChats.setTextSize(20);
	// }
	// missedChats.setVisibility(View.VISIBLE);
	// if (!isAnimationDisabled)
	// {
	// missedChats.startAnimation(AnimationUtils.loadAnimation(LinphoneActivity.this,
	// R.anim.bounce));
	// }
	// }
	// else
	// {
	// missedChats.clearAnimation();
	// missedChats.setVisibility(View.GONE);
	// }
	// }
	// });
	// }

	@Override
	public void onCallStateChanged(LinphoneCall call, State state, String message)
	{
		if (state == State.IncomingReceived)
		{
			startActivity(new Intent(this, IncomingCallActivity.class));
		}
		else if (state == State.OutgoingInit)
		{
			if (call.getCurrentParamsCopy().getVideoEnabled())
			{
				startVideoActivity(call);
			}
			else
			{
				startIncallActivity(call);
			}
		}
		else if (state == State.Connected || state == State.StreamsRunning)
		{
			if (state == State.StreamsRunning)
			{
				// tim2.setBase(SystemClock.elapsedRealtime() - 1000 * timesec);
				// tim2.start();
			}
			else
			{
				// t.start();
				timesec = -1;
				timer = new Timer();
				startTimer();
			}
		}
		else if (state == State.CallEnd || state == State.Error || state == State.CallReleased)
		{
			// Convert LinphoneCore message for internalization
			if (message != null && message.equals("Call declined."))
			{
				displayCustomToast(getString(R.string.error_call_declined), Toast.LENGTH_LONG);
			}
			else if (message != null && message.equals("User not found."))
			{
				displayCustomToast(getString(R.string.error_user_not_found), Toast.LENGTH_LONG);
			}
			else if (message != null && message.equals("Incompatible media parameters."))
			{
				displayCustomToast(getString(R.string.error_incompatible_media), Toast.LENGTH_LONG);
			}
			else if (message != null && message.equals("Call terminated") /*
																		 * ||
																		 * message
																		 * .
																		 * equals
																		 * (
																		 * "Forbidden"
																		 * )
																		 */
					|| message.equals("Call ended") || message.equals("Call released") || message.equals("Service Unavailable"))
			{
				try
				{
					/*
					 * if(tim2!=null){ tim2.stop();
					 */running = false;
					// t.interrupt();
					stopTimer();
					// String
					// dur=/*call.getDuration();*/tim2.getText().toString();
					String dur = secondsToDisplayableString(timesec);
					LinphoneActivity.instance().storedDuration(dur);
					Log.i(dur + " " + timesec);
					/*
					 * }else{ Log.i("Call ended"); }
					 */
				}
				catch (Exception e)
				{
					Log.i("Call ended" + e.getMessage());
				}
			}

			resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
		}

		// int missedCalls = LinphoneManager.getLc().getMissedCallsCount();
		// displayMissedCalls(missedCalls);
	}

	public void displayCustomToast(final String message, final int duration)
	{
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastRoot));

				TextView toastText = (TextView) layout.findViewById(R.id.toastMessage);
				toastText.setText(message);

				final Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.setDuration(duration);
				toast.setView(layout);
				toast.show();
			}
		});
	}

	@Override
	public void setAddresGoToDialerAndCall(String number, String name, Uri photo)
	{
		Bundle extras = new Bundle();
		extras.putString("SipUri", number);
		extras.putString("DisplayName", name);
		extras.putString("Photo", photo == null ? null : photo.toString());

		// changeCurrentFragment(FragmentsAvailable.DIALER, extras);

		AddressType address = new AddressText(this, null);
		address.setDisplayedName(name);
		address.setText(number);
		if (LinphoneManager.getLc().getCallsNb() == 0)
		{
			// LinphoneManager.getInstance().newOutgoingCall(address);
			startActivity(new Intent(this, InCallActivity.class).putExtra("Text", address.getText().toString()).putExtra("Name", address.getDisplayedName()));
		}
	}

	public void setAddressAndGoToDialer(String number)
	{
		Bundle extras = new Bundle();
		extras.putString("SipUri", number);
		changeCurrentFragment(FragmentsAvailable.DIALER, extras);
	}

	@Override
	public void goToDialer()
	{
		changeCurrentFragment(FragmentsAvailable.DIALER, null);
	}

	public void startVideoActivity(LinphoneCall currentCall)
	{
		Intent intent = new Intent(this, InCallActivity.class);
		intent.putExtra("VideoEnabled", true);
		startOrientationSensor();
		startActivityForResult(intent, callActivity);
	}

	public void startIncallActivity(LinphoneCall currentCall)
	{
		Intent intent = new Intent(this, InCallActivity.class);
		intent.putExtra("VideoEnabled", false);
		// startOrientationSensor();
		startActivityForResult(intent, callActivity);
	}

	/**
	 * Register a sensor to track phoneOrientation changes
	 */
	private synchronized void startOrientationSensor()
	{
		if (mOrientationHelper == null)
		{
			mOrientationHelper = new LocalOrientationEventListener(this);
		}
		mOrientationHelper.enable();
	}

	private int						mAlwaysChangingPhoneAngle	= -1;
	private AcceptNewFriendDialog	acceptNewFriendDialog;
	public Uri						lastid;

	private class LocalOrientationEventListener extends OrientationEventListener
	{
		public LocalOrientationEventListener(Context context)
		{
			super(context);
		}

		@Override
		public void onOrientationChanged(final int o)
		{
			if (o == OrientationEventListener.ORIENTATION_UNKNOWN) { return; }

			int degrees = 270;
			if (o < 45 || o > 315) degrees = 0;
			else if (o < 135) degrees = 90;
			else if (o < 225) degrees = 180;

			if (mAlwaysChangingPhoneAngle == degrees) { return; }
			mAlwaysChangingPhoneAngle = degrees;

			Log.d("Phone orientation changed to ", degrees);
			int rotation = (360 - degrees) % 360;
			LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
			if (lc != null)
			{
				lc.setDeviceRotation(rotation);
				LinphoneCall currentCall = lc.getCurrentCall();
				if (currentCall != null && currentCall.cameraEnabled() && currentCall.getCurrentParamsCopy().getVideoEnabled())
				{
					lc.updateCall(currentCall, null);
				}
			}
		}
	}

	public void showPreferenceErrorDialog(String message)
	{

	}

	public List<Contact> getAllContacts()
	{
		return contactList;
	}

	public List<Contact> getSIPContacts()
	{
		return sipContactList;
	}

	public Cursor getAllContactsCursor()
	{
		return contactCursor;
	}

	public Cursor getSIPContactsCursor()
	{
		return sipContactCursor;
	}

	public void setLinphoneContactsPrefered(boolean isPrefered)
	{
		preferLinphoneContacts = isPrefered;
	}

	public boolean isLinphoneContactsPrefered()
	{
		return preferLinphoneContacts;
	}

	public void refreshStatus(OnlineStatus status)
	{
		if (LinphoneManager.isInstanciated())
		{
			LinphoneManager.getLcIfManagerNotDestroyedOrNull().setPresenceInfo(0, "", status);
		}
	}

	public void onNewSubscriptionRequestReceived(LinphoneFriend friend, String sipUri)
	{
		if (isContactPresenceDisabled) { return; }

		sipUri = sipUri.replace("<", "").replace(">", "");

		if (Engine.getPref().getBoolean(getString(R.string.pref_auto_accept_friends_key), false))
		{
			Contact contact = findContactWithSipAddress(sipUri);
			if (contact != null)
			{
				friend.enableSubscribes(true);
				try
				{
					LinphoneManager.getLc().addFriend(friend);
					contact.setFriend(friend);
				}
				catch (LinphoneCoreException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			Contact contact = findContactWithSipAddress(sipUri);
			if (contact != null)
			{
				FragmentManager fm = getSupportFragmentManager();
				acceptNewFriendDialog = new AcceptNewFriendDialog(contact, sipUri);
				acceptNewFriendDialog.show(fm, "New Friend Request Dialog");
			}
		}
	}

	private Contact findContactWithSipAddress(String sipUri)
	{
		if (!sipUri.startsWith("sip:"))
		{
			sipUri = "sip:" + sipUri;
		}

		for (Contact contact : sipContactList)
		{
			for (String addr : contact.getNumerosOrAddresses())
			{
				if (addr.equals(sipUri)) { return contact; }
			}
		}
		return null;
	}

	public void onNotifyPresenceReceived(LinphoneFriend friend)
	{
		if (!isContactPresenceDisabled && currentFragment == FragmentsAvailable.CONTACTS && friendStatusListenerFragment != null)
		{
			((ContactsFragment) friendStatusListenerFragment).invalidate();
		}
	}

	public boolean newFriend(Contact contact, String sipUri)
	{
		LinphoneFriend friend = LinphoneCoreFactory.instance().createLinphoneFriend(sipUri);
		friend.enableSubscribes(true);
		friend.setIncSubscribePolicy(LinphoneFriend.SubscribePolicy.SPAccept);
		try
		{
			LinphoneManager.getLc().addFriend(friend);
			contact.setFriend(friend);
			return true;
		}
		catch (LinphoneCoreException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	private void acceptNewFriend(Contact contact, String sipUri, boolean accepted)
	{
		acceptNewFriendDialog.dismissAllowingStateLoss();
		if (accepted)
		{
			newFriend(contact, sipUri);
		}
	}

	public boolean removeFriend(Contact contact, String sipUri)
	{
		LinphoneFriend friend = LinphoneManager.getLc().findFriendByAddress(sipUri);
		if (friend != null)
		{
			friend.enableSubscribes(false);
			LinphoneManager.getLc().removeFriend(friend);
			contact.setFriend(null);
			return true;
		}
		return false;
	}

	private void searchFriendAndAddToContact(Contact contact)
	{
		if (contact == null || contact.getNumerosOrAddresses() == null) { return; }

		for (String sipUri : contact.getNumerosOrAddresses())
		{
			if (LinphoneUtils.isSipAddress(sipUri))
			{
				LinphoneFriend friend = LinphoneManager.getLc().findFriendByAddress(sipUri);
				if (friend != null)
				{
					friend.enableSubscribes(true);
					friend.setIncSubscribePolicy(LinphoneFriend.SubscribePolicy.SPAccept);
					contact.setFriend(friend);
					break;
				}
			}
		}
	}

	public void removeContactFromLists(Contact contact)
	{
		if (contactList.contains(contact))
		{
			contactList.remove(contact);
			contactCursor = Compatibility.getContactsCursor(getContentResolver());
		}
		if (sipContactList.contains(contact))
		{
			sipContactList.remove(contact);
			sipContactCursor = Compatibility.getSIPContactsCursor(getContentResolver());
		}
	}

	public synchronized void prepareContactsInBackground()
	{
		if (contactCursor != null)
		{
			contactCursor.close();
		}
		if (sipContactCursor != null)
		{
			sipContactCursor.close();
		}

		contactCursor = Compatibility.getContactsCursor(getContentResolver());
		sipContactCursor = Compatibility.getSIPContactsCursor(getContentResolver());

		Thread sipContactsHandler = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				for (int i = 0; i < sipContactCursor.getCount(); i++)
				{
					Contact contact = Compatibility.getContact(getContentResolver(), sipContactCursor, i);
					if (contact == null) continue;

					contact.refresh(getContentResolver());
					if (!isContactPresenceDisabled)
					{
						searchFriendAndAddToContact(contact);
					}
					sipContactList.add(contact);
				}
				for (int i = 0; i < contactCursor.getCount(); i++)
				{
					Contact contact = Compatibility.getContact(getContentResolver(), contactCursor, i);
					if (contact == null) continue;

					for (Contact c : sipContactList)
					{
						if (c != null && c.getID().equals(contact.getID()))
						{
							contact = c;
							break;
						}
					}
					contactList.add(contact);
				}
			}
		});

		contactList = new ArrayList<Contact>();
		sipContactList = new ArrayList<Contact>();

		sipContactsHandler.start();
	}

	private void initInCallMenuLayout(boolean callTransfer)
	{
		selectMenu(FragmentsAvailable.DIALER);
		if (dialerFragment != null)
		{
			((DialerFragment) dialerFragment).resetLayout(callTransfer);
		}
	}

	public void resetClassicMenuLayoutAndGoBackToCallIfStillRunning()
	{
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (dialerFragment != null)
				{
					((DialerFragment) dialerFragment).resetLayout(false);
				}

				if (LinphoneManager.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0)
				{
					LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
					if (call.getState() == LinphoneCall.State.IncomingReceived)
					{
						startActivity(new Intent(LinphoneActivity.this, IncomingCallActivity.class));
					}
					else if (call.getCurrentParamsCopy().getVideoEnabled())
					{
						startVideoActivity(call);
					}
					else
					{
						// startIncallActivity(call);
					}
				}
			}
		});
	}

	public FragmentsAvailable getCurrentFragment()
	{
		return currentFragment;
	}

	public ChatStorage getChatStorage()
	{
		if (chatStorage == null)
		{
			chatStorage = new ChatStorage(this);
		}
		return chatStorage;
	}

	public void addContact(String displayName, String sipUri)
	{
		if (getResources().getBoolean(R.bool.use_android_native_contact_edit_interface))
		{
			Intent intent = Compatibility.prepareAddContactIntent(displayName, sipUri);
			startActivity(intent);
		}
		else
		{
			Bundle extras = new Bundle();
			extras.putSerializable("NewSipAdress", sipUri);
			changeCurrentFragment(FragmentsAvailable.EDIT_CONTACT, extras);
		}
	}

	public void editContact(Contact contact)
	{
		if (getResources().getBoolean(R.bool.use_android_native_contact_edit_interface))
		{
			Intent intent = Compatibility.prepareEditContactIntent(Integer.parseInt(contact.getID()));
			startActivity(intent);
		}
		else
		{
			Bundle extras = new Bundle();
			extras.putSerializable("Contact", contact);
			changeCurrentFragment(FragmentsAvailable.EDIT_CONTACT, extras);
		}
	}

	public void editContact(Contact contact, String sipAddress)
	{
		if (getResources().getBoolean(R.bool.use_android_native_contact_edit_interface))
		{
			Intent intent = Compatibility.prepareEditContactIntentWithSipAddress(Integer.parseInt(contact.getID()), sipAddress);
			startActivity(intent);
		}
		else
		{
			Bundle extras = new Bundle();
			extras.putSerializable("Contact", contact);
			extras.putSerializable("NewSipAdress", sipAddress);
			changeCurrentFragment(FragmentsAvailable.EDIT_CONTACT, extras);
		}
	}

	public void exit()
	{
		refreshStatus(OnlineStatus.Offline);
		finish();
		stopService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_FIRST_USER && requestCode == SETTINGS_ACTIVITY)
		{
			if (data.getExtras().getBoolean("Exit", false))
			{
				exit();
			}
			else
			{
				FragmentsAvailable newFragment = (FragmentsAvailable) data.getExtras().getSerializable("FragmentToDisplay");
				changeCurrentFragment(newFragment, null, true);
				selectMenu(newFragment);
			}
		}
		else if (requestCode == callActivity)
		{
			boolean callTransfer = data == null ? false : data.getBooleanExtra("Transfer", false);
			if (LinphoneManager.getLc().getCallsNb() > 0)
			{
				initInCallMenuLayout(callTransfer);
			}
			else
			{
				resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
			}
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (!LinphoneService.isReady())
		{
			startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
		}

		// Remove to avoid duplication of the listeners
		LinphoneManager.removeListener(this);
		LinphoneManager.addListener(this);

		prepareContactsInBackground();

		if (chatStorage != null)
		{
			chatStorage.close();
		}

		chatStorage = new ChatStorage(this);

		// updateMissedChatCount();
		//
		// displayMissedCalls(LinphoneManager.getLc().getMissedCallsCount());

		if (LinphoneManager.getLc().getCalls().length > 0)
		{
			LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
			LinphoneCall.State callState = call.getState();
			if (callState == State.IncomingReceived)
			{
				startActivity(new Intent(this, IncomingCallActivity.class));
			}
		}

		refreshStatus(OnlineStatus.Online);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		refreshStatus(OnlineStatus.Away);
	}

	@Override
	protected void onDestroy()
	{
		LinphoneManager.removeListener(this);

		if (chatStorage != null)
		{
			chatStorage.close();
			chatStorage = null;
		}

		if (mOrientationHelper != null)
		{
			mOrientationHelper.disable();
			mOrientationHelper = null;
		}

		instance = null;
		super.onDestroy();

		unbindDrawables(findViewById(R.id.topLayout));
		System.gc();
	}

	private void unbindDrawables(View view)
	{
		if (view != null && view.getBackground() != null)
		{
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView))
		{
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
			{
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		Bundle extras = intent.getExtras();
		if (extras != null && extras.getBoolean("GoToChat", false))
		{
			LinphoneService.instance().removeMessageNotification();
			String sipUri = extras.getString("ChatContactSipUri");
			// displayChat(sipUri);
		}
		else if (extras != null && extras.getBoolean("Notification", false))
		{
			if (LinphoneManager.getLc().getCallsNb() > 0)
			{
				LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
				if (call.getCurrentParamsCopy().getVideoEnabled())
				{
					startVideoActivity(call);
				}
				else
				{
					startIncallActivity(call);
				}
			}
		}
		else
		{
			if (dialerFragment != null)
			{
				((DialerFragment) dialerFragment).newOutgoingCall(intent);
			}
			if (LinphoneManager.getLc().getCalls().length > 0)
			{
				LinphoneCall calls[] = LinphoneManager.getLc().getCalls();
				if (calls.length > 0)
				{
					LinphoneCall call = calls[0];

					if (call != null && call.getState() != LinphoneCall.State.IncomingReceived)
					{
						if (call.getCurrentParamsCopy().getVideoEnabled())
						{
							startVideoActivity(call);
						}
						else
						{
							startIncallActivity(call);
						}
					}
				}

				// If a call is ringing, start incomingcallactivity
				Collection<LinphoneCall.State> incoming = new ArrayList<LinphoneCall.State>();
				incoming.add(LinphoneCall.State.IncomingReceived);
				if (LinphoneUtils.getCallsInState(LinphoneManager.getLc(), incoming).size() > 0)
				{
					if (InCallActivity.isInstanciated())
					{
						InCallActivity.instance().startIncomingCallActivity();
					}
					else
					{
						startActivity(new Intent(this, IncomingCallActivity.class));
					}
				}
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (currentFragment == FragmentsAvailable.DIALER)
			{
				boolean isBackgroundModeActive = Engine.getPref().getBoolean(getString(R.string.pref_background_mode_key), getResources().getBoolean(R.bool.pref_background_mode_default));
				if (!isBackgroundModeActive)
				{
					stopService(new Intent(Intent.ACTION_MAIN).setClass(this, LinphoneService.class));
					finish();
				}
				else if (LinphoneUtils.onKeyBackGoHome(this, keyCode, event)) { return true; }
			}
			else if (!isTablet())
			{
				int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
				if (backStackEntryCount <= 1)
				{
					showStatusBar();
					// hideStatusBar();
				}

				if (currentFragment == FragmentsAvailable.SETTINGS)
				{
					// showStatusBar();
					hideStatusBar();
					reloadConfig();
					updateAnimationsState();
				}
			}
		}
		else if (keyCode == KeyEvent.KEYCODE_MENU && statusFragment != null)
		{
			if (event.getRepeatCount() < 1)
			{
				// statusFragment.openOrCloseStatusBar(true);

			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.menu_layout, menu); return
	 * super.onCreateOptionsMenu(menu); }
	 */

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) {
	 * if(item.getItemId()==R.id.item1){ exit(); } return
	 * super.onOptionsItemSelected(item); }
	 */

	@SuppressLint("ValidFragment")
	class AcceptNewFriendDialog extends DialogFragment
	{
		private Contact	contact;
		private String	sipUri;

		public AcceptNewFriendDialog(Contact c, String a)
		{
			contact = c;
			sipUri = a;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View view = inflater.inflate(R.layout.new_friend_request_dialog, container);

			getDialog().setTitle(R.string.linphone_friend_new_request_title);

			Button yes = (Button) view.findViewById(R.id.yes);
			yes.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					acceptNewFriend(contact, sipUri, true);
				}
			});

			Button no = (Button) view.findViewById(R.id.no);
			no.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					acceptNewFriend(contact, sipUri, false);
				}
			});

			return view;
		}
	}

	public void storeHistory(String name, String no, String dur, String stat)
	{
		Calendar cl = Calendar.getInstance();

		SimpleDateFormat timest = new SimpleDateFormat("EEEE MMM d HH:mm");
		String timestamp = timest.format((new Date()));

		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String date = df.format(cl.getTime());

		SimpleDateFormat dat = new SimpleDateFormat("HH:mm:ss");
		String time = dat.format(new Date());

		ContentValues values = new ContentValues();
		values.put(HistoryStatus.Column_Name, name);
		values.put(HistoryStatus.Column_No, no);
		values.put(HistoryStatus.Column_Date, date);
		values.put(HistoryStatus.Column_Time, time);
		values.put(HistoryStatus.Column_Timestamp, timestamp);
		values.put(HistoryStatus.Column_Duration, dur);
		values.put(HistoryStatus.Column_Status, stat);
		lastid = getContentResolver().insert(MyContentProvider.Content_Uri, values);
	}

	public void storedDuration(String dur)
	{
		ContentValues values = new ContentValues();
		values.put(HistoryStatus.Column_Duration, dur);
		Uri urip = lastid;
		if (urip.toString().contains("/"))
		{
			String id[] = urip.toString().split("[/]");
			int uid = getContentResolver().update(MyContentProvider.Content_Uri, values, HistoryStatus.Column_ID + "=?", new String[] { id[1] });
		}
	}

	public void deleteRow(String id)
	{
		getContentResolver().delete(MyContentProvider.Content_Uri, HistoryStatus.Column_ID + "=?", new String[] { id });
	}

	public void restartLoader()
	{
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1)
	{

		CursorLoader cursorLoader = new CursorLoader(this, MyContentProvider.Content_Uri, null, null, null, HistoryStatus.Column_ID + " DESC");
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data)
	{
		adap.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0)
	{
		adap.swapCursor(null);
	}

	public void displayHistoryDetails(String callDate, String callTime, String callDur, String displayName, String sipUri, String status, String timestamp)
	{

//		Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer2);
//		if (fragment2 != null && fragment2.isVisible() && currentFragment == FragmentsAvailable.HISTORY_DETAIL)
//		{
//			HistoryDetailFragment historyDetailFragment = (HistoryDetailFragment) fragment2;
//			historyDetailFragment.changeDisplayedHistory(sipUri, displayName, "", status, callTime, callDate);
//		}
//		else
		{
			Bundle extras = new Bundle();
			extras.putString("SipUri", sipUri);
			if (displayName != null)
			{
				extras.putString("DisplayName", displayName);
				// extras.putString("PictureUri", pictureUri);
			}
			extras.putString("CallStatus", status);
			extras.putString("CallTime", callTime);
			extras.putString("CallTimestamp", timestamp);
			extras.putString("CallDur", callDur);
			extras.putString("CallDate", callDate);

			changeCurrentFragment(FragmentsAvailable.HISTORY_DETAIL, extras);
		}

	}
}

interface ContactPicked
{
	void setAddresGoToDialerAndCall(String number, String name, Uri photo);

	void goToDialer();
}
