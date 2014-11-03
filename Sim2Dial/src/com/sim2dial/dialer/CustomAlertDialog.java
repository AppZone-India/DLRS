package com.sim2dial.dialer;


import com.sim2dial.dialer.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;


public class CustomAlertDialog {
	 
	
	public static  void showAlert(final Context activity,Drawable icon, View view, String title, String msg,boolean isPositive,String positiveText, boolean isNegative,String negativeText, boolean iscancilable,final ClickPos obj) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		if(icon!=null)
		builder.setIcon(icon);
		if(title!="")
		builder.setTitle(title);
		if(msg!="")
			builder.setMessage(msg);
		if(view!=null){
			ImageButton tv1 = (ImageButton) view.findViewById(R.id.button1);
			if(tv1!=null)
			tv1.setOnClickListener(new OnClickListener() { 
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(obj!=null)
						obj.clickPositive(activity);
				}
			});
			//TextView 
			tv1 = (ImageButton)view.findViewById(R.id.button2);
			if(tv1!=null)
			tv1.setOnClickListener(new OnClickListener() { 
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(obj!=null)
						obj.clickNegative(activity);
				}
			});
			builder.setView(view);
		}
		if(isPositive)
		builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(obj!=null)
				obj.clickPositive(activity);
			}
		});
		builder.setCancelable(iscancilable);
		if(isNegative)
		builder.setNegativeButton(negativeText,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if(obj!=null)
							obj.clickNegative(activity);
					}
				});
		
	//	if(view!=null){
		//	int i=-1;
//			do{
//				i++;
//				String btn = "btn" + i;
			
//			}while(i!=-1);
	//	}

		AlertDialog dialog = builder.create();
		dialog.show();

	}
	
	
}
