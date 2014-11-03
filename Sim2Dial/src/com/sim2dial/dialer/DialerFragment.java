package com.sim2dial.dialer;

/*
 DialerFragment.java
 Copyright (C) 2012  Belledonne Communications, Grenoble, France

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
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.linphone.core.LinphoneCore;
import org.linphone.mediastream.Log;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.kgraphicsengin.RemoteData;
import com.ns.kgraphicsengin.RemoteData.OnRemoteCompleated;
import com.ns.kgraphicsengin.RemoteData.RemoteProperty;
import com.sim2dial.dialer.quickaction.ActionItem;
import com.sim2dial.dialer.quickaction.QuickAction;
import com.sim2dial.dialer.setup.SetupActivity;
import com.sim2dial.dialer.ui.AddressAware;
import com.sim2dial.dialer.ui.AddressText;
import com.sim2dial.dialer.ui.CallBackButton;
import com.sim2dial.dialer.ui.CallButton;
import com.sim2dial.dialer.ui.EraseButton;
import com.sim2dial.dialer.util.Theme;

/**
 * @author Sylvain Berfini
 */
public class DialerFragment extends Fragment implements OnLongClickListener, OnClickListener, OnRemoteCompleated
{
	private static DialerFragment	instance;
	final int						SELECT_COUNTRY			= 0x1;
	private static boolean			isCallTransferOngoing	= false;
	ClipboardManager				clipmgr;
	public boolean					mVisible;
	private AddressText				mAddress;
	private CallButton				mCall;
	private CallBackButton			mCallBack;
	private ImageView				mAddContact;
	private OnClickListener			addContactListener, cancelListener, transferListener;
	private boolean					shouldEmptyAddressField	= true;
TextView select_contry;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/*
		 * getActivity().getWindow().setSoftInputMode(
		 * WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		 */
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) clipmgr = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
	}

	public void initQuickAction()
	{
		// Add action item
		ActionItem addAction = new ActionItem();

		addAction.setTitle("Cut");
		addAction.setIcon(getResources().getDrawable(R.drawable.ic_action_cut));

		// Accept action item
		ActionItem accAction = new ActionItem();

		accAction.setTitle("Copy");
		accAction.setIcon(getResources().getDrawable(R.drawable.ic_action_copy));

		// Upload action item
		ActionItem upAction = new ActionItem();

		upAction.setTitle("Paste");
		upAction.setIcon(getResources().getDrawable(R.drawable.ic_action_paste));

		final QuickAction mQuickAction = new QuickAction(getActivity());

		mQuickAction.addActionItem(addAction);
		mQuickAction.addActionItem(accAction);
		mQuickAction.addActionItem(upAction);

		// setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onItemClick(int pos)
			{

				if (pos == 0)
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					{
						String str = mAddress.getText().subSequence(mAddress.getSelectionStart(), mAddress.getSelectionEnd()).toString();

						if (str.length() > 0)
						{
							ClipData clip = ClipData.newPlainText("cut", str);
							clipmgr.setPrimaryClip(clip);
							/*
							 * SpannableString str1 = new SpannableString( str);
							 * str1.setSpan(new ForegroundColorSpan( 0xff0000),
							 * mAddress .getSelectionStart(), mAddress
							 * .getSelectionEnd(), 0);
							 */
						}
						mAddress.setText(mAddress.getText().subSequence(0, mAddress.getSelectionStart()));
						mAddress.setSelection(mAddress.getText().length());

					}
					else
					{
						// clipmgr.setText(mAddress.getText().toString());
						String str = (String) mAddress.getText().subSequence(mAddress.getSelectionStart(), mAddress.getSelectionEnd()).toString();
						if (str.length() > 0)
						{
							/*
							 * SpannableString str1 = new SpannableString( str);
							 * str1.setSpan(new BackgroundColorSpan( 0xff0000),
							 * mAddress .getSelectionStart(), mAddress
							 * .getSelectionEnd(), 0);
							 */
							clipmgr.setText(str);
						}
						mAddress.setText(mAddress.getText().subSequence(0, mAddress.getSelectionStart()));
						mAddress.setSelection(mAddress.getText().length());
					}

					/*
					 * Toast.makeText(MainActivity.this, "Add item selected",
					 * Toast.LENGTH_SHORT) .show();
					 */
				}
				else if (pos == 1)
				{ // Accept item selected
					/*
					 * Toast.makeText(MainActivity.this, "Accept item selected",
					 * Toast.LENGTH_SHORT) .show();
					 */
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					{
						String str = mAddress.getText().subSequence(mAddress.getSelectionStart(), mAddress.getSelectionEnd()).toString();
						if (str.length() > 0)
						{
							ClipData clip = ClipData.newPlainText("copy", str);
							clipmgr.setPrimaryClip(clip);
							/*
							 * SpannableString str1=new SpannableString(str);
							 * str1.setSpan(new BackgroundColorSpan(0xff0000),
							 * mAddress.getSelectionStart(),
							 * mAddress.getSelectionEnd(), 0);
							 */
							mAddress.setSelection(mAddress.getText().length());
						}

					}
					else
					{
						String str = mAddress.getText().subSequence(mAddress.getSelectionStart(), mAddress.getSelectionEnd()).toString();
						if (str.length() > 0)
						{
							clipmgr.setText(str);
							/*
							 * SpannableString str1=new SpannableString(str);
							 * str1.setSpan(new BackgroundColorSpan(0xff0000),
							 * mAddress.getSelectionStart(),
							 * mAddress.getSelectionEnd(), 0);
							 */
							mAddress.setSelection(mAddress.getText().length());
						}

					}

				}
				else if (pos == 2)
				{ // Upload item selected
					/*
					 * Toast.makeText(MainActivity.this,
					 * "Upload items selected", Toast.LENGTH_SHORT) .show();
					 */
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					{
						ClipData.Item clip = clipmgr.getPrimaryClip().getItemAt(0);
						// mAddress.setSelection(mAddress.getSelectionStart(),
						// mAddress.getText().length());
						mAddress.setText(mAddress.getText().subSequence(0, mAddress.getSelectionStart()));
						mAddress.append(clip.getText());
						mAddress.setSelection(mAddress.getText().length());
					}
					else
					{
						// mAddress.setSelection(mAddress.getSelectionStart(),
						// mAddress.getText().length());
						mAddress.setText(mAddress.getText().subSequence(0, mAddress.getSelectionStart()));
						mAddress.append(clipmgr.getText());
						mAddress.setSelection(mAddress.getText().length());
					}
				}
			}
		});
		mAddress.setOnLongClickListener(new OnLongClickListener()
		{

			@Override
			public boolean onLongClick(View v)
			{
				mAddress.setSelection(mAddress.getSelectionStart(), mAddress.getText().length());
				/*
				 * SpannableStringBuilder string = new SpannableStringBuilder(
				 * mAddress.getText().toString()); string.setSpan(new
				 * BackgroundColorSpan(Color.CYAN),
				 * mAddress.getSelectionStart(), mAddress.getSelectionEnd(), 0);
				 */
				// mAddress.setText(string, BufferType.SPANNABLE);
				// mAddress.setText(string.toString());

				/*
				 * mAddress.setHighlightColor(0xff00ff);
				 */
				mQuickAction.show(v);
				mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
				return true;
			}
		});

		/*
		 * Button btn2 = (Button) this.findViewById(R.id.btn2);
		 * btn2.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { mQuickAction.show(v);
		 * mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER); } });
		 */
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		instance = this;
		View view = inflater.inflate(R.layout.dialer, container, false);
		select_contry=(TextView) view.findViewById(R.id.country);
		select_contry.setOnClickListener(this);
		select_contry.setText(Engine.getPref().getString(Engine.PREF.COUNTRY.name(), "UK"));
		
//		  EditText ed1 = (EditText) view.findViewById(R.id.test);
//		  ed1.setOnLongClickListener(new View.OnLongClickListener() {
//		  
//		  @Override 
//		  public boolean onLongClick(View v) 
//		  {
//		  LinphoneActivity.instance().hideStatusBar();
//		  return true; 
//		  } });
		 

		mAddress = (AddressText) view.findViewById(R.id.Adress);
		mAddress.setDialerFragment(this);
		// mAddress.setInputType(InputType.TYPE_NULL);

		// mAddress.setHighlightColor(0xffff00);
		// mAddress.setOnCreateContextMenuListener(this);
		// registerForContextMenu(mAddress);
		mAddress.setOnLongClickListener(this);

		initQuickAction();

		EraseButton erase = (EraseButton) view.findViewById(R.id.Erase);
		erase.setAddressWidget(mAddress);

		mCall = (CallButton) view.findViewById(R.id.Call);
		mCall.setBackground(Theme.selectorDrawable("shp_voipcall"));
		// mCall.setEnabled(false);
		mCallBack = (CallBackButton) view.findViewById(R.id.CallBack);
		mCallBack.setBackground(Theme.selectorDrawable("shp_callback"));
		// mCallBack.setEnabled(false);

		mCallBack.setAddressWidget(mAddress);
		// mCall.setBackgroundDrawable(GEngine.getGraphics().getSLTRDrawable(ScreenGraphics.XML_RES,"login"));
		mCall.setAddressWidget(mAddress);
		if (LinphoneActivity.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0)
		{
			if (isCallTransferOngoing)
			{
				// mCall.setImageResource(R.drawable.transfer_call);
			}
			else
			{
				// mCall.setImageResource(R.drawable.add_call);
			}
		}
		else
		{
			// mCall.setImageResource(R.drawable.call);
		}

		AddressAware numpad = (AddressAware) view.findViewById(R.id.Dialer);
		if (numpad != null)
		{
			numpad.setAddressWidget(mAddress);
		}

		mAddContact = (ImageView) view.findViewById(R.id.addContact);

		addContactListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				LinphoneActivity.instance().displayContactsForEdition(mAddress.getText().toString());
			}
		};
		cancelListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				LinphoneActivity.instance().resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
			}
		};
		transferListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				LinphoneCore lc = LinphoneManager.getLc();
				if (lc.getCurrentCall() == null) { return; }
				lc.transferCall(lc.getCurrentCall(), mAddress.getText().toString());
				isCallTransferOngoing = false;
				LinphoneActivity.instance().resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
			}
		};

		mAddContact.setEnabled(!(LinphoneActivity.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0));
		resetLayout(isCallTransferOngoing);

		if (getArguments() != null)
		{
			shouldEmptyAddressField = false;
			String number = getArguments().getString("SipUri");
			String displayName = getArguments().getString("DisplayName");
			String photo = getArguments().getString("Photo");
			String num[] = number.split("[:]");
			if (num.length > 1)
			{
				String num1[] = num[1].split("[@]");
				// mAddress.setText(num1[0]);
				mAddress.getText().clear();
				mAddress.append(num1[0].replace(" ", ""));
			}
			else
			{
				// mAddress.setText(num[0]);
				mAddress.getText().clear();
				mAddress.append(num[0].replace(" ", ""));
			}

			if (displayName != null)
			{
				// mAddress.setDisplayedName(displayName);
			}
			if (photo != null)
			{
				// mAddress.setPictureUri(Uri.parse(photo));
			}
		}

		/*
		 * getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.
		 * SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		 */

		return view;
	}

	/**
	 * @return null if not ready yet
	 */
	public static DialerFragment instance()
	{
		return instance;
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		if (LinphoneActivity.isInstanciated())
		{
			LinphoneActivity.instance().updateDialerFragment(this);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (LinphoneActivity.isInstanciated())
		{
			// LinphoneActivity.instance().selectMenu(FragmentsAvailable.DIALER);
			LinphoneActivity.instance().updateDialerFragment(this);
		}

		if (shouldEmptyAddressField)
		{
			mAddress.setText("");
		}
		else
		{
			shouldEmptyAddressField = true;
		}
		resetLayout(isCallTransferOngoing);
	}

	/*
	 * @Override public void onCreateContextMenu(ContextMenu menu, View view,
	 * ContextMenu.ContextMenuInfo menuInfo) { Log.i("ss",
	 * "Creating context menu for view=" + view); menu.add(Menu.NONE, Menu.FIRST
	 * + 1, Menu.NONE, "Test menu"); super.onCreateContextMenu(menu, view,
	 * menuInfo); }
	 * 
	 * @Override public boolean onContextItemSelected(MenuItem item) {
	 * Log.i("s", "Context item selected as=" + item.toString()); return
	 * super.onContextItemSelected(item); }
	 */

	public void resetLayout(boolean callTransfer)
	{
		isCallTransferOngoing = callTransfer;
		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc == null) { return; }

		if (lc.getCallsNb() > 0)
		{
			if (isCallTransferOngoing)
			{
				// mCall.setImageResource(R.drawable.transfer_call);
				mCall.setExternalClickListener(transferListener);
			}
			else
			{
				// mCall.setImageResource(R.drawable.add_call);
				mCall.resetClickListener();
			}
			mAddContact.setEnabled(true);
			mAddContact.setImageResource(R.drawable.cancel);
			mAddContact.setOnClickListener(cancelListener);
		}
		else
		{
			// mCall.setImageResource(R.drawable.call);
			mAddContact.setEnabled(true);
			mAddContact.setImageResource(R.drawable.add_contact);
			mAddContact.setOnClickListener(addContactListener);
			enableDisableAddContact();
		}
	}

	public void enableDisableAddContact()
	{
		mAddContact.setEnabled(LinphoneManager.getLc().getCallsNb() > 0 || !mAddress.getText().toString().equals(""));
	}

	public void newOutgoingCall(Intent intent)
	{
		if (intent != null && intent.getData() != null)
		{
			String scheme = intent.getData().getScheme();
			if (scheme.startsWith("imto"))
			{
				mAddress.setText("sip:" + intent.getData().getLastPathSegment());
			}
			else if (scheme.startsWith("call") || scheme.startsWith("sip"))
			{
				mAddress.setText(intent.getData().getSchemeSpecificPart());
			}
			else
			{
				Log.e("Unknown scheme: ", scheme);
				mAddress.setText(intent.getData().getSchemeSpecificPart());
			}

			mAddress.clearDisplayedName();
			intent.setData(null);

			LinphoneManager.getInstance().newOutgoingCall(mAddress);
		}
	}

	@Override
	public boolean onLongClick(View v)
	{
		LinphoneActivity.instance().hideStatusBar();
	//Toast.makeText(getActivity(), "long....", Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.country:
				RemoteData remoteData = new RemoteData(SELECT_COUNTRY, DialerFragment.this);
				remoteData.setProgressDialog(getActivity());
				remoteData.execute(RemoteData.RESULT_JSON, LinphoneUtils.API_URL + "countrylist_api.php");
			break;

			default:
			break;
		}

	}

	@Override
	public void remoteCompleated(RemoteProperty rp)
	{
		switch (rp.getId())
		{

			case SELECT_COUNTRY:
				JSONArray array;
				final ArrayList<HashMap<String, String>> ctry = new ArrayList<HashMap<String, String>>();
				try
				{
					array = rp.getJsonObject().getJSONArray("listcountry");
					for (int i = 0; i < array.length(); i++)
					{
						HashMap<String, String> cntry = new HashMap<String, String>();
						cntry.put("country", array.getJSONObject(i).getString("Country"));
						ctry.add(cntry);
					}
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SimpleAdapter adapter = new SimpleAdapter(getActivity(), ctry, R.layout.country_list_item, new String[] { "country" }, new int[] { R.id.country });
				new AlertDialog.Builder(getActivity(), R.style.MyTheme).setAdapter(adapter, new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{

						if (ctry.get(which).get("country").toString().equals("Country not Supported"))
						{
							Engine.getEditor().putString(Engine.PREF.COUNTRY.name(), ctry.get(which).get("country").toString()).commit();
							Engine.getEditor().putBoolean("noSelection", true).commit();

							if (Engine.getPref().getString("Frag", "").equals("Account"))
							{
								LinphoneActivity.instance().displayAccSettings();
							}
							else
							{
								SetupActivity.instance().displayLoginGeneric();
							}

						}
						else
						{

							Engine.getEditor().putString(Engine.PREF.COUNTRY.name(), ctry.get(which).get("country").toString()).commit();
							Engine.getEditor().putBoolean("noSelection", false).commit();
							// RemoteData remoteData = new
							// RemoteData(SELECT_STATES, ShowCountry.this);
							// remoteData.setProgressDialog(getActivity());
							// remoteData.execute(RemoteData.RESULT_PLANE_TEXT,
							// LinphoneUtils.API_URL + "countrylist_api.php");
							// new GetStates().execute(o.toString());
						}
						select_contry.setText(Engine.getPref().getString(Engine.PREF.COUNTRY.name(), "UK"));
					}
				}).setPositiveButton("Okay", new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{

					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
					}
				}).setTitle("Select Your Country").setCancelable(false).show();
			break;

			default:
			break;
		}

	}
}
