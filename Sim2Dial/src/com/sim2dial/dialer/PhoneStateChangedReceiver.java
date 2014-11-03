/*
PhoneStateReceiver.java
Copyright (C) 2011  Belledonne Communications, Grenoble, France

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
package com.sim2dial.dialer;

import org.linphone.mediastream.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * Pause current SIP calls when GSM phone rings or is active.
 * 
 * @author Guillaume Beraudo
 * 
 */
public class PhoneStateChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		final String extraState = intent
				.getStringExtra(TelephonyManager.EXTRA_STATE);
		String phonenum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

		if (TelephonyManager.EXTRA_STATE_RINGING.equals(extraState)
				|| TelephonyManager.EXTRA_STATE_OFFHOOK.equals(extraState)) {
			LinphoneManager.setGsmIdle(false);
			if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(extraState)) {
				try {
					Thread.sleep(3000);
					String dur = LinphoneUtils.getCallLogduration(context,
							phonenum);
					LinphoneActivity.instance().storedDuration(dur);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!LinphoneManager.isInstanciated()) {
				Log.i("GSM call state changed but manager not instantiated");
				return;
			}
			LinphoneManager.getLc().pauseAllCalls();
		} else if (TelephonyManager.EXTRA_STATE_IDLE.equals(extraState)) {
			LinphoneManager.setGsmIdle(true);
		}

		// do nothing
	}

}
