package com.sim2dial.dialer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.PullAndLoadListView;
import com.costum.android.widget.PullAndLoadListView.OnLoadMoreListener;
import com.costum.android.widget.PullToRefreshListView.OnRefreshListener;
import com.ns.kgraphicsengin.RemoteData;
import com.ns.kgraphicsengin.RemoteData.OnRemoteCompleated;
import com.ns.kgraphicsengin.RemoteData.RemoteProperty;

public class MyAccount extends Fragment implements OnRemoteCompleated, OnItemClickListener, OnRefreshListener, OnLoadMoreListener
{
	Button								rateBack, refilHistoBack;
	public Button						refresh;
	FrameLayout							contentPlaceHolder;

	private final int					DISPLAY_ACCOUNT			= 0;
	// private final int DISPLAY_CALLHISTORY = DISPLAY_ACCOUNT + 1;
	private final int					DISPLAY_CREDITHISTORY	= DISPLAY_ACCOUNT + 1;
	private final int					DISPLAY_TOP_UP			= DISPLAY_CREDITHISTORY + 1;
	private final int					DISPLAY_PRIVACY_POLICY	= DISPLAY_TOP_UP + 1;
	private final int					LOGOUT					= DISPLAY_PRIVACY_POLICY + 1;
	private final int					DISPLAY_NEWS			= DISPLAY_CREDITHISTORY + 1;
	private final int					DISPLAY_ABOUT			= DISPLAY_NEWS + 1;

	ArrayList<HashMap<String, String>>	list					= new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>>	mainlist				= new ArrayList<HashMap<String, String>>();

	String								numForRate				= "";
	static String						numForVoucher			= "";
	static String						num1					= "";
	static String						num2					= "";
	TextView							sel, des, cur, dilpre;
	ListView							account;
	PullAndLoadListView					lv;
	SimpleAdapter						adapter;
	MyMenuAdapter						mainadapter;
	ImageButton							chsn1, chsn2;
	DateFormat							dateFormat				= new SimpleDateFormat("dd/MM/yyyy");
	Date								startDate, endDate;
	long								dateofset				= 1728000000l, oneday = 86400000l;
	boolean								savaData				= true;
	private final static int			CHANGE_PREFS			= 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
	{
		View v = inflater.inflate(R.layout.accountplaceholder, container, false);

		contentPlaceHolder = (FrameLayout) v.findViewById(R.id.placeholder);
		createMainList("Account Settings", "Credit History", "Top Up", "Privacy Policy", "Exit");
		setProfile();
		return v;
	}

	private void createMainList(String... args)
	{
		mainlist.clear();
		for (String item : args)
		{
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("acc_item", item);
			mainlist.add(hm);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == CHANGE_PREFS)
		{
			// getActivity().sendBroadcast(new
			// Intent(SipManager.ACTION_SIP_REQUEST_RESTART));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setProfile()
	{
		account = new ListView(getActivity());
		mainadapter = new MyMenuAdapter(getActivity(), mainlist, R.layout.accountlist_item, new String[] { "acc_item" }, new int[] { R.id.acc_item });
		account.setAdapter(mainadapter);
		account.setOnItemClickListener(this);
		mainadapter.notifyDataSetChanged();
		contentPlaceHolder.removeAllViews();
		contentPlaceHolder.addView(account);

	}

	/*
	 * class CallHisto extends AsyncTask<String, String,
	 * ArrayList<HashMap<String, String>>> {
	 * 
	 * ArrayList<HashMap<String, String>> hlist = new ArrayList<HashMap<String,
	 * String>>(); HashMap<String, String> hmap = null;
	 * 
	 * ProgressDialog pb;
	 * 
	 * @Override protected void onPreExecute() { super.onPreExecute(); pb = new
	 * ProgressDialog(getActivity()); pb.show(); pb.setContentView(new
	 * ProgressBar(getActivity()), new LayoutParams(LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT)); }
	 * 
	 * @Override protected ArrayList<HashMap<String, String>>
	 * doInBackground(String... params) {
	 * 
	 * 
	 * calledfrom: "jineed", calledto: "00447766742689", date:
	 * "2013-01-11 12:30:30.0", duration: "13", currency: "GBP", cost: "0.0043"
	 * 
	 * 
	 * String str = LinphoneUtils.getCallHistory(params[0], params[1],
	 * params[2]); try { JSONObject pjobj = new JSONObject(str); JSONArray jarr
	 * = pjobj.getJSONArray("callHistory"); for (int i = 0; i < jarr.length();
	 * i++) { JSONObject jobj = jarr.getJSONObject(i); hmap = new
	 * HashMap<String, String>(); String callfrm = jobj.getString("calledfrom");
	 * hmap.put("calledfrom", callfrm);
	 * 
	 * String callto = jobj.getString("calledto"); hmap.put("calledto", callto);
	 * 
	 * String destin = jobj.getString("destination"); hmap.put("destination",
	 * destin);
	 * 
	 * String date = jobj.getString("date"); hmap.put("date", date);
	 * 
	 * String dur = jobj.getString("duration"); hmap.put("duration", dur);
	 * 
	 * String cur = jobj.getString("currency"); hmap.put("currency", cur);
	 * 
	 * String cost = jobj.getString("cost"); hmap.put("cost", cost);
	 * 
	 * hlist.add(hmap); } } catch (JSONException e) { e.printStackTrace(); }
	 * return hlist;
	 * 
	 * }
	 * 
	 * @Override protected void onPostExecute(ArrayList<HashMap<String, String>>
	 * result) { pb.dismiss(); if (result != null) { list.clear(); list =
	 * result; LayoutInflater inflater = getActivity().getLayoutInflater(); View
	 * vi = inflater.inflate(R.layout.refillhisto, null); lv = (ListView)
	 * vi.findViewById(R.id.history); adapter = new
	 * MySimpleAdapter(getActivity(), result, R.layout.callhistory, new String[]
	 * { "calledto", "destination", "duration", "date", "currency", "cost" },
	 * new int[] { R.id.callto, R.id.destin, R.id.dur, R.id.date, R.id.cur,
	 * R.id.cost }); lv.setAdapter(adapter);
	 * 
	 * lv.setOnItemClickListener(new OnItemClickListener() {
	 * 
	 * @Override public void onItemClick(AdapterView<?> arg0, View v, int arg2,
	 * long arg3) { CustomAlertDialog.showAlert(getActivity(), null, null,
	 * "More Details", "Date: " + list.get(arg2).get("dt"), false, "", false,
	 * "", true, null);
	 * 
	 * } });
	 * 
	 * adapter.notifyDataSetChanged(); refilHistoBack = (Button)
	 * vi.findViewById(R.id.refilhistoback); refilHistoBack.setText("Back"); //
	 * refilHistoBack.setVisibility(View.GONE);
	 * refilHistoBack.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { setProfile();
	 * 
	 * } });
	 * 
	 * refresh = (Button) vi.findViewById(R.id.refresh);
	 * refresh.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * String id = Engine.getPref().getString("idClient", ""); DateFormat
	 * dateFormat = new SimpleDateFormat("dd/MM/yyyy"); Date date = new Date();
	 * String frm = dateFormat.format(date); CallHisto rf = new CallHisto();
	 * rf.execute("", "", id);
	 * 
	 * } });
	 * 
	 * contentPlaceHolder.removeAllViews(); contentPlaceHolder.addView(vi); }
	 * else { CustomAlertDialog.showAlert(getActivity(), null, null, "Error",
	 * "Communication Error", false, "Ok", true, "Back", true, null); }
	 * super.onPostExecute(result); } }
	 */
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
			TextView tv = (TextView) v.findViewById(R.id.callto);
			final String num = tv.getText().toString().trim();
			// vi.setOnClickListener(l)
			imv.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					LinphoneActivity.instance().setAddressAndGoToDialer(num);
				}
			});
			return v;
		}

	}

	//
	// class CreditHisto extends AsyncTask<String, String,
	// ArrayList<HashMap<String, String>>>
	// {
	//
	// ArrayList<HashMap<String, String>> hlist = new ArrayList<HashMap<String,
	// String>>();
	// HashMap<String, String> hmap = null;
	//
	// ProgressDialog pb;
	//
	// @Override
	// protected void onPreExecute()
	// {
	// super.onPreExecute();
	// pb = new ProgressDialog(getActivity());
	// pb.show();
	// pb.setContentView(new ProgressBar(getActivity()), new
	// LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	// }
	//
	// @Override
	// protected ArrayList<HashMap<String, String>> doInBackground(String...
	// params)
	// {
	// /*
	// * { date: "2012-11-28 12:40:56.0", amount: "0.01", method:
	// * "100MINS" },
	// */
	//
	// String str = LinphoneUtils.getCreditHistory(params[0], params[1],
	// params[2]);
	// try
	// {
	// JSONObject pjobj = new JSONObject(str);
	// JSONArray jarr = pjobj.getJSONArray("topupHistory");
	// for (int i = 0; i < jarr.length(); i++)
	// {
	// JSONObject jobj = jarr.getJSONObject(i);
	// hmap = new HashMap<String, String>();
	// String date = jobj.getString("date");
	// hmap.put("date", date);
	//
	// String amt = jobj.getString("amount");
	// hmap.put("amount", amt);
	//
	// String meth = jobj.getString("method");
	// hmap.put("method", meth);
	//
	// hlist.add(hmap);
	// }
	// }
	// catch (JSONException e)
	// {
	// e.printStackTrace();
	// }
	// return hlist;
	//
	// }
	//
	// @Override
	// protected void onPostExecute(ArrayList<HashMap<String, String>> result)
	// {
	// pb.dismiss();
	// if (result != null)
	// {
	// list.clear();
	// list = result;
	// LayoutInflater inflater = getActivity().getLayoutInflater();
	// View vi = inflater.inflate(R.layout.refillhisto, null);
	// TextView head = (TextView) vi.findViewById(R.id.textView1);
	// head.setText("Credit History");
	//
	// lv = (ListView) vi.findViewById(R.id.history);
	// adapter = new SimpleAdapter(getActivity(), result,
	// R.layout.refillhistolist, new String[] { "date", "amount", "method" },
	// new int[] { R.id.date, R.id.amtc, R.id.methn });
	// lv.setAdapter(adapter);
	// /*
	// * lv.setOnItemClickListener(new OnItemClickListener() {
	// *
	// * @Override public void onItemClick(AdapterView<?> arg0, View
	// * v, int arg2, long arg3) {
	// * CustomAlertDialog.showAlert(getActivity(), null, null,
	// * "More Details", "Date: " + list.get(arg2).get("dt"), false,
	// * "", false, "", true, null);
	// *
	// * } });
	// */
	// adapter.notifyDataSetChanged();
	// refilHistoBack = (Button) vi.findViewById(R.id.refilhistoback);
	// // refilHistoBack.setVisibility(View.GONE);
	// refilHistoBack.setText("Back");
	// refilHistoBack.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// setProfile();
	//
	// }
	// });
	//
	// refresh = (Button) vi.findViewById(R.id.refresh);
	// refresh.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// String id = Engine.getPref().getString("idClient", "");
	// DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	// Date date = new Date();
	// String frm = dateFormat.format(date);
	// CreditHisto rf = new CreditHisto();
	// rf.execute("", "", id);
	//
	// }
	// });
	//
	// contentPlaceHolder.removeAllViews();
	// contentPlaceHolder.addView(vi);
	// }
	// else
	// {
	// CustomAlertDialog.showAlert(getActivity(), null, null, "Error",
	// "Communication Error", false, "Ok", true, "Back", true, null);
	// }
	// super.onPostExecute(result);
	// }
	// }
	//
	// class GetRefill extends AsyncTask<String, String, HashMap<String,
	// String>>
	// {
	//
	// HashMap<String, String> hmap = new HashMap<String, String>();
	//
	// @Override
	// protected HashMap<String, String> doInBackground(String... params)
	// {
	//
	// String str = LinphoneUtils.getRefill(params[0], params[1], params[2]);
	// URL url;
	// String key = "", value = "";
	// try
	// {
	// // url = new URL(str);
	// // InputStream is=url.openConnection().getInputStream();
	// XmlPullParserFactory xfact = XmlPullParserFactory.newInstance();
	// XmlPullParser parse = xfact.newPullParser();
	// InputStream uri = new ByteArrayInputStream(str.getBytes());
	// parse.setInput(uri, null);
	// int event = parse.getEventType();
	//
	// while (event != XmlPullParser.END_DOCUMENT)
	// {
	//
	// if (event == XmlPullParser.START_TAG)
	// {
	// key = parse.getName();
	// }
	//
	// else if (event == XmlPullParser.END_TAG)
	// {
	// if (key.equals("error")) hmap.put(key, value);
	//
	// if (key.equals("newbal")) hmap.put(key, value);
	// }
	//
	// else if (event == XmlPullParser.TEXT)
	// {
	// value = parse.getText();
	// }
	// event = parse.next();
	// }
	// }
	// catch (MalformedURLException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// catch (IOException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// catch (XmlPullParserException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return hmap;
	//
	// }
	//
	// @Override
	// protected void onPostExecute(HashMap<String, String> result)
	// {
	// if (result != null)
	// {
	// CustomAlertDialog.showAlert(getActivity(), null, null, "Status",
	// result.get("error"), false, "Ok", true, "Back", true, null);
	//
	// if (result.get("newbal") != null && !result.get("newbal").equals(""))
	// {
	//
	// Engine.getEditor().putString("acccount_bal", "Credit : " +
	// result.get("newbal").trim()).commit();
	//
	// /*
	// * hdr.invalidateHeader();
	// * CallLogListFragment.hdr.invalidateHeader();
	// */
	// }
	// }
	// else
	// {
	// CustomAlertDialog.showAlert(getActivity(), null, null, "Error",
	// "Communication Error", false, "Ok", true, "Back", true, null);
	//
	// }
	// super.onPostExecute(result);
	//
	// super.onPostExecute(result);
	// }
	// }
	//
	// class GetRates extends AsyncTask<String, HashMap<String, String>,
	// HashMap<String, String>>
	// {
	//
	// // public String bal;
	// HashMap<String, String> hmap = new HashMap<String, String>();
	//
	// @Override
	// protected HashMap<String, String> doInBackground(String... params)
	// {
	// String str = LinphoneUtils.getRates(params[0], params[1], params[2]);
	// URL url;
	// String key = "", value = "";
	// try
	// {
	// // url = new URL(str);
	// // InputStream is=url.openConnection().getInputStream();
	// XmlPullParserFactory xfact = XmlPullParserFactory.newInstance();
	// XmlPullParser parse = xfact.newPullParser();
	// InputStream uri = new ByteArrayInputStream(str.getBytes());
	// parse.setInput(uri, null);
	// int event = parse.getEventType();
	//
	// while (event != XmlPullParser.END_DOCUMENT)
	// {
	//
	// if (event == XmlPullParser.START_TAG)
	// {
	// key = parse.getName();
	// }
	//
	// else if (event == XmlPullParser.END_TAG)
	// {
	// if (key.equals("Sell")) hmap.put(key, value);
	//
	// if (key.equals("Destination")) hmap.put(key, value);
	//
	// if (key.equals("Dialprefix")) hmap.put(key, value);
	//
	// if (key.equals("Currency")) hmap.put(key, value);
	// }
	//
	// else if (event == XmlPullParser.TEXT)
	// {
	// value = parse.getText();
	// }
	// event = parse.next();
	// }
	// }
	// catch (MalformedURLException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// catch (IOException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// catch (XmlPullParserException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return hmap;
	// }
	//
	// @Override
	// protected void onPostExecute(HashMap<String, String> result)
	// {
	// if (result.get("Sell").equals("-1"))
	// {
	// CustomAlertDialog.showAlert(getActivity(), null, null, "Error",
	// "Invalid Request", false, "Ok", true, null, true, null);
	// }
	// else if (result != null)
	// {
	// LayoutInflater inflater = getActivity().getLayoutInflater();
	// View v = inflater.inflate(R.layout.rates, null);
	// sel = (TextView) v.findViewById(R.id.sel);
	// des = (TextView) v.findViewById(R.id.des);
	// dilpre = (TextView) v.findViewById(R.id.dilpre);
	// cur = (TextView) v.findViewById(R.id.cur);
	// sel.setText(result.get("Sell"));
	// des.setText(result.get("Destination"));
	// dilpre.setText(result.get("Dialprefix"));
	// cur.setText(result.get("Currency"));
	//
	// //
	// rateBack = (Button) v.findViewById(R.id.rateback);
	// //
	// rateBack.setBackgroundDrawable(graphics.getSLTRDrawable(ScreenGraphics.LOGIN_BTN));
	// rateBack.setTextColor(0xffffffff);
	// rateBack.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// setProfile();
	//
	// }
	// });
	// contentPlaceHolder.removeAllViews();
	// contentPlaceHolder.addView(v);
	// }
	// else
	// {
	// CustomAlertDialog.showAlert(getActivity(), null, null, "Error",
	// "Communication Error", false, "Ok", true, "Back", true, null);
	// }
	//
	// super.onPostExecute(result);
	// }
	// }
	//
	// class RefillHisto extends AsyncTask<String, Void,
	// ArrayList<HashMap<String, String>>>
	// {
	//
	// HashMap<String, String> hmap = new HashMap<String, String>();
	// ArrayList<HashMap<String, String>> hlist = new ArrayList<HashMap<String,
	// String>>();
	//
	// @Override
	// protected ArrayList<HashMap<String, String>> doInBackground(String...
	// params)
	// {
	// String str = LinphoneUtils.getRefillHistory(params[0], params[1]);
	// URL url;
	// String key = "", value = "";
	// try
	// {
	// // url = new URL(str);
	// // InputStream is=url.openConnection().getInputStream();
	// XmlPullParserFactory xfact = XmlPullParserFactory.newInstance();
	// XmlPullParser parse = xfact.newPullParser();
	// InputStream uri = new ByteArrayInputStream(str.getBytes());
	// parse.setInput(uri, null);
	// int event = parse.getEventType();
	//
	// while (event != XmlPullParser.END_DOCUMENT)
	// {
	//
	// if (event == XmlPullParser.START_TAG)
	// {
	// key = parse.getName();
	// }
	//
	// else if (event == XmlPullParser.END_TAG)
	// {
	// if (key.equals("Id")) hmap.put(key, value);
	//
	// if (key.equals("Date")) hmap.put(key, value);
	//
	// if (key.equals("Credit")) hmap.put(key, value);
	//
	// if (key.equals("Voucher")) hmap.put(key, value);
	//
	// if (key.equals("status")) hmap.put(key, value);
	//
	// if (key.equals("response")) hlist.add(hmap);
	// }
	//
	// else if (event == XmlPullParser.TEXT)
	// {
	// value = parse.getText();
	// }
	// event = parse.next();
	// }
	// }
	// catch (MalformedURLException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// catch (IOException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// catch (XmlPullParserException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return hlist;
	// }
	//
	// @Override
	// protected void onPostExecute(ArrayList<HashMap<String, String>> result)
	// {
	// if (result != null)
	// {
	// list.clear();
	// list = result;
	// LayoutInflater inflater = getActivity().getLayoutInflater();
	// View vi = inflater.inflate(R.layout.refillhisto, null);
	// lv = (ListView) vi.findViewById(R.id.history);
	// adapter = new SimpleAdapter(getActivity(), list,
	// R.layout.refillhistolist, new String[] { "Credit", "Voucher", "Date" },
	// new int[] { R.id.callto, R.id.dur, R.id.date });
	// lv.setAdapter(adapter);
	// lv.setOnItemClickListener(new OnItemClickListener()
	// {
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3)
	// {
	// CustomAlertDialog.showAlert(getActivity(), null, null, "More Details",
	// "Date: " + list.get(arg2).get("dt"), false, "", false, "", true, null);
	//
	// }
	// });
	// adapter.notifyDataSetChanged();
	// refilHistoBack = (Button) vi.findViewById(R.id.refilhistoback);
	// refilHistoBack.setOnClickListener(new OnClickListener()
	// {
	//
	// @Override
	// public void onClick(View v)
	// {
	// setProfile();
	//
	// }
	// });
	//
	// contentPlaceHolder.removeAllViews();
	// contentPlaceHolder.addView(vi);
	// }
	// else
	// {
	// CustomAlertDialog.showAlert(getActivity(), null, null, "Error",
	// "Communication Error", false, "Ok", true, "Back", true, null);
	// }
	//
	// super.onPostExecute(result);
	// }
	// }

	private void loadData(int index, String data)
	{
		switch (index)
		{
			case DISPLAY_CREDITHISTORY:
				if (data.equals("Error"))
				{
					try
					{
						ArrayList<HashMap<String, String>> hlist = new ArrayList<HashMap<String, String>>();
						JSONArray jarr = (new JSONObject(data)).getJSONArray("callHistory");
						for (int i = 0; i < jarr.length(); i++)
						{
							JSONObject jobj = jarr.getJSONObject(i);
							HashMap<String, String> hmap = new HashMap<String, String>();
							String callfrm = jobj.getString("calledfrom");
							hmap.put("calledfrom", callfrm);

							String callto = jobj.getString("calledto");
							hmap.put("calledto", callto);

							String destin = jobj.getString("destination");
							hmap.put("destination", destin);

							String date = jobj.getString("date");
							hmap.put("date", date);

							String dur = jobj.getString("duration");
							hmap.put("duration", dur);

							String cur = jobj.getString("currency");
							hmap.put("currency", cur);

							String cost = jobj.getString("cost");
							hmap.put("cost", cost);

							hlist.add(hmap);
						}
						list.clear();
						list = hlist;
					}
					catch (Exception ex)
					{
					}
				}
				else Toast.makeText(getActivity(), "Credit CrHistory not available ", 1000).show();
			break;

		// LayoutInflater inflater = getActivity().getLayoutInflater();
		// View vi = inflater.inflate(R.layout.refillhisto, null);
		// lv = new PullAndLoadListView(getActivity());
		// lv.setOnRefreshListener(this);
		// lv.setOnLoadMoreListener(this);
		// adapter = new MySimpleAdapter(getActivity(), list,
		// R.layout.callhistory, new String[] { "calledto", "destination",
		// "duration", "date", "currency", "cost" }, new int[] { R.id.callto,
		// R.id.destin, R.id.dur, R.id.date, R.id.cur, R.id.cost });
		// lv.setAdapter(adapter);
		// adapter.notifyDataSetChanged();
		// contentPlaceHolder.removeAllViews();
		// contentPlaceHolder.addView(lv);

		}
	}

	@Override
	public void remoteCompleated(RemoteProperty res)
	{
		if (res != null) switch (res.getId())
		{
			case DISPLAY_CREDITHISTORY:
				if (savaData)
				{
					savaData = !savaData;
					Engine.getEditor().putString(Engine.PREF.CREDIT_HISTORY.name(), res.getPlaneData()).commit();
				}
				loadData(DISPLAY_CREDITHISTORY, res.getPlaneData());
			break;
			default:
			break;
		}

	}

	private String getURL(int index)
	{
		switch (index)
		{
		// case DISPLAY_CALLHISTORY:
		// return LinphoneUtils.API_URL + "callhistory_api.php?idClient=" +
		// Engine.getPref().getString(Engine.PREF.ID_CLIENT.name(), "") +
		// "&fromDate=" + dateFormat.format(startDate)
		// + "&toDate=" + dateFormat.format(endDate);
			case DISPLAY_CREDITHISTORY:
				return LinphoneUtils.API_URL + "topuphistory_api.php?idClient=" + Engine.getPref().getString(Engine.PREF.ID_CLIENT.name(), "") + "&fromDate=" + dateFormat.format(startDate)
						+ "&toDate=" + dateFormat.format(endDate);
			default:
				return "";

		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3)
	{
		switch (index)
		{
			case DISPLAY_ACCOUNT:
				LinphoneActivity.instance().displayAccSettings();
			break;
			// case DISPLAY_CALLHISTORY:
			// loadData(index,
			// Engine.getPref().getString(Engine.PREF.CALL_HISTORY.name(), ""));
			// if (Engine.isOnline())
			// {
			// RemoteData data = new RemoteData(index, MyAccount.this);
			// data.setProgressDialog(getActivity());
			// startDate = new Date();
			// endDate = new Date(startDate.getTime() - dateofset);
			// data.execute(RemoteData.RESULT_PLANE_TEXT, getURL(index));
			// }
			// break;
			case DISPLAY_PRIVACY_POLICY:
				LayoutInflater inflater = getActivity().getLayoutInflater();
				View v = inflater.inflate(R.layout.webview, null);
				WebView webView = (WebView) v.findViewById(R.id.webView);
				webView.loadUrl("http://sim2dial.com/privacy.php");
				contentPlaceHolder.removeAllViews();
				contentPlaceHolder.addView(v);
			break;
			case DISPLAY_CREDITHISTORY:
				loadData(index, Engine.getPref().getString(Engine.PREF.CREDIT_HISTORY.name(), ""));
				if (Engine.isOnline())
				{
					RemoteData data = new RemoteData(index, MyAccount.this);
					data.setProgressDialog(getActivity());
					startDate = new Date();
					endDate = new Date(startDate.getTime() - dateofset);
					data.execute(RemoteData.RESULT_PLANE_TEXT, getURL(index));
				}
			break;
			case LOGOUT:
				Engine.getEditor().putBoolean(getString(R.string.first_launch_suceeded_once_key), false).commit();
				LinphoneActivity.instance().exit();
			break;
			default:
			break;
		}

		// else if (index == )
		// {
		//
		//
		// // callhistory_api.php? idClient =[ idClient&fromDate=[start
		// // date]&toDate=[end date]
		// // String id = Engine.getPref().getString("idClient", "");
		// // DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		// // Date date = new Date();
		// // String frm = dateFormat.format(date);
		// // CallHisto rf = new CallHisto();
		// // rf.execute("", "", id);
		//
		// }
		// else if (index == DISPLAY_CREDITHISTORY)
		// {
		//
		// // String id = Engine.getPref().getString("idClient", "");
		// //
		// // CreditHisto rf = new CreditHisto();
		// // rf.execute("", "", id);
		//
		// } /*
		// * else if (index == DISPLAY_NEWS) { pb.setVisibility(View.VISIBLE);
		// * LinphoneActivity.instance().displayNews();
		// *
		// * }
		// *
		// * else if (index == DISPLAY_ABOUT) { pb.setVisibility(View.VISIBLE);
		// * LinphoneActivity.instance().displayAbout(); }
		// */

		/*
		 * else if (index == DISPLAY_REFILL_HISTORY) {
		 * pb.setVisibility(View.VISIBLE); RefillHisto rf = new RefillHisto();
		 * rf.execute(LinphoneUtils.getUsername(getActivity()),
		 * LinphoneUtils.getPass(getActivity())); }
		 */
		// else if (index == LOGOUT)
		// {
		//
		// Engine.getEditor().putBoolean(getString(R.string.first_launch_suceeded_once_key),
		// false).commit();
		// LinphoneActivity.instance().exit();
		// }

	}

	@Override
	public void onRefresh()
	{

		loadData(DISPLAY_CREDITHISTORY, Engine.getPref().getString(Engine.PREF.CALL_HISTORY.name(), ""));
		savaData = true;
		if (Engine.isOnline())
		{
			RemoteData data = new RemoteData(DISPLAY_CREDITHISTORY, MyAccount.this);
			data.setProgressDialog(getActivity());
			startDate = new Date(endDate.getTime() - oneday);
			endDate = new Date(startDate.getTime() - dateofset);
			data.execute(RemoteData.RESULT_PLANE_TEXT, getURL(DISPLAY_CREDITHISTORY));
		}
		else
		{
			Toast.makeText(MyAccount.this.getActivity(), MyAccount.this.getActivity().getString(R.string.no_network), Toast.LENGTH_LONG);
		}

	}

	@Override
	public void onLoadMore()
	{

		Toast.makeText(getActivity(), getActivity().getString(R.string.load_more), 1000).show();
		if (Engine.isOnline())
		{
			RemoteData data = new RemoteData(DISPLAY_CREDITHISTORY, MyAccount.this);
			data.setProgressDialog(getActivity());
			startDate = new Date(endDate.getTime() - oneday);
			endDate = new Date(startDate.getTime() - dateofset);
			data.execute(RemoteData.RESULT_PLANE_TEXT, getURL(DISPLAY_CREDITHISTORY));
		}
		else
		{
			Toast.makeText(getActivity(), getActivity().getString(R.string.no_network), 1000).show();
		}

	}

}