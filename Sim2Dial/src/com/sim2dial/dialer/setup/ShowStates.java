package com.sim2dial.dialer.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sim2dial.dialer.Engine;
import com.sim2dial.dialer.LinphoneActivity;
import com.sim2dial.dialer.LinphoneUtils;
import com.sim2dial.dialer.R;

public class ShowStates extends Fragment implements OnItemClickListener
{

	private TextView									tv1;
	public static ArrayList<HashMap<String, String>>	cities	= new ArrayList<HashMap<String, String>>();

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View v = inflater.inflate(R.layout.myaccount, container, false);

		ListView lv = (ListView) v.findViewById(R.id.account);
		lv.setBackgroundResource(R.drawable.keypad_bg);
		tv1 = (TextView) v.findViewById(R.id.head);
		tv1.setText("States");

		Button back = (Button) v.findViewById(R.id.back);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (Engine.getPref().getString("Frag", "").equals("Account"))
				{
					LinphoneActivity.instance().displayShowCountry("");
				}
				else
				{
					SetupActivity.instance().displayCountry();
				}
			}
		});

		// ArrayAdapter<HashMap<String, String>> adap=new
		// ArrayAdapter<HashMap<String,String>>(getActivity(),
		// android.R.layout.simple_list_item_checked,ShowCountry.states);
		MySimpleAdapter adap = new MySimpleAdapter(getActivity(), ShowCountry.states, R.layout.listitem, new String[] { "state" }, new int[] { R.id.textView1 });
		lv.setAdapter(adap);
		lv.setOnItemClickListener(this);
		return v;
	}

	class MySimpleAdapter extends SimpleAdapter
	{

		public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
		{
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View v = super.getView(position, convertView, parent);

			ImageView imv = (ImageView) v.findViewById(R.id.imageView1);
			String state = Engine.getPref().getString("state", "");

			HashMap<String, String> sel = (HashMap<String, String>) getItem(position);
			String stat = sel.get("state");

			if (state.equals(stat))
			{
				imv.setVisibility(View.VISIBLE);
			}
			else
			{
				imv.setVisibility(View.INVISIBLE);
			}
			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adap, View v, int pos, long arg3)
	{
		Object o = adap.getItemAtPosition(pos);

		String ctry = Engine.getPref().getString("country", "");
		String split1[] = o.toString().split("[,]");
		if (split1.length > 0)
		{
			String split11[] = split1[1].replace("}", "").split("[=]");
			Engine.getEditor().putString("state", split11[1]).commit();
			String split2[] = split1[0].split("[{]");
			if (split2.length > 0)
			{
				String split3[] = split2[1].split("[=]");
				Engine.getEditor().putString("stateid", split3[1]).commit();
				new GetCities().execute(ctry, split3[1]);
			}
		}
	}

	public class GetCities extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>
	{

		ArrayList<HashMap<String, String>>	hlist	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>				hmap	= null;

		ProgressDialog						pb;
		private String						res;

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
			String str = LinphoneUtils.getCities(params[0], params[1]);
			try
			{
				JSONObject pjobj = new JSONObject(str);
				res = pjobj.getString("result");
				JSONArray jarr = pjobj.getJSONArray("cities");
				for (int i = 0; i < jarr.length(); i++)
				{
					JSONObject jobj = jarr.getJSONObject(i);
					String city = jobj.getString("city");
					String id = jobj.getString("id");
					hmap = new HashMap<String, String>();
					hmap.put("city", city.trim());
					hmap.put("id", id.trim());
					hlist.add(hmap);
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
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
				cities = result;
				/*
				 * if(cities.equals("")){ Toast.makeText(getActivity(),
				 * "Nothing to display", 20).show(); }
				 */

				if (res.equals("0"))
				{
					Toast.makeText(getActivity(), "No states for this country", 20).show();
					new Thread()
					{
						public void run()
						{
							try
							{
								Thread.sleep(3500);
								if (Engine.getPref().getString("Frag", "").equals("Account"))
								{
									LinphoneActivity.instance().displayShowCountry("");
								}
								else
								{
									SetupActivity.instance().displayCountry();
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
						LinphoneActivity.instance().displaycities();
					}
					else
					{
						SetupActivity.instance().displaycities();
					}
				}
			}
		}

	}

}
