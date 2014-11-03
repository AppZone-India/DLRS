package com.sim2dial.dialer;
/*
AboutFragment.java
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

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * @author Sylvain Berfini
 */
public class AboutFragment extends Fragment{
	private Handler mHandler = new Handler();
	private FragmentsAvailable about = FragmentsAvailable.ABOUT_INSTEAD_OF_CHAT;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.webaddres, container, false);
		TextView tv=(TextView)view.findViewById(R.id.textView1);
		tv.setText("About Us");
		WebView web=(WebView)view.findViewById(R.id.webView1);
		if(getArguments()!=null){
			String abouturl=getArguments().getString("about");
			web.getSettings().setJavaScriptEnabled(true);
			web.loadUrl(abouturl);
		}
		return view;
	}
	
}
