package com.sim2dial.dialer.adapter;

import java.util.Map;
import java.util.TreeMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.sim2dial.dialer.LinphoneActivity;
import com.sim2dial.dialer.PhoneBookItemInfo;
import com.sim2dial.dialer.R;
import com.sim2dial.dialer.util.ImageViewRounded;
import com.sim2dial.dialer.util.Theme;

public class MyContactAdapter extends SimpleCursorAdapter implements
		SectionIndexer, OnAlphabaticPosition
{

	Context ctxt;
	private static final int TYPE_HEADER = 1;
	private static final int TYPE_NORMAL = 0;

	private static final int TYPE_COUNT = 2;

	private AlphabetIndexer indexer;

	private int[] usedSectionNumbers;

	private Map<Integer, Integer> sectionToPosition;
	Drawable callbtn;

	public MyContactAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to)
	{
		super(context, layout, c, from, to);
		ctxt = context;
		indexer = new AlphabetIndexer(c,
				c.getColumnIndexOrThrow("display_name"),
				"#ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		sectionToPosition = new TreeMap<Integer, Integer>();
		callbtn = Theme.selectorDrawable("ic_call");
		final int count = super.getCount();
		int i;
		for (i = count - 1; i >= 0; i--)
		{
			sectionToPosition.put(indexer.getSectionForPosition(i), i);
		}
		i = 0;
		usedSectionNumbers = new int[sectionToPosition.keySet().size()];
		for (Integer section : sectionToPosition.keySet())
		{

			usedSectionNumbers[i] = section;
			i++;
		}
		// for(Integer section: sectionToPosition.keySet()){
		// sectionToPosition.put(section, sectionToPosition.get(section) +
		// sectionToOffset.get(section));
		// }

	}

	@Override
	public void bindView(View v1, Context ctx, Cursor cursor)
	{
		super.bindView(v1, ctx, cursor);

		final PhoneBookItemInfo bookItemInfo = new PhoneBookItemInfo();

		try
		{
			bookItemInfo.setIsStred(cursor.getString(cursor
					.getColumnIndex("starred")));
			bookItemInfo.setContactID(cursor.getString(cursor
					.getColumnIndex(CommonDataKinds.Phone.CONTACT_ID)));
			bookItemInfo.setName(cursor.getString(cursor
					.getColumnIndex("display_name")));
			String n = cursor.getString(cursor.getColumnIndex("data1"));
			if (n.trim().startsWith("+"))
				n = n.replace("+", "00");
			bookItemInfo.setNumber(n);
			bookItemInfo.setUserData(cursor.getPosition());

		} catch (Exception e)
		{
		}
		ImageViewRounded img = (ImageViewRounded) v1.findViewById(R.id.img);
		ImageButton imb = (ImageButton) v1.findViewById(R.id.imb);
		imb.setImageDrawable(callbtn);
		View v;
		v = v1.findViewById(R.id.condisp);
		v.setTag(bookItemInfo);
		imb.setTag(bookItemInfo);
		v.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				PhoneBookItemInfo pi = (PhoneBookItemInfo) view.getTag();
				LinphoneActivity.instance().setAddressAndGoToDialer(
						pi.getNumber().toString());
				/*
				 * if (!(SipHome.mTabsAdapter.mCurrentPosition <= 0))
				 * SipHome.mViewPager.setCurrentItem(0, true); DialerFragment
				 * .digits.setText(PhoneNumberUtils.stripSeparators
				 * (pi.getNumber()));
				 */
			}
		});

		// faviourte icon
		v = v1.findViewById(R.id.fav);
		v.setTag(bookItemInfo);

		if (bookItemInfo.getIsStred().equals("1"))
			((ImageView) v).setImageDrawable(ctxt.getResources().getDrawable(
					R.drawable.fev));
		else
			((ImageView) v).setImageDrawable(ctxt.getResources().getDrawable(
					R.drawable.fev_inv));

		v.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				PhoneBookItemInfo pi = (PhoneBookItemInfo) v.getTag();
				ContentValues values = new ContentValues();
				ImageView imv = (ImageView) v;
				if (pi.getIsStred().equals("0"))
				{
					values.put(Contacts.STARRED, 1);
					mContext.getContentResolver().update(Contacts.CONTENT_URI,
							values, Contacts._ID + "= ?", new String[]
							{ pi.getContactID() });
					pi.setIsStred("1");
					imv.setImageDrawable(ctxt.getResources().getDrawable(
							R.drawable.fev));

				} else
				{
					values.put(Contacts.STARRED, 0);
					ctxt.getContentResolver().update(Contacts.CONTENT_URI,
							values, Contacts._ID + "= ?", new String[]
							{ pi.getContactID() });
					pi.setIsStred("0");
					imv.setImageDrawable(ctxt.getResources().getDrawable(
							R.drawable.fev_inv));

				}
				v.setTag(pi);
			}
		});

		img.setImageBitmap(loadContactPhoto(ctxt.getContentResolver(), cursor));
		imb.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View view)
			{
				PhoneBookItemInfo pi = (PhoneBookItemInfo) view.getTag();
				LinphoneActivity.instance().setAddresGoToDialerAndCall(
						pi.getNumber().toString(), pi.getName().toString(),
						null);
			}
		});
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = super.getView(position, convertView, parent);
		if (v != null)
		{
			final int type = getItemViewType(position);
			if (type == TYPE_HEADER)
			{

				((TextView) v.findViewById(R.id.section))
						.setText((String) getSections()[getSectionForPosition(position)]);
				((TextView) v.findViewById(R.id.section))
						.setVisibility(View.VISIBLE);
			} else
			{
				((TextView) v.findViewById(R.id.section))
						.setVisibility(View.GONE);
			}

		}
		return v;
	}

	@Override
	public int getItemViewType(int position)
	{
		if (position == getPositionForSection(getSectionForPosition(position)))
		{
			return TYPE_HEADER;
		}
		return TYPE_NORMAL;
	}

	@Override
	public int getViewTypeCount()
	{
		return TYPE_COUNT;
	}// these two methods just disable the headers

	@Override
	public boolean areAllItemsEnabled()
	{
		return false;
	}

	@Override
	public boolean isEnabled(int position)
	{
		if (getItemViewType(position) == TYPE_HEADER)
		{
			return false;
		}
		return true;
	}

	private Bitmap loadContactPhoto(ContentResolver cr, Cursor c)
	{
		Uri person = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI,
				c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID)));
		Uri photo = Uri.withAppendedPath(person,
				ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
		Cursor cursor = cr.query(photo, new String[]
		{ ContactsContract.Contacts.Photo.PHOTO }, null, null, null);
		if (cursor == null)
		{
			return null;
		}
		try
		{
			if (cursor.moveToFirst())
			{
				byte[] data = cursor.getBlob(0);
				if (data != null)
				{
					Bitmap bmp;
					bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
					Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888,
							true);
					return mutableBitmap;
				}
			}
		} finally
		{
			cursor.close();

		}
		return ((BitmapDrawable) ctxt.getResources().getDrawable(
				R.drawable.ic_contact)).getBitmap();

	}

	@Override
	public int getPositionForSection(int section)
	{
		try
		{
			return indexer.getPositionForSection(section);
		} catch (Exception e)
		{
			return 0;

		}
	}

	@Override
	public int getSectionForPosition(int position)
	{
		try
		{
			int i = 0;
			int maxLength = usedSectionNumbers.length;
			while (i < maxLength
					&& position >= sectionToPosition.get(usedSectionNumbers[i]))
			{
				i++;
			}
			return usedSectionNumbers[i - 1];
		} catch (Exception e)
		{
			return 0;

		}
	}

	@Override
	public Object[] getSections()
	{
		return indexer.getSections();
	}

	@Override
	public int setAlphabaticPosition(int alpha)
	{
		Integer c = (int) alpha;
		for (int i = c; i >= 0; i--)
		{
			if (sectionToPosition.containsKey(i))
				return sectionToPosition.get(i);
		}
		return -1;

	}
}