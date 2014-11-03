package com.sim2dial.dialer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.TextView;

import com.sim2dial.dialer.adapter.MyContactAdapter;
import com.sim2dial.dialer.util.SideBar;

public class ContactsFragment extends Fragment implements OnClickListener, OnItemClickListener
{
	private Handler				mHandler	= new Handler();

	// private LayoutInflater mInflater;
	private ListView			contactsList;
	private TextView			allContacts, linphoneContacts, newContact, noSipContact, noContact/* search */;
	private EditText			searchbox;
	private boolean				onlyDisplayLinphoneContacts;
	private int					lastKnownPosition;
	// private AlphabetIndexer indexer;
	// private boolean editOnClick = false, editConsumed = false,
	// onlyDisplayChatAddress = false, searchboxenble = false;
	// private String sipAddressToAdd;
	private MyContactAdapter	cadap;

	// @Override
	// public void onCreate(Bundle savedInstanceState)
	// {
	// // TODO Auto-generated method stub
	// /*
	// * getActivity().getWindow().setSoftInputMode(
	// * WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	// */
	// /*
	// * InputMethodManager imm = (InputMethodManager) getActivity()
	// * .getSystemService(Context.INPUT_METHOD_SERVICE);
	// * imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
	// * InputMethodManager.HIDE_IMPLICIT_ONLY);
	// */
	// super.onCreate(savedInstanceState);
	//
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.contacts_list, container, false);

		// if (getArguments() != null)
		// {
		// // editOnClick = getArguments().getBoolean("EditOnClick");
		// //sipAddressToAdd = getArguments().getString("SipAddress");
		//
		// ///onlyDisplayChatAddress =
		// getArguments().getBoolean("ChatAddressOnly");
		// }

		searchbox = (EditText) view.findViewById(R.id.searchbox);
		searchbox.addTextChangedListener(watcher);

		noSipContact = (TextView) view.findViewById(R.id.noSipContact);
		noContact = (TextView) view.findViewById(R.id.noContact);

		contactsList = (ListView) view.findViewById(R.id.contactsList);
		SideBar indexBar = (SideBar) view.findViewById(R.id.sideBar);
		indexBar.setListView(contactsList);

		Cursor c = fetchAllContact("");
		prepareAdap(c);
		contactsList.setAdapter(cadap);
		contactsList.setOnItemClickListener(this);

		allContacts = (TextView) view.findViewById(R.id.allContacts);
		allContacts.setOnClickListener(this);

		linphoneContacts = (TextView) view.findViewById(R.id.linphoneContacts);
		linphoneContacts.setOnClickListener(this);

		newContact = (TextView) view.findViewById(R.id.newContact);
		newContact.setOnClickListener(this);
		newContact.setEnabled(LinphoneManager.getLc().getCallsNb() == 0);

		allContacts.setSelected(!onlyDisplayLinphoneContacts);
		linphoneContacts.setSelected(onlyDisplayLinphoneContacts);

		cadap.setFilterQueryProvider(new FilterQueryProvider()
		{

			@Override
			public Cursor runQuery(CharSequence arg0)
			{
				return fetchAllContact(searchbox.getText().toString());
			}
		});

		return view;

	}

	private void prepareAdap(Cursor cur)
	{
		Cursor c = cur;
		String from[] = { Phone.DISPLAY_NAME, Phone.NUMBER };
		int to[] = { R.id.name, R.id.num };
		cadap = new MyContactAdapter(getActivity(), R.layout.contact_cell, c, from, to);
	}

	// private void placeCall(String number, String name)
	// {
	// getActivity().startActivity(new Intent(getActivity(),
	// InCallActivity.class).putExtra("Text", number).putExtra("Name", name));
	// }

	TextWatcher	watcher	= new TextWatcher()
						{

							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count)
							{
								cadap.getFilter().filter(s);
							}

							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after)
							{

							}

							@Override
							public void afterTextChanged(Editable s)
							{

							}
						};

	Cursor fetchAllContact(String ss)
	{
		return getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				Phone.DISPLAY_NAME + " like '%" + ss + "%' OR " + Phone.NUMBER + " like '%" + ss + "%'", null, Phone.DISPLAY_NAME);
	}

	Cursor fetchFavContact(String ss)
	{
		return getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				"starred=1 and (" + Phone.DISPLAY_NAME + " like '%" + ss + "%' OR " + Phone.NUMBER + " like '%" + ss + "%')", null, Phone.DISPLAY_NAME);
	}

	

//	private void changeContactsToggle()
//	{
//		if (onlyDisplayLinphoneContacts)
//		{
//			allContacts.setEnabled(true);
//			linphoneContacts.setEnabled(false);
//		}
//		else
//		{
//			allContacts.setEnabled(false);
//			linphoneContacts.setEnabled(true);
//		}
//	}

	private void changeContactsAdapter()
	{
//		changeContactsToggle();

		// Cursor allContactsCursor =
		// LinphoneActivity.instance().getAllContactsCursor();
		// Cursor sipContactsCursor =
		// LinphoneActivity.instance().getSIPContactsCursor();

		noSipContact.setVisibility(View.GONE);
		noContact.setVisibility(View.GONE);
		contactsList.setVisibility(View.VISIBLE);

		if (onlyDisplayLinphoneContacts)
		{
			/*
			 * if (sipContactsCursor.getCount() == 0) {
			 * noSipContact.setVisibility(View.VISIBLE);
			 * contactsList.setVisibility(View.GONE); } else {
			 */Cursor fav = fetchFavContact("");
			prepareAdap(fav);
			contactsList.setAdapter(cadap);
			cadap.setFilterQueryProvider(new FilterQueryProvider()
			{

				@Override
				public Cursor runQuery(CharSequence arg0)
				{
					return fetchFavContact(searchbox.getText().toString());
				}
			});
			/* } */
		}
		else
		{
			/*
			 * if (allContactsCursor.getCount() == 0) {
			 * noContact.setVisibility(View.VISIBLE);
			 * contactsList.setVisibility(View.GONE); } else {
			 */
			Cursor all = fetchAllContact("");
			prepareAdap(all);
			contactsList.setAdapter(cadap);
			cadap.setFilterQueryProvider(new FilterQueryProvider()
			{

				@Override
				public Cursor runQuery(CharSequence arg0)
				{
					return fetchAllContact(searchbox.getText().toString());
				}
			});
			// }
		}
		// LinphoneActivity.instance().setLinphoneContactsPrefered(onlyDisplayLinphoneContacts);*/
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();

		if (id == R.id.allContacts)
		{
			onlyDisplayLinphoneContacts = false;
			
			allContacts.setSelected(true);
			allContacts.setTextColor(getResources().getColor(R.color.gray_333333));
			linphoneContacts.setSelected(false);
			linphoneContacts.setTextColor(getResources().getColor(R.color.white));
			changeContactsAdapter();
		}
		else if (id == R.id.linphoneContacts)
		{
			onlyDisplayLinphoneContacts = true;
			linphoneContacts.setSelected(true);
			linphoneContacts.setTextColor(getResources().getColor(R.color.gray_333333));
			allContacts.setTextColor(getResources().getColor(R.color.white));
			allContacts.setSelected(false);
			
			changeContactsAdapter();

		}
		else if (id == R.id.newContact)
		{
			Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
			startActivity(intent);
			/*
			 * editConsumed = true; LinphoneActivity.instance().addContact(null,
			 * sipAddressToAdd);
			 */
		}
		/*
		 * else if(id==R.id.search){ if(!searchboxenble){
		 * newContact.setVisibility(View.GONE);
		 * allContacts.setVisibility(View.GONE);
		 * linphoneContacts.setVisibility(View.GONE);
		 * searchbox.setVisibility(View.VISIBLE); searchboxenble=true; }else{
		 * newContact.setVisibility(View.VISIBLE);
		 * allContacts.setVisibility(View.VISIBLE);
		 * linphoneContacts.setVisibility(View.VISIBLE);
		 * searchbox.setVisibility(View.GONE); searchboxenble=false; } }
		 */
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
	{
		Contact contact = (Contact) adapter.getItemAtPosition(position);
		// LinphoneActivity.instance().setAddressAndGoToDialer(contact.getNumerosOrAddresses());
		PhoneBookItemInfo bookItemInfo = (PhoneBookItemInfo) view.getTag();
		String no[] = bookItemInfo.getNumber().toString().split("[+]");
		LinphoneActivity.instance().setAddresGoToDialerAndCall(no[1], contact.getName(), contact.getPhotoUri());
	}

	public void invalidate()
	{
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				// changeContactsAdapter();
				contactsList.setSelectionFromTop(lastKnownPosition, 0);
				contactsList.clearTextFilter();
			}
		});
	}
}
