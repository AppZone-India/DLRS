/*
CallButton.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

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
package com.sim2dial.dialer.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ns.kgraphicsengin.RemoteData;
import com.ns.kgraphicsengin.RemoteData.OnRemoteCompleated;
import com.ns.kgraphicsengin.RemoteData.RemoteProperty;
import com.sim2dial.dialer.CustomAlertDialog;
import com.sim2dial.dialer.Engine;
import com.sim2dial.dialer.LinphoneActivity;
import com.sim2dial.dialer.LinphoneUtils;
import com.sim2dial.dialer.R;

/**
 * @author Guillaume Beraudo
 */
public class CallBackButton extends Button implements OnClickListener,OnRemoteCompleated,
		AddressAware
{

	private AddressText mAddress;

	public void setAddressWidget(AddressText a)
	{
		mAddress = a;
	}

	public void setExternalClickListener(OnClickListener e)
	{
		setOnClickListener(e);
	}

	public void resetClickListener()
	{
		setOnClickListener(this);
	}

	Context ctxt;

	public CallBackButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		ctxt = context;
		setOnClickListener(this);
	}

	public void onClick(View v)
	{

//		https://www.mycallhistory.com/vportal/API/SIM2DIAL/ddi_api.php?idClient=[idClient]&country=[
//
//			CountryName]&number=[PhoneNumber]

		String address = mAddress.getText().toString();

			if (address.length() >= 1)
			{
				if (LinphoneUtils.isHightBandwidthConnection(ctxt))
				{
					LinphoneActivity.instance().storeHistory(address, address,
							"00:00", "Outgoing");
				RemoteData data=new RemoteData(1000,CallBackButton.this);
				data.setProgressDialog(ctxt, 0);
				if(!"Please Select Country".equals(Engine.PREF.COUNTRY.name()))
				{
				data.execute(RemoteData.RESULT_JSON, LinphoneUtils.API_URL+"ddi_api.php?idClient="
				+Engine.getPref().getString(Engine.PREF.ID_CLIENT.name(),"0")+"&country="
						+Engine.getPref().getString(Engine.PREF.COUNTRY.name(),"0")+"&number="+address);
				}
				else
				{
					Toast.makeText(ctxt, "Please Select Country", 2000).show();
				}
				} else
				{
					Toast.makeText(ctxt, "Check Data Connection", 20).show();
				}
			} else
			{
				Toast.makeText(ctxt, "Please Enter Number.", 20).show();
			}

		

		/*
		 * try { if
		 * (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) { if
		 * (mAddress.getText().length() > 0) { ctxt.startActivity(new
		 * Intent(ctxt,InCallActivity.class).putExtra("Text",
		 * mAddress.getText().
		 * toString()).putExtra("Name",mAddress.getDisplayedName()));
		 * //LinphoneManager.getInstance().newOutgoingCall(mAddress); } else {
		 * if (getContext().getResources().getBoolean(R.bool.
		 * call_last_log_if_adress_is_empty)) { LinphoneCallLog[] logs =
		 * LinphoneManager.getLc().getCallLogs(); LinphoneCallLog log = null;
		 * for (LinphoneCallLog l : logs) { if (l.getDirection() ==
		 * CallDirection.Outgoing) { log = l; break; } } if (log == null) {
		 * return; }
		 * 
		 * LinphoneProxyConfig lpc =
		 * LinphoneManager.getLc().getDefaultProxyConfig(); if (lpc != null &&
		 * log.getTo().getDomain().equals(lpc.getDomain())) {
		 * mAddress.setText(log.getTo().getUserName()); } else {
		 * mAddress.setText(log.getTo().asStringUriOnly()); }
		 * mAddress.setSelection(mAddress.getText().toString().length());
		 * mAddress.setDisplayedName(log.getTo().getDisplayName()); } } } }
		 * catch (LinphoneCoreException e) {
		 * LinphoneManager.getInstance().terminateCall();
		 * onWrongDestinationAddress(); };
		 */
	}

	protected void onWrongDestinationAddress()
	{
		Toast.makeText(
				getContext(),
				String.format(
						getResources().getString(
								R.string.warning_wrong_destination_address),
						mAddress.getText().toString()), Toast.LENGTH_LONG)
				.show();
	}

	class GetDDI extends AsyncTask<String, String, String>
	{

		ProgressDialog pb;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			pb = new ProgressDialog(ctxt);
			pb.show();
			pb.setContentView(new ProgressBar(ctxt), new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		}

		@Override
		protected String doInBackground(String... params)
		{
			String str = LinphoneUtils.getDDI(params[0], params[1], params[2],
					params[3], params[4]);
			String ddi = null, result = null;
			try
			{
				JSONObject jobj = new JSONObject(str);
				ddi = jobj.getString("ddi");
				result = jobj.getString("result");

			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ddi + "|" + result;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			pb.dismiss();
			if (result.contains("1") && result != null)
			{
				String ddi[] = result.split("[|]");
				/*
				 * LinphoneActivity.instance().storeHistory("", ddi[0], "00:00",
				 * "Outgoing");
				 */
				ctxt.startActivity(new Intent(Intent.ACTION_CALL, Uri
						.parse("tel:" + ddi[0])));
				// Toast.makeText(ctxt, "Success "+ddi[0], 20).show();
				/*
				 * CustomAlertDialog.showAlert(ctxt, null, null, "Status",
				 * result, true, "Ok", false, "Back", true, null);
				 */
			} else
			{
				CustomAlertDialog.showAlert(ctxt, null, null, "Error",
						"Communication Error", false, "Ok", true, "Ok", true,
						null);

			}
		}
	}

	@Override
	public void remoteCompleated(RemoteProperty r)
	{
		//{"listnumber":[{"Contact Number":"++","Status":"success"}]} 
		if(r!=null)
		{
			try
			{
				JSONObject ob=r.getJsonObject().getJSONArray("listnumber").getJSONObject(0);
		
				ctxt.startActivity(new Intent(Intent.ACTION_CALL, Uri
						.parse("tel:" + ob.getString("Contact Number"))));
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
