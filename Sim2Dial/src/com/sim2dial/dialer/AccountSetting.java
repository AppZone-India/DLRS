package com.sim2dial.dialer;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountSetting extends DialogFragment
{

	private EditText		useret, passet;
	private TextView		country;
	private Button			login;
	private RelativeLayout	sel_ctry;
	public static String	clist;

	static AccountSetting newInstance()
	{
		AccountSetting f = new AccountSetting();

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View v = inflater.inflate(R.layout.account_setting, container, false);
		useret = (EditText) v.findViewById(R.id.idet);
		passet = (EditText) v.findViewById(R.id.passet);
		country = (TextView) v.findViewById(R.id.country);
		sel_ctry = (RelativeLayout) v.findViewById(R.id.sel_ctry);

		login = (Button) v.findViewById(R.id.save);
		String user = Engine.getPref().getString("loginid", "");
		String pass = Engine.getPref().getString("pass", "");

		useret.setText(user);
		passet.setText(pass);

		String country1 = Engine.getPref().getString("country", "");
		String state = Engine.getPref().getString("state", "");
		String city = Engine.getPref().getString("city", "");
		boolean bnl = Engine.getPref().getBoolean("noSelection", false);

		String msg = "UK";
		if (bnl)
		{
			country.setText("Country not Supported");
		}
		else
		{
			if (country1.length() != 0)
			{
				msg = country1;
			}
			else if (state.length() != 0)
			{
				msg += "\\" + state;
			}
			else if (city.length() != 0)
			{
				msg += "\\" + city;
			}

			country.setText(msg);
		}

		// ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED,
		// InputMethodManager.HIDE_IMPLICIT_ONLY);

		sel_ctry.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Engine.getEditor().putString("loginid", useret.getText().toString()).commit();
				Engine.getEditor().putString("pass", passet.getText().toString()).commit();
				Engine.getEditor().putString("Frag", "Account").commit();
				if (LinphoneUtils.isHightBandwidthConnection(getActivity())) new GetCountry().execute();
				else Toast.makeText(getActivity(), "Check your Wifi/Data Connection", 20).show();

			}
		});

		login.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String u, p;
				u = useret.getText().toString();
				p = passet.getText().toString();
//				if (LinphoneUtils.isHightBandwidthConnection(getActivity())) new GetLogin().execute(u, p);
//				else Toast.makeText(getActivity(), "Check your Wifi/Data Connection", 20).show();
			}
		});

		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		return v;
	}

	/*public class GetLogin extends AsyncTask<String, Void, String>
	{

		// private HashMap<String, String> hmap=new HashMap<String, String>();
		ProgressDialog	pb;
		String			pass	= "";

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pb = new ProgressDialog(getActivity());
			pb.show();
			LayoutParams prm = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			ProgressBar bar = new ProgressBar(getActivity());

			pb.setContentView(bar, prm);

		}

		@Override
		protected String doInBackground(String... params)
		{
			return LinphoneUtils.getLogin(params[0], params[1]);

		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			pb.dismiss();
			if (result != null)
			{
				JSONObject jobj;
				try
				{
					jobj = new JSONObject(result).getJSONArray("listclient").getJSONObject(0);
					String sippassword = jobj.getString("sippassword");
					String idclient = jobj.getString("idClient");
					String status = jobj.getString("status");
					if (status.equals("OK") || status.equals("New Application Id Generated"))
					{
						if (jobj.has("aid") && !jobj.getString("aid").equals(""))
						{
							Engine.getEditor().putString("aid", jobj.getString("aid")).commit();
							Toast.makeText(getActivity(), status, 2000).show();
						}

						Engine.getEditor().putString(getString(R.string.pref_username_key), useret.getText().toString()).commit();
						Engine.getEditor().putString(getString(R.string.pref_passwd_key), sippassword).commit();
						Engine.getEditor().putString("loginid", useret.getText().toString()).commit();
						Engine.getEditor().putString("pass", passet.getText().toString()).commit();
						Engine.getEditor().putString("idClient", idclient).commit();
						// ed.putString("country",country.getText().toString());

						LinphoneActivity.instance().applyConfigChangesIfNeeded();

						// LinphoneUtils.getBalance(getActivity());
						// LinphoneActivity.instance().refreshStatus(OnlineStatus.Online);
						LinphoneActivity.instance().displayMenuSettings();
					}
					else Toast.makeText(getActivity(), status, 2000).show();
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// if (res.length > 1)
				// {
				// if (res[1] != null)
				// {
				//
				// }
				// }
				// else
				// {
				// Toast.makeText(getActivity(), "Authentication Failed", 20)
				// .show();
				// }
			}
			else
			{
				Toast.makeText(getActivity(), "Data not found", 20).show();
			}
		}
	}*/

	public class GetCountry extends AsyncTask<String, Void, String>
	{

		ArrayList<HashMap<String, String>>	hlist	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>				hmap	= null;

		ProgressDialog						pb;
		private String						result;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pb = new ProgressDialog(getActivity());
			pb.show();
			pb.setContentView(new ProgressBar(getActivity()), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}

		@Override
		protected String doInBackground(String... params)
		{
			String str = LinphoneUtils.getCountry();
			try
			{
				JSONObject pjobj = new JSONObject(str);
				String country = "Country not Supported," + pjobj.getString("countries");
				result = pjobj.getString("result");
				clist = /* result+"|"+ */country;
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			pb.dismiss();
			if (result != null && result.equals("1"))
			{
				LinphoneActivity.instance().displayShowCountry("");
			}
			else
			{
				Toast.makeText(getActivity(), "Nothing to display", 20).show();
			}
		}

	}

	@Override
	public void onPause()
	{
		super.onPause();

		if (LinphoneActivity.isInstanciated())
		{
			LinphoneActivity.instance().applyConfigChangesIfNeeded();
		}
	}

}
