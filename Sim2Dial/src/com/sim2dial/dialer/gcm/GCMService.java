package com.sim2dial.dialer.gcm;

/*
 GCMService.java
 Copyright (C) 2012  Belledonne Communications, Grenoble, France

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

import org.linphone.core.LinphoneCoreException;
import org.linphone.mediastream.Log;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;
import com.sim2dial.dialer.Engine;
import com.sim2dial.dialer.LinphoneManager;
import com.sim2dial.dialer.R;

/**
 * @author Sylvain Berfini
 */
// Warning ! Do not rename the service !
public class GCMService extends GCMBaseIntentService
{

	public GCMService()
	{

	}

	@Override
	protected void onError(Context context, String errorId)
	{
		Log.e("Error while registering push notification : " + errorId);
	}

	@Override
	protected void onMessage(Context context, Intent intent)
	{
		Log.d("Push notification received");
		if (LinphoneManager.isInstanciated())
		{
			LinphoneManager.getLc().setNetworkReachable(false);
			LinphoneManager.getLc().setNetworkReachable(true);
		}
	}

	@Override
	protected void onRegistered(Context context, String regId)
	{
		Log.d("Registered push notification : " + regId);

		Engine.getEditor().putString(context.getString(R.string.push_reg_id_key), regId).commit();

		if (LinphoneManager.isInstanciated())
		{
			try
			{
				LinphoneManager.getInstance().initAccounts();
			}
			catch (LinphoneCoreException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onUnregistered(Context context, String regId)
	{
		Log.w("Unregistered push notification : " + regId);

		Engine.getEditor().putString(context.getString(R.string.push_reg_id_key), null).commit();
	}

	protected String[] getSenderIds(Context context)
	{
		return new String[] { context.getString(R.string.push_sender_id) };
	}
}
