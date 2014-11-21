package com.sim2dial.dialer.setup;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.kgraphicsengin.RemoteData;
import com.ns.kgraphicsengin.RemoteData.OnRemoteCompleated;
import com.ns.kgraphicsengin.RemoteData.RemoteProperty;
import com.sim2dial.dialer.Engine;
import com.sim2dial.dialer.LinphoneActivity;
import com.sim2dial.dialer.LinphoneUtils;
import com.sim2dial.dialer.R;
import com.sim2dial.dialer.util.Theme;

public class GenericLoginFragment extends Fragment implements OnClickListener, OnRemoteCompleated
{
	private final int	LOGIN			= 0x1;
	private final int	SELECT_COUNTRY	= 0x2;
	private EditText	login, password;
	private TextView	sel_cntry;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view;
		try
		{
			if (getArguments().getString("type").equals("update")) 
				{
				view = inflater.inflate(R.layout.account_setting, container, false);
				view.findViewById(R.id.login).setBackground(Theme.selectorDrawable("shp_voipcall"));
					}
			else 
				{
				view = inflater.inflate(R.layout.setup_generic_login, container, false);
				
				TextView textView=(TextView) view.findViewById(R.id.policy);
				textView.setLinkTextColor(0xffffffff);
				textView.setClickable(true);
				textView.setMovementMethod(LinkMovementMethod.getInstance());
				String text = getString(R.string.login_msg)+"<a href='http://sim2dial.com/terms/'> Service &amp; Privacy Policy </a>.";
				textView.setText(Html.fromHtml(text));
			
				}
		}
		catch (Exception e)
		{
			view = inflater.inflate(R.layout.setup_generic_login, container, false);
		
			TextView textView=(TextView) view.findViewById(R.id.policy);
			textView.setLinkTextColor(0xffffffff);
			textView.setClickable(true);
			textView.setMovementMethod(LinkMovementMethod.getInstance());
			String text = getString(R.string.login_msg)+"<a href='http://sim2dial.com/terms/'> Service &amp; Privacy Policy </a>.";
			textView.setText(Html.fromHtml(text));
		
		}

		login = (EditText) view.findViewById(R.id.idet);
		password = (EditText) view.findViewById(R.id.passet);
		sel_cntry = (TextView) view.findViewById(R.id.sel_cntry);
		view.findViewById(R.id.login).setOnClickListener(this);
		view.findViewById(R.id.ll_cntry).setOnClickListener(this);

		login.setText(Engine.getPref().getString(Engine.PREF.LOGIN_ID.name(), ""));
		password.setText(Engine.getPref().getString(Engine.PREF.PASSWORD.name(), ""));
		if (Engine.getPref().getBoolean("noSelection", false)) sel_cntry.setText("Country not Supported");
		else sel_cntry.setText(Engine.getPref().getString(Engine.PREF.COUNTRY.name(), "Please Select Country"));

		return view;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.login:
				if (Engine.isOnline())
				{
					if (login.getText().toString().trim().equals("") || password.getText().toString().trim().equals(""))
					{
						Toast.makeText(getActivity(), getString(R.string.first_launch_no_login_password), Toast.LENGTH_LONG).show();
						return;
					}
					else
					{
						
						if (!Engine.getPref().getString(Engine.PREF.LOGIN_ID.name(), "").equals(login.getText().toString().trim())
								|| !Engine.getPref().getString(Engine.PREF.PASSWORD.name(), "").equals(password.getText().toString().trim()))
						{
							Engine.getEditor().putString(Engine.PREF.APP_ID.name(), "").commit();
							Engine.getEditor().putString(Engine.PREF.LOGIN_ID.name(), login.getText().toString()).commit();
							Engine.getEditor().putString(Engine.PREF.PASSWORD.name(), password.getText().toString()).commit();
						}
						RemoteData remoteData = new RemoteData(LOGIN, GenericLoginFragment.this);
						remoteData.setProgressDialog(getActivity(),R.style.ProgressBar);
						remoteData.execute(RemoteData.RESULT_JSON, LinphoneUtils.API_URL + "login_api.php?user=" + login.getText().toString() + "&password=" + password.getText().toString() + "&aid="
								+ (Engine.getPref().getString(Engine.PREF.APP_ID.name(), "").equals("null")?"":Engine.getPref().getString(Engine.PREF.APP_ID.name(), "")));
						// new GetLogin().execute(login.getText().toString(),
						// password.getText().toString());
						// SetupActivity.instance().genericLogIn(login.getText().toString(),password.getText().toString(),"server.nextstag.com");

					}
				}
				else Toast.makeText(getActivity(), "Check Your Data Connection", Toast.LENGTH_LONG).show();

			/*
			 * SetupActivity.instance().genericLogIn(login.getText().toString(),
			 * password.getText().toString(), domain.getText().toString());
			 * getActivity().finish();
			 */

			break;
			case R.id.ll_cntry:
				Engine.getEditor().putString(Engine.PREF.LOGIN_ID.name(), login.getText().toString()).commit();
				Engine.getEditor().putString(Engine.PREF.PASSWORD.name(), password.getText().toString()).commit();
				RemoteData remoteData = new RemoteData(SELECT_COUNTRY, GenericLoginFragment.this);
				remoteData.setProgressDialog(getActivity(),R.style.ProgressBar);
				remoteData.execute(RemoteData.RESULT_JSON, LinphoneUtils.API_URL + "countrylist_api.php");
			// new GetCountry(getActivity()).execute();
			break;
			default:
			break;
		}

	}

	@Override
	public void remoteCompleated(RemoteProperty result)
	{

		switch (result.getId())
		{

			case LOGIN:
				JSONObject jobj;
				try
				{
					// {"listclient":[{"sippassword":"UJ689175XX","idClient":"105","aid":"395704","status":"New Application Id Generated"}]}
					// "status":"No of Devices Exceed"}]}
					// {"listclient":[{"sippassword":"RC305874ZZ","idClient":"42810","aid":"766930","status":"New Application Id Generated"}]}
					jobj = result.getJsonObject().getJSONArray("listclient").getJSONObject(0);
					String status = jobj.getString("status");
					if (status.equals("OK") || status.equals("New Application Id Generated"))
					{
						if (jobj.has("aid") && !jobj.getString("aid").equals(""))
						{
							Engine.getEditor().putString(Engine.PREF.APP_ID.name(), jobj.getString("aid")).commit();
							Toast.makeText(getActivity(), status, 2000).show();
						}
						Engine.getEditor().putString(Engine.PREF.ID_CLIENT.name(), jobj.getString("idClient")).commit();
						SetupActivity.instance().genericLogIn(login.getText().toString(), jobj.getString("sippassword"), LinphoneUtils.SIP_SERVER);
						getActivity().finish();
					}
					else Toast.makeText(getActivity(), status, 2000).show();

				}
				catch (JSONException e)
				{
					Toast.makeText(getActivity(), "Data not found", 2000).show();
				}
			break;
			case SELECT_COUNTRY:
				JSONArray array;
				final ArrayList<HashMap<String, String>> ctry = new ArrayList<HashMap<String, String>>();
				try
				{
					array = result.getJsonObject().getJSONArray("listcountry");
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
						sel_cntry.setText(Engine.getPref().getString(Engine.PREF.COUNTRY.name(), "Please Select Country"));
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
			// SetupActivity.instance().displayCountry(result.getPlaneData());
			break;
			default:
			break;
		}

	}

}
