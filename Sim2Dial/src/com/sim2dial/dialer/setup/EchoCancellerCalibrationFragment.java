package com.sim2dial.dialer.setup;

import java.net.URL;

import org.linphone.core.LinphoneCore.EcCalibratorStatus;
import org.linphone.core.LinphoneCoreException;
import org.linphone.mediastream.Log;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sim2dial.dialer.Engine;
import com.sim2dial.dialer.LinphoneManager;
import com.sim2dial.dialer.LinphoneManager.EcCalibrationListener;
import com.sim2dial.dialer.LinphoneService;
import com.sim2dial.dialer.R;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class EchoCancellerCalibrationFragment extends Fragment implements EcCalibrationListener
{
	private Handler	mHandler					= new Handler();
	private boolean	mSendEcCalibrationResult	= false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.setup_ec_calibration, container, false);

		try
		{
			LinphoneManager.getInstance().startEcCalibration(this);
		}
		catch (LinphoneCoreException e)
		{
			Log.e(e, "Unable to calibrate EC");
		}

		return view;
	}

	@Override
	public void onEcCalibrationStatus(EcCalibratorStatus status, int delayMs)
	{

		Context context = SetupActivity.instance() == null ? LinphoneService.instance().getApplicationContext() : SetupActivity.instance();

		if (status == EcCalibratorStatus.DoneNoEcho)
		{
			Engine.getEditor().putBoolean(context.getString(R.string.pref_echo_cancellation_key), false).commit();
		}
		else if ((status == EcCalibratorStatus.Done) || (status == EcCalibratorStatus.Failed))
		{
			Engine.getEditor().putBoolean(context.getString(R.string.pref_echo_cancellation_key), true).commit();
		}
		if (mSendEcCalibrationResult)
		{
			sendEcCalibrationResult(status, delayMs);
		}
		else
		{
			SetupActivity.instance().isEchoCalibrationFinished();
		}
	}

	public void enableEcCalibrationResultSending(boolean enabled)
	{
		mSendEcCalibrationResult = enabled;
	}

	private void sendEcCalibrationResult(EcCalibratorStatus status, int delayMs)
	{
		try
		{
			XMLRPCClient client = new XMLRPCClient(new URL(getString(R.string.wizard_url)));

			XMLRPCCallback listener = new XMLRPCCallback()
			{
				Runnable	runFinished	= new Runnable()
										{
											public void run()
											{
												SetupActivity.instance().isEchoCalibrationFinished();
											}
										};

				public void onResponse(long id, Object result)
				{
					mHandler.post(runFinished);
				}

				public void onError(long id, XMLRPCException error)
				{
					mHandler.post(runFinished);
				}

				public void onServerError(long id, XMLRPCServerException error)
				{
					mHandler.post(runFinished);
				}
			};

			Log.i("Add echo canceller calibration result: manufacturer=" + Build.MANUFACTURER + " model=" + Build.MODEL + " status=" + status + " delay=" + delayMs + "ms");
			client.callAsync(listener, "add_ec_calibration_result", Build.MANUFACTURER, Build.MODEL, status.toString(), delayMs);
		}
		catch (Exception ex)
		{
		}
	}
}
