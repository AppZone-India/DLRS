package com.sim2dial.dialer.setup;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ns.kgraphicsengin.RemoteData;
import com.ns.kgraphicsengin.RemoteData.OnRemoteCompleated;
import com.ns.kgraphicsengin.RemoteData.RemoteProperty;
import com.sim2dial.dialer.Engine;
import com.sim2dial.dialer.LinphoneActivity;
import com.sim2dial.dialer.LinphoneUtils;
import com.sim2dial.dialer.R;

public class ShowCountry extends Fragment implements OnItemClickListener,OnRemoteCompleated
{
	private final int SELECT_STATES=0x3;

	public static ArrayList<HashMap<String, String>>	states	= new ArrayList<HashMap<String, String>>();
	String												ctry[]	= null;
	private TextView									tv1, tv2;
	private String										arg;

	// public static String res;
	// private static ShowCountry instance;

	/*
	 * public static ShowCountry instance() { if (instance != null) return
	 * instance; throw new RuntimeException("Show Country is not instantiated");
	 * }
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View v = inflater.inflate(R.layout.myaccount, container, false);
		ListView lv = (ListView) v.findViewById(R.id.account);
		lv.setBackgroundResource(R.drawable.keypad_bg);
		tv1 = (TextView) v.findViewById(R.id.head);
		tv1.setText("Country");

		tv2 = (TextView) v.findViewById(R.id.info);
		tv2.setVisibility(View.VISIBLE);

		Button back = (Button) v.findViewById(R.id.back);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (Engine.getPref().getString("Frag", "").equals("Account"))
				{
					LinphoneActivity.instance().displayAccSettings();
				}
				else
				{
					SetupActivity.instance().displayLoginGeneric();
				}
			}
		});

		// instance = this;
//		if (Engine.getPref().getString("Frag", "").equals("Account"))
//		{
//			ctry = AccountSetting.clist.split("[,]");
//		}
//		else
//		{
			JSONArray array;
			try
			{
				array = new JSONObject(getArguments().getString("listcountry")).getJSONArray("listcountry");

				ctry = new String[array.length()];
				for (int i = 0; i < array.length(); i++)
				{
					ctry[i] = array.getJSONObject(i).getString("Country");
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}

		MyArrayAdapter adap = new MyArrayAdapter(getActivity(), R.layout.myaccount, ctry);
		lv.setAdapter(adap);
		lv.setOnItemClickListener(this);
		return v;
	}

	class MyArrayAdapter extends ArrayAdapter<String>
	{

		int	resid;

		public MyArrayAdapter(Context context, int textViewResourceId, String[] ctry)
		{
			super(context, textViewResourceId);
			resid = textViewResourceId;
		}

		@Override
		public int getCount()
		{
			return ctry.length;
		}

		@Override
		public String getItem(int position)
		{
			return ctry[position];
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View v = convertView;
			if (v == null)
			{
				LayoutInflater inflate = getActivity().getLayoutInflater();
				v = inflate.inflate(R.layout.listitem, null);
			}

			String ctry1 = ctry[position];
			TextView tv = (TextView) v.findViewById(R.id.textView1);
			tv.setText(ctry1);

			ImageView imv = (ImageView) v.findViewById(R.id.imageView1);
			String country1 = Engine.getPref().getString("country", "UK");
			boolean bnl = Engine.getPref().getBoolean("noSelection", false);

			/*
			 * if(country1.equals("")){ if(ctry1.equals("No Selection")){
			 * imv.setVisibility(View.VISIBLE); } }else
			 */
			if (country1.equals(ctry1))
			{
				imv.setVisibility(View.VISIBLE);
			}
			else
			{
				imv.setVisibility(View.INVISIBLE);
			}

			/*
			 * if(country1.equals("UK") && bnl){
			 * imv.setVisibility(View.VISIBLE); }
			 */

			return v;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adap, View v, int pos, long arg3)
	{

		Object o = adap.getItemAtPosition(pos);

		if (o.toString().equals("Country not Supported"))
		{
			Engine.getEditor().putString("country", o.toString()).commit();
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

			Engine.getEditor().putString("country", o.toString()).commit();
			Engine.getEditor().putBoolean("noSelection", false).commit();
			RemoteData remoteData = new RemoteData(SELECT_STATES, ShowCountry.this);
			remoteData.setProgressDialog(getActivity());
			remoteData.execute(RemoteData.RESULT_PLANE_TEXT, LinphoneUtils.API_URL + "countrylist_api.php");			
			//new GetStates().execute(o.toString());
		}
	}

	@Override
	public void remoteCompleated(RemoteProperty remoteProperty)
	{
		switch (remoteProperty.getId())
		{
			case SELECT_STATES:
			
			break;

			default:
			break;
		}
		
	}

	/*public class GetStates extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>
	{

		ArrayList<HashMap<String, String>>	hlist	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>				hmap	= null;

		ProgressDialog						pb;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pb = new ProgressDialog(getActivity());
			pb.show();
			pb.setContentView(new ProgressBar(getActivity()), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params)
		{
			String str = LinphoneUtils.getStates(params[0]);
			try
			{
				JSONObject pjobj = new JSONObject(str);
				res = pjobj.getString("result");
				JSONArray jarr = pjobj.getJSONArray("states");

				for (int i = 0; i < jarr.length(); i++)
				{
					JSONObject jobj = jarr.getJSONObject(i);
					String state = jobj.getString("state");
					String id = jobj.getString("id");
					hmap = new HashMap<String, String>();
					hmap.put("state", state.trim());
					hmap.put("id", id.trim());
					hlist.add(hmap);
				}

			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			return hlist;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result)
		{
			super.onPostExecute(result);
			pb.dismiss();
			if (result != null)
			{
				states = result;
				
				 * if(states.equals(" ")){ Toast.makeText(getActivity(),
				 * "Nothing to display", 20).show(); }else
				 

				if (res.equals("0"))
				{
					// Toast.makeText(getActivity(),
					// "No states for this country", Toast.LENGTH_LONG).show();
					new Thread()
					{
						public void run()
						{
							try
							{
								Thread.sleep(100);
								if (Engine.getPref().getString("Frag", "").equals("Account"))
								{
									LinphoneActivity.instance().displayAccSettings();
								}
								else
								{
									SetupActivity.instance().displayLoginGeneric();
								}
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}.start();

				}
				else if (res.equals("1"))
				{
					if (Engine.getPref().getString("Frag", "").equals("Account"))
					{
						LinphoneActivity.instance().displayStates();
					}
					else
					{
						SetupActivity.instance().displayStates();
					}
				}

			}
		}

	}*/

}
