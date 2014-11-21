package com.sim2dial.dialer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	TextView							sel, des, cur, dilpre,header;
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
	boolean clearlist=true;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
	{
		View v = inflater.inflate(R.layout.accountplaceholder, container, false);

		contentPlaceHolder = (FrameLayout) v.findViewById(R.id.placeholder);
		createMainList("Account Settings", "Credit History", "Top Up", "Privacy Policy", "Exit");
		header=(TextView) v.findViewById(R.id.header);
		
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
			return v;
		}

	}

	private void loadData(int index, String data)
	{
		switch (index)
		{
			case DISPLAY_CREDITHISTORY:
				if (!data.equals("Error"))
				{
					try
					{
						//{"listtopup":[{"date":"2014-11-12 17:01:42","amount":"5.00","method":"Recharge via PIN"}]}
						ArrayList<HashMap<String, String>> hlist = new ArrayList<HashMap<String, String>>();
						JSONArray jarr = (new JSONObject(data)).getJSONArray("listtopup");
						for (int i = 0; i < jarr.length(); i++)
						{
							JSONObject jobj = jarr.getJSONObject(i);
							HashMap<String, String> hmap = new HashMap<String, String>();
							String date[] = jobj.getString("date").split("\\s+");
							hmap.put("date", date[0]);
							hmap.put("time", date[1]);
							String amount = jobj.getString("amount");
							hmap.put("amount", amount);

							String method = jobj.getString("method");
							hmap.put("method", method);
							hlist.add(hmap);
						}
						if(clearlist)
							list.clear();
						clearlist=false;
						list.addAll(hlist);
						if(list.size()<10)
							onLoadMore();
					LayoutInflater inflater = getActivity().getLayoutInflater();
					View vi = inflater.inflate(R.layout.refillhisto, null);
					lv = (PullAndLoadListView) vi.findViewById(R.id.recha);
					header.setText("Recharge History");
					lv.setOnRefreshListener(this);
					lv.setOnLoadMoreListener(this);
					adapter = new MySimpleAdapter(getActivity(), list,
							R.layout.callhistory, new String[]
							{ "date", "time", "amount", "method"}, new int[]
							{ R.id.date, R.id.time, R.id.amount, R.id.method});
					lv.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					contentPlaceHolder.removeAllViews();
					contentPlaceHolder.addView(vi);

					}
					catch (Exception ex)
					{
					}
				}
				else Toast.makeText(getActivity(), "Credit CrHistory not available ", 1000).show();
			break;

		
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
			case DISPLAY_TOP_UP:
			try
			{
				//{"rechargepin":[{"Status":"Payment Cannot done","Amount":0}]}
				//{"rechargepin":[{"Amount":0,"Status":"Invalid PIN"}]}
				JSONObject jo=res.getJsonObject().getJSONArray("rechargepin").getJSONObject(0);
//				if(jo.getString("Status").equals("Invalid PIN"))
//				{
//					Toast.makeText(getActivity(), jo.getString("Status"), 1000).show();
//				}
//				else
//				{
//					System.out.println();
//				}
				CustomAlertDialog.showAlert(getActivity(), null, null, "Status", jo.getString("Status")+"\nRecharge Amount : "+jo.getString("Amount"), false, "", false, "", true, null);
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
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
		//{"listtopup":[{"date":"2014-11-12 17:01:42","amount":"5.00","method":"Recharge via PIN"}]}
			case DISPLAY_CREDITHISTORY:
				return LinphoneUtils.API_URL + "topuphistory_api.php?idClient=" + Engine.getPref().getString(Engine.PREF.ID_CLIENT.name(), "") + "&toDate=" + dateFormat.format(startDate)
						+ "&fromDate=" + dateFormat.format(endDate);
		
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
			case DISPLAY_TOP_UP:
				LinphoneActivity.instance().displayRecharge();
				/*
				contentPlaceHolder.removeAllViews();
				contentPlaceHolder.addView(v);*/
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
				View vv = inflater.inflate(R.layout.webview, null);
				WebView webView = (WebView) vv.findViewById(R.id.webView);
				webView.loadUrl("http://sim2dial.com/privacy.php");
				contentPlaceHolder.removeAllViews();
				contentPlaceHolder.addView(vv);
			break;
			case DISPLAY_CREDITHISTORY:
				loadData(index, Engine.getPref().getString(Engine.PREF.CREDIT_HISTORY.name(), ""));
				if (Engine.isOnline())
				{
					clearlist=true;
					RemoteData data = new RemoteData(index, MyAccount.this);
					data.setProgressDialog(getActivity(),R.style.ProgressBar);
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

		
	}

	@Override
	public void onRefresh()
	{

		loadData(DISPLAY_CREDITHISTORY, Engine.getPref().getString(Engine.PREF.CALL_HISTORY.name(), ""));
		savaData = true;
		clearlist=true;
		if (Engine.isOnline())
		{
			RemoteData data = new RemoteData(DISPLAY_CREDITHISTORY, MyAccount.this);
			//data.setProgressDialog(getActivity(),R.style.ProgressBar);
			startDate = new Date();
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
		//	data.setProgressDialog(getActivity(),R.style.ProgressBar);
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