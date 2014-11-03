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

import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneProxyConfig;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.sim2dial.dialer.InCallActivity;
import com.sim2dial.dialer.LinphoneManager;
import com.sim2dial.dialer.R;

/**
 * @author Guillaume Beraudo
 */
public class CallButton extends Button implements OnClickListener, AddressAware {

	private AddressText mAddress;
	public void setAddressWidget(AddressText a) { mAddress = a; }

	public void setExternalClickListener(OnClickListener e) { setOnClickListener(e); }
	public void resetClickListener() { setOnClickListener(this); }

	Context ctxt;
	public CallButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctxt=context;
		setOnClickListener(this);
		
	}

	public void onClick(View v) {
		try {
			if (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) {
				if (mAddress.getText().length() > 0) {
					ctxt.startActivity(new Intent(ctxt,InCallActivity.class).putExtra("Text", mAddress.getText().toString()).putExtra("Name",mAddress.getDisplayedName()));
					//LinphoneManager.getInstance().newOutgoingCall(mAddress);
				} else {
					if (getContext().getResources().getBoolean(R.bool.call_last_log_if_adress_is_empty)) {
						LinphoneCallLog[] logs = LinphoneManager.getLc().getCallLogs();
						LinphoneCallLog log = null;
						for (LinphoneCallLog l : logs) {
							if (l.getDirection() == CallDirection.Outgoing) {
								log = l;
								break;
							}
						}
						if (log == null) {
							return;
						}
						
						LinphoneProxyConfig lpc = LinphoneManager.getLc().getDefaultProxyConfig();
						/*if (lpc != null && log.getTo().getDomain().equals(lpc.getDomain())) {
							mAddress.setText(log.getTo().getUserName());
						} else {
							mAddress.setText(log.getTo().asStringUriOnly());
						}
						mAddress.setSelection(mAddress.getText().toString().length());
						mAddress.setDisplayedName(log.getTo().getDisplayName());*/
					}
				}
			}
		} catch (LinphoneCoreException e) {
			LinphoneManager.getInstance().terminateCall();
			onWrongDestinationAddress();
		};
	}
	
	protected void onWrongDestinationAddress() {
		Toast.makeText(getContext()
				,String.format(getResources().getString(R.string.warning_wrong_destination_address),mAddress.getText().toString())
				,Toast.LENGTH_LONG).show();
	}
}
