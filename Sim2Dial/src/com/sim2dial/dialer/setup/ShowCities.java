package com.sim2dial.dialer.setup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sim2dial.dialer.Engine;
import com.sim2dial.dialer.LinphoneActivity;
import com.sim2dial.dialer.R;

public class ShowCities extends Fragment implements OnItemClickListener
{

	private TextView	tv1;

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
				LinphoneActivity.instance().displayStates();
			}
		});

		// ArrayAdapter<HashMap<String, String>> adap=new
		// ArrayAdapter<HashMap<String,String>>(getActivity(),
		// android.R.layout.simple_list_item_checked,ShowCountry.states);
		//MySimpleAdapter adap = new MySimpleAdapter(getActivity(), ShowStates.cities, R.layout.listitem, new String[] { "city" }, new int[] { R.id.textView1 });
		//lv.setAdapter(adap);
		//lv.setOnItemClickListener(this);
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
			String city = Engine.getPref().getString("city", "");

			HashMap<String, String> sel = (HashMap<String, String>) getItem(position);
			String stat = sel.get("city");

			if (city.equals(stat))
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

		String split1[] = o.toString().split("[,]");
		if (split1.length > 0)
		{
			String split11[] = split1[1].replace("}", "").split("[=]");
			Engine.getEditor().putString("city", split11[1]).commit();
			String split2[] = split1[0].split("[{]");
			if (split2.length > 0)
			{
				String split3[] = split2[1].split("[=]");
				Engine.getEditor().putString("cityid", split3[1]).commit();
			}
		}

		if (Engine.getPref().getString("Frag", "").equals("Account"))
		{
			Engine.getEditor().putBoolean("noSelection", false).commit();
			LinphoneActivity.instance().displayAccSettings();

		}
		else
		{
			Engine.getEditor().putBoolean("noSelection", false).commit();
			SetupActivity.instance().displayLoginGeneric();
		}

	}
}
