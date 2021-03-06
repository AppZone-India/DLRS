package com.sim2dial.dialer.compatibility;

import android.annotation.TargetApi;
import android.view.Display;

/*
 ApiEightPlus.java
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
/**
 * @author Sylvain Berfini
 */
@TargetApi(8)
public class ApiEightPlus
{

	public static int getRotation(Display display)
	{
		return display.getRotation();
	}
//
//	public static void initPushNotificationService(Context context)
//	{
//
//		try
//		{
//			// Starting the push notification service
//			GCMRegistrar.checkDevice(context);
//			GCMRegistrar.checkManifest(context);
//			final String regId = GCMRegistrar.getRegistrationId(context);
//			String newPushSenderID = context.getString(R.string.push_sender_id);
//			String currentPushSenderID = Engine.getPref().getString(context.getString(R.string.push_sender_id_key), null);
//			if (regId.equals("") || currentPushSenderID == null || !currentPushSenderID.equals(newPushSenderID))
//			{
//				GCMRegistrar.register(context, newPushSenderID);
//
//				Log.d("Push Notification : storing current sender id = " + newPushSenderID);
//
//				Engine.getEditor().putString(context.getString(R.string.push_sender_id_key), newPushSenderID).commit();
//			}
//			else
//			{
//				Log.d("Push Notification : already registered with id = " + regId);
//				Engine.getEditor().putString(context.getString(R.string.push_reg_id_key), regId).commit();
//			}
//		}
//		catch (java.lang.UnsupportedOperationException e)
//		{
//			Log.i("Push Notification not activated");
//		}
//	}
}
