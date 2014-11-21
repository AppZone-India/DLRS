package com.sim2dial.dialer;

import org.json.JSONException;
import org.json.JSONObject;

import com.ns.kgraphicsengin.RemoteData;
import com.ns.kgraphicsengin.RemoteData.OnRemoteCompleated;
import com.ns.kgraphicsengin.RemoteData.RemoteProperty;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RechargeFragment extends Fragment implements OnRemoteCompleated
{
	final int DISPLAY_TOP_UP = 100;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{

		View v = inflater.inflate(R.layout.top_up, container, false);

		// header.setText("Top Up");
		final EditText pin = (EditText) v.findViewById(R.id.pin);
		Button topup = (Button) v.findViewById(R.id.topup);
		topup.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				// https://www.mycallhistory.com/vportal/API/sim2dial/rechargepin_api.php?idClient=[client
				// id]&pin=[recharge pin]
				//
				// ex: https://www.mycallhistory.com/vportal/API/sim2dial/
				// rechargepin_api.php?idClient=1234&pin=342113445
				if (Engine.isOnline())
				{
					if (!pin.getText().toString().trim().equals(""))
					{
						RemoteData data = new RemoteData(DISPLAY_TOP_UP,
								RechargeFragment.this);
						data.setProgressDialog(getActivity(), 0);
						data.execute(
								RemoteData.RESULT_JSON,
								LinphoneUtils.API_URL
										+ "rechargepin_api.php?idClient="
										+ Engine.getPref().getString(
												Engine.PREF.ID_CLIENT.name(),
												"") + "&pin="
										+ pin.getText().toString().trim());

						pin.setText("");
					} else
						Toast.makeText(getActivity(),
								"Please provide some input", 1000).show();
				} else
					Toast.makeText(getActivity(),
							getString(R.string.no_network), 1000).show();
			}
		});
		return v;
	}

	@Override
	public void remoteCompleated(RemoteProperty res)
	{
		switch (res.getId())
		{
		case DISPLAY_TOP_UP:
			try
			{
				// {"rechargepin":[{"Status":"Payment Cannot done","Amount":0}]}
				// {"rechargepin":[{"Amount":0,"Status":"Invalid PIN"}]}
				JSONObject jo = res.getJsonObject().getJSONArray("rechargepin")
						.getJSONObject(0);
				// if(jo.getString("Status").equals("Invalid PIN"))
				// {
				// Toast.makeText(getActivity(), jo.getString("Status"),
				// 1000).show();
				// }
				// else
				// {
				// System.out.println();
				// }
				CustomAlertDialog.showAlert(
						getActivity(),
						null,
						null,
						"Status",
						jo.getString("Status") + "\nRecharge Amount : "
								+ jo.getString("Amount"), false, "", false, "",
						true, null);
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		default:
			break;
		}

	}
}
