package com.sim2dial.dialer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class News extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.webaddres, container, false);
		TextView tv=(TextView)view.findViewById(R.id.textView1);
		tv.setText("News");
		WebView web=(WebView)view.findViewById(R.id.webView1);
		if(getArguments()!=null){
			String abouturl=getArguments().getString("news");
			web.getSettings().setJavaScriptEnabled(true);
			web.loadUrl(abouturl);
		}
		return view;
	}
}
