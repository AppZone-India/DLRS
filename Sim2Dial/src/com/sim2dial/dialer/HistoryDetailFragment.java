package com.sim2dial.dialer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents.Insert;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sim2dial.dialer.util.ImageViewRounded;

public class HistoryDetailFragment extends Fragment implements OnClickListener
{
	private TextView dialBack, chat, addToContacts;
	private ImageViewRounded contactPicture;
	private View view;
	private TextView contactName, contactAddress, callDirection, time, date;
	private String sipUri, displayName, pictureUri;

	// private TextView chat;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		sipUri = getArguments().getString("SipUri");
		displayName = getArguments().getString("DisplayName");
		pictureUri = getArguments().getString("PictureUri");
		String status = getArguments().getString("CallStatus");
		String callTime = getArguments().getString("CallTime");
		String callDate = getArguments().getString("CallDate");
		String timestamp = getArguments().getString("CallTimestamp");

		if (sipUri.contains("@"))
		{
			String num[] = sipUri.split("[:]");
			if (num.length > 1)
			{
				String num1[] = num[1].split("[@]");
				sipUri = num1[0];
			}
		}

		view = inflater.inflate(R.layout.history_detail, container, false);

		contactPicture = (ImageViewRounded) view
				.findViewById(R.id.contactPicture);

		dialBack = (TextView) view.findViewById(R.id.dialBack);
		dialBack.setOnClickListener(this);

		chat = (TextView) view.findViewById(R.id.chat);
		chat.setOnClickListener(this);
		chat.setVisibility(View.GONE);

		chat.setVisibility(View.GONE);

		if (getResources().getBoolean(R.bool.disable_chat))
			view.findViewById(R.id.chat).setVisibility(View.GONE);

		addToContacts = (TextView) view.findViewById(R.id.addToContacts);
		addToContacts.setOnClickListener(this);

		contactName = (TextView) view.findViewById(R.id.contactName);
		if (displayName == null
				&& getResources().getBoolean(
						R.bool.only_display_username_if_unknown)
				&& LinphoneUtils.isSipAddress(sipUri))
		{
			displayName = LinphoneUtils.getUsernameFromAddress(sipUri);
		}

		contactAddress = (TextView) view.findViewById(R.id.contactAddress);

		callDirection = (TextView) view.findViewById(R.id.callDirection);

		time = (TextView) view.findViewById(R.id.time);
		date = (TextView) view.findViewById(R.id.date);

		displayHistory(status, callTime, callDate);

		return view;
	}

	private void displayHistory(String status, String callTime, String callDate)
	{
		if (pictureUri != null)
		{
			LinphoneUtils.setImagePictureFromUri(view.getContext(),	contactPicture, Uri.parse(pictureUri),
					R.drawable.ic_contact);
			view.findViewById(R.id.addToContacts).setVisibility(View.GONE);
		}

		contactName.setText(displayName == null ? sipUri : displayName);
		contactAddress.setText(LinphoneUtils.getUsernameFromAddress(sipUri));

		if (status.equals("Missed"))
		{
			callDirection.setText(getString(R.string.call_state_missed));
		} else if (status.equals("Incoming"))
		{
			callDirection.setText(getString(R.string.call_state_incoming));
		} else if (status.equals("Outgoing"))
		{
			callDirection.setText(getString(R.string.call_state_outgoing));
		} else
		{
			callDirection.setText(status);
		}

		time.setText(callTime == null ? "" : callTime);
		date.setText(callDate);
	}

	public void changeDisplayedHistory(String sipUri, String displayName,
			String pictureUri, String status, String callTime, String callDate)
	{
		if (displayName == null
				&& getResources().getBoolean(
						R.bool.only_display_username_if_unknown)
				&& LinphoneUtils.isSipAddress(sipUri))
		{
			displayName = LinphoneUtils.getUsernameFromAddress(sipUri);
		}

		this.sipUri = sipUri;
		this.displayName = displayName;
		this.pictureUri = pictureUri;
		displayHistory(status, callTime, callDate);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (LinphoneActivity.isInstanciated())
		{
			LinphoneActivity.instance().selectMenu(
					FragmentsAvailable.HISTORY_DETAIL);
		}
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();

		if (id == R.id.dialBack)
		{
			LinphoneActivity.instance().setAddresGoToDialerAndCall(sipUri,
					displayName,
					pictureUri == null ? null : Uri.parse(pictureUri));
		} else if (id == R.id.chat)
		{
			// LinphoneActivity.instance().displayChat(sipUri);
		} else if (id == R.id.addToContacts)
		{
			Intent addc = new Intent(Intent.ACTION_INSERT,
					ContactsContract.Contacts.CONTENT_URI);
			addc.putExtra(Insert.PHONE, sipUri);
			startActivity(addc);
			// LinphoneActivity.instance().displayContactsForEdition(sipUri);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String timestampToHumanDate(String timestamp)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.parseLong(timestamp));

		SimpleDateFormat dateFormat;
		dateFormat = new SimpleDateFormat(getResources().getString(
				R.string.history_detail_date_format));
		return dateFormat.format(cal.getTime());
	}
}
