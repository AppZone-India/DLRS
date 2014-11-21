package com.sim2dial.dialer;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyMenuAdapter extends SimpleAdapter
{
	Context	context;
	

	public MyMenuAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to)
	{

		super(context, items, resource, from, to);
		this.context = context;
		

	}

	int getResIco(String ss)
	{
		if (ss.equals("Exit")) return R.drawable.ic_exit;
		else if (ss.equals("Account Settings")) return R.drawable.ic_setting;
		else if (ss.equals("Call History")) return R.drawable.ic_call_history;
		else if (ss.equals("Credit History")) return R.drawable.ic_credit_history;
		else if (ss.equals("Privacy Policy")) return R.drawable.ic_privacy;
		
		else if (ss.equals("Top Up")) return R.drawable.ic_topup;
		
		else return R.drawable.ic_topup;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = super.getView(position, convertView, parent);
		TextView tv = (TextView) view.findViewById(R.id.acc_item);
		if (tv != null)
		{
			ImageView img = (ImageView) view.findViewById(R.id.imageView1);
			img.setImageResource(getResIco(tv.getText().toString()));
		}
		return view;
	}

}
