package com.sim2dial.dialer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sim2dial.dialer.db.HistoryStatus;
import com.sim2dial.dialer.db.MyContentProvider;

public class MyHSimpleCAdapter extends SimpleCursorAdapter {

	Cursor c1;
	Context ctxt;
	String name, no, date, time, status;
	private String dur;
	private String timestamp;
	long i = 0, j = 0;

	public MyHSimpleCAdapter(Context context, int layout, Cursor c1,
			String[] from, int[] to) {
		super(context, layout, c1, from, to);
		c1 = context.getContentResolver().query(MyContentProvider.Content_Uri,
				null, null, null, null);
		this.ctxt = context;

	}

	@Override
	public View getView(int pos, View arg1, ViewGroup arg2) {
		View v = super.getView(pos, arg1, arg2);
		ImageView detail = (ImageView) v.findViewById(R.id.detail);
		RelativeLayout tv = (RelativeLayout) v.findViewById(R.id.placeholder);
		final TextView rowid = (TextView) v.findViewById(R.id.rowid);

		// c.moveToPosition(pos);
		// tv.setText(HistoryStatus.Column_No);
		// v.setTag(detail);
		detail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String id = rowid.getText().toString();
				// Uri getrow=Uri.parse(MyContentProvider.Content_Uri+"/"+id);
				String[] projection = { HistoryStatus.Column_ID,
						HistoryStatus.Column_Name, HistoryStatus.Column_Date,
						HistoryStatus.Column_Duration, HistoryStatus.Column_No,
						HistoryStatus.Column_Status, HistoryStatus.Column_Time,
						HistoryStatus.Column_Timestamp };
				Cursor c = ctxt.getContentResolver().query(
						MyContentProvider.Content_Uri, projection,
						HistoryStatus.Column_ID + "=?", new String[] { id },
						null);
				if (c != null) {
					c.moveToFirst();
					no = c.getString(c.getColumnIndex(HistoryStatus.Column_No));
					name = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Name));
					date = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Date));
					time = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Time));
					timestamp = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Timestamp));
					dur = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Duration));
					status = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Status));
					c.close();
					LinphoneActivity.instance().setAddressAndGoToDialer(no);
					/*LinphoneActivity.instance().setAddresGoToDialerAndCall(no,
							name, null);*/
				} else {

				}
			}

		});

		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String id = rowid.getText().toString();
				// Uri getrow=Uri.parse(MyContentProvider.Content_Uri+"/"+id);
				String[] projection = { HistoryStatus.Column_ID,
						HistoryStatus.Column_Name, HistoryStatus.Column_Date,
						HistoryStatus.Column_Duration, HistoryStatus.Column_No,
						HistoryStatus.Column_Status, HistoryStatus.Column_Time,
						HistoryStatus.Column_Timestamp };
				Cursor c = ctxt.getContentResolver().query(
						MyContentProvider.Content_Uri, projection,
						HistoryStatus.Column_ID + "=?", new String[] { id },
						null);
				if (c != null) {
					c.moveToFirst();
					no = c.getString(c.getColumnIndex(HistoryStatus.Column_No));
					name = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Name));
					date = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Date));
					time = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Time));
					timestamp = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Timestamp));
					dur = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Duration));
					status = c.getString(c
							.getColumnIndex(HistoryStatus.Column_Status));
					c.close();
					LinphoneActivity.instance().displayHistoryDetails(date,
							time, dur, name, no, status, timestamp);

				} else {
					// Toast.makeText(ctxt, , 20).show();
				}

			}
		});

		return v;
	}

	@Override
	public void bindView(View v, final Context ctxt, Cursor c) {
		super.bindView(v, ctxt, c);
		LinearLayout llp = (LinearLayout) v.findViewById(R.id.parview);
		RelativeLayout rlp = (RelativeLayout) v.findViewById(R.id.placeholder);
		ImageView stat = (ImageView) v.findViewById(R.id.icon);
		ImageView detail = (ImageView) v.findViewById(R.id.detail);
		TextView tv = (TextView) v.findViewById(R.id.sipUri);
		TextView tvn = (TextView) v.findViewById(R.id.dur);
		TextView tvd = (TextView) v.findViewById(R.id.dat);
		TextView tvt = (TextView) v.findViewById(R.id.time);
		TextView sep = (TextView) v.findViewById(R.id.separator);

		TextView rowid = (TextView) v.findViewById(R.id.rowid);

		// c.moveToPosition(pos);

		no = c.getString(c.getColumnIndex(HistoryStatus.Column_No));
		name = c.getString(c.getColumnIndex(HistoryStatus.Column_Name));
		date = c.getString(c.getColumnIndex(HistoryStatus.Column_Date));
		time = c.getString(c.getColumnIndex(HistoryStatus.Column_Duration));
		status = c.getString(c.getColumnIndex(HistoryStatus.Column_Status));
		i++;
		if (name.length() < 1) {

			tv.setText(no);
		} else {
			tv.setText(name);
		}

		Calendar cl = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String cdate = df.format(cl.getTime());
		/*
		 * Date d1=new Date(date); int yd=d1.getDay();
		 * 
		 * Date d2=new Date();
		 */
		if (cdate.equals(date)) {
			if (i == 1) {
				sep.setVisibility(View.VISIBLE);
			} else {
				sep.setVisibility(View.GONE);
			}
			tvd.setText("Today");
		} else {
			j++;
			if (j == 1) {
				sep.setVisibility(View.VISIBLE);
			} else {
				sep.setVisibility(View.GONE);
			}
			tvd.setText(date);
		}

		if (status.equals("Outgoing")) {
			stat.setImageResource(R.drawable.call_status_outgoing);
		} else if (status.equals("Incoming")) {
			stat.setImageResource(R.drawable.call_status_incoming);
		} else if (status.equals("Missed")) {
			stat.setImageResource(R.drawable.call_status_missed);
		}

		tvn.setText(no);
		tvt.setText(time);

		rowid.setText(c.getString(c.getColumnIndex(HistoryStatus.Column_ID)));
		v.setTag(detail);
		v.setTag(tv);
		v.setTag(rowid);

		/*
		 * detail.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * LinphoneActivity.instance().displayHistoryDetails
		 * (date,time,dur,name,no,status,timestamp); } });
		 */
		/*
		 * tv.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * LinphoneActivity.instance().setAddresGoToDialerAndCall(no, name,
		 * null); } });
		 */

		rlp.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				/* menu.add(0, v.getId(), 0, ctxt.getString(R.string.delete)); */
			}
		});

	}
}