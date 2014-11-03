package com.sim2dial.dialer;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ShowCountry1 extends Fragment implements OnItemClickListener
{

	String				ctry[]	= { "AUS", "USA", "UK", "No Selection" };
	private TextView	tv1;
	private String		arg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View v = inflater.inflate(R.layout.myaccount, container, false);
		ListView lv = (ListView) v.findViewById(R.id.account);
		lv.setBackgroundResource(R.drawable.keypad_bg);
		tv1 = (TextView) v.findViewById(R.id.head);
		tv1.setText("Country");
		if (getArguments() != null)
		{
			arg = getArguments().getString("login");
		}
		ArrayAdapter<String> adap = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_checked, ctry);
		lv.setAdapter(adap);
		lv.setOnItemClickListener(this);
		return v;
	}

	@Override
	public void onItemClick(AdapterView<?> adap, View v, int pos, long arg3)
	{
		Object o = adap.getItemAtPosition(pos);

		Engine.getEditor().putString("country", o.toString()).commit();
		if (arg.equals("login"))
		{
			new GetStates().execute(o.toString());
		}
		else
		{
			LinphoneActivity.instance().displayAccSettings();
			// Toast.makeText(getActivity(), o.toString(), 20).show();
		}
	}

	public class GetStates extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>
	{

		ArrayList<HashMap<String, String>>	hlist	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>				hmap	= null;

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params)
		{
			String str = LinphoneUtils.getStates(params[0]);
			try
			{
				JSONObject pjobj = new JSONObject(str);
				JSONArray jarr = pjobj.getJSONArray("states");
				for (int i = 0; i < jarr.length(); i++)
				{
					JSONObject jobj = jarr.getJSONObject(i);
					String state = jobj.getString("state");
					String id = jobj.getString("id");
					hmap.put("state", state);
					hmap.put("id", id);
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

			LayoutInflater inflater = getActivity().getLayoutInflater();
			View vi = inflater.inflate(R.layout.refillhisto, null);
			TextView tv = (TextView) vi.findViewById(R.id.textView1);
			tv.setText("States");
			Button b1 = (Button) vi.findViewById(R.id.refilhistoback);
			b1.setVisibility(View.GONE);
			ListView lv = (ListView) vi.findViewById(R.id.history);
			SimpleAdapter adap = new SimpleAdapter(getActivity(), result, android.R.layout.simple_list_item_1, new String[] { "state" }, new int[] { android.R.id.text1 });
			lv.setAdapter(adap);
		}

	}

}
