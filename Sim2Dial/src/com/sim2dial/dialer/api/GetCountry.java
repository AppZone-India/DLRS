package com.sim2dial.dialer.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sim2dial.dialer.Engine;
import com.sim2dial.dialer.LinphoneActivity;
import com.sim2dial.dialer.LinphoneUtils;
import com.sim2dial.dialer.setup.SetupActivity;

public class GetCountry extends AsyncTask<String, Void, String>
{

//	ArrayList<HashMap<String, String>>	hlist	= new ArrayList<HashMap<String, String>>();
	//HashMap<String, String>				hmap	= null;
	ProgressDialog						pb;
	private String						result;
//	Context								ctxt;

	public static String				clist;

	public GetCountry(Context ctxt)
	{
		this.ctxt = ctxt;
	}

	protected void onPreExecute()
	{
		super.onPreExecute();
		pb = new ProgressDialog(ctxt);
		pb.show();
		pb.setContentView(new ProgressBar(ctxt), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

	}

	@Override
	protected String doInBackground(String... params)
	{
		String str = LinphoneUtils.getCountry();
		try
		{
			JSONObject pjobj = new JSONObject(str);
			String country = "Country not Supported," + pjobj.getString("countries");
			result = pjobj.getString("result");
			clist = /* result+"|"+ */country;
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
		pb.dismiss();
		if (result != null && result.equals("1"))
		{
			if (Engine.getPref().getString("Frag", "").equals("Account"))
			{
				LinphoneActivity.instance().displayShowCountry("");
			}
			else
			{
				SetupActivity.instance().displayCountry();
			}
		}
		else
		{
			Toast.makeText(ctxt, "Nothing to display", 20).show();
		}
	}

}
