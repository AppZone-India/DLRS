package com.sim2dial.dialer;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/*public class getBal extends AsyncTask<String, Void, String>{

	public static String bal;
	@Override
	protected String doInBackground(String... params) {
		//String str=LinphoneUtils.hitBalance(params[0]);
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		bal=result;
		Log.i("result:"+result);
		if(bal.length()!=0){
		   //StatusFragment.bal.setText("Bal:"+bal);
		}else{
			//StatusFragment.bal.setText("Bal:"+bal);
		}
		//StatusFragment.bal.setText("Bal:"+bal);
		//StatusFragment.bal.invalidate();
	}
}*/

/*public class getBal extends AsyncTask<String, Void, String>{

	//public String bal;
	HashMap<String, String> hmap=new HashMap<String, String>();
	Context ctxt;
	
	public getBal(Context ctxt) {
		this.ctxt=ctxt;
	}
	
	@Override
	protected String doInBackground(String... params) {
		String url=LinphoneUtils.hitBalance(params[0],params[1],params[2]);
		JSONObject jobj;
		try {
			jobj = new JSONObject(url);
			String result=jobj.getString("result");
			String balance=jobj.getString("balance");
			String currency=jobj.getString("currency");
			return result+"|"+balance+"|"+currency;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if(result!=null){
			String res[]=result.split("[|]");
			if(res.length>1){
				if(res[1]!=null && res.length>1){
					if(StatusFragment.bal.length()!=0){
						   StatusFragment.bal.setText("Bal: "+res[1]+res[2]);
						}else{
							StatusFragment.bal.setText("Bal: "+res[1]+res[2]);
						}
					StatusFragment.bal.invalidate();
				}	
			}
		}else{
			//Toast.makeText(ctxt, "Balance is not updating", 20).show();
		}
	}
}*/