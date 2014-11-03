/*
AddressView.java
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

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sim2dial.dialer.DialerFragment;
import com.sim2dial.dialer.LinphoneManager.AddressType;
import com.sim2dial.dialer.R;

/**
 * @author Guillaume Beraudo
 * 
 */
public class AddressText extends EditText implements AddressType, Parcelable/*
																			 * ,
																			 * OnTouchListener
																			 * ,
																			 * OnLongClickListener
																			 * ,
																			 * OnCreateContextMenuListener
																			 */, OnFocusChangeListener{

	private String displayedName;
	private Uri pictureUri;
	private Paint mTestPaint;
	private DialerFragment dialer;
	private Context ctxt;

	public void setPictureUri(Uri uri) {
		pictureUri = uri;
	}

	public Uri getPictureUri() {
		return pictureUri;
	}

	public AddressText(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctxt = context;

		mTestPaint = new Paint();
		mTestPaint.set(this.getPaint());
		// setOnTouchListener(this);
		setFocusableInTouchMode(true);
		setFocusable(true);
		setOnFocusChangeListener(this);
		// setOnLongClickListener(this);

	}

	/*
	 * MenuItem.OnMenuItemClickListener click = new
	 * MenuItem.OnMenuItemClickListener() {
	 * 
	 * @Override public boolean onMenuItemClick(MenuItem item) {
	 * onTextContextMenuItem(item.getItemId()); return true; } };
	 */

	public void clearDisplayedName() {
		displayedName = "";
	}

	public String getDisplayedName() {
		return displayedName;
	}

	public void setContactAddress(String uri, String displayedName) {
		setText(uri);
		this.displayedName = displayedName;
	}

	public void setDisplayedName(String displayedName) {
		this.displayedName = displayedName;
	}

	private String getHintText() {
		String resizedText = getContext().getString(R.string.addressHint);
		if (getHint() != null) {
			resizedText = getHint().toString();
		}
		return resizedText;
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before,
			int after) {
		clearDisplayedName();
		pictureUri = null;

		refitText(getWidth(), getHeight());

		if (dialer != null) {
			dialer.enableDisableAddContact();
		}

		super.onTextChanged(text, start, before, after);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth,
			int oldHeight) {
		if (width != oldWidth) {
			refitText(getWidth(), getHeight());
		}
	}

	private float getOptimizedTextSize(String text, int textWidth,
			int textHeight) {
		int targetWidth = textWidth - getPaddingLeft() - getPaddingRight();
		int targetHeight = textHeight - getPaddingTop() - getPaddingBottom();
		float hi = 90;
		float lo = 2;
		final float threshold = 0.5f;

		mTestPaint.set(getPaint());

		while ((hi - lo) > threshold) {
			float size = (hi + lo) / 2;
			mTestPaint.setTextSize(size);
			if (mTestPaint.measureText(text) >= targetWidth
					|| size >= targetHeight) {
				hi = size;
			} else {
				lo = size;
			}
		}

		return lo;
	}

	private void refitText(int textWidth, int textHeight) {
		if (textWidth <= 0) {
			return;
		}

		float size = getOptimizedTextSize(getHintText(), textWidth, textHeight);
		float entrySize = getOptimizedTextSize(getText().toString(), textWidth,
				textHeight);
		if (entrySize < size)
			size = entrySize;
		setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int height = getMeasuredHeight();

		refitText(parentWidth, height);
		setMeasuredDimension(parentWidth, height);
	}

	public void setDialerFragment(DialerFragment dialerFragment) {
		dialer = dialerFragment;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}

	@Override
	public boolean onCheckIsTextEditor() {
		hideKeyboard();
		return super.onCheckIsTextEditor();
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		super.onSelectionChanged(selStart, selEnd);

		hideKeyboard();
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindowToken(), 0);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		hideKeyboard();
	}

	/*
	 * @Override public boolean onTextContextMenuItem(int id) { // Do your
	 * thing: boolean consumed = super.onTextContextMenuItem(id); // React:
	 * switch (id) { case android.R.id.cut: onTextCut(); break; case
	 * android.R.id.paste: onTextPaste(); break; case android.R.id.copy:
	 * onTextCopy(); } return consumed; }
	 *//**
	 * Text was cut from this EditText.
	 */
	/*
	 * public void onTextCut() { Toast.makeText(ctxt, "Cut!",
	 * Toast.LENGTH_SHORT).show(); }
	 *//**
	 * Text was copied from this EditText.
	 */
	/*
	 * public void onTextCopy() { Toast.makeText(ctxt, "Copy!",
	 * Toast.LENGTH_SHORT).show(); }
	 *//**
	 * Text was pasted into the EditText.
	 */
	/*
	 * public void onTextPaste() { Toast.makeText(ctxt, "Paste!",
	 * Toast.LENGTH_SHORT).show(); }
	 */

	/*
	 * @Override public boolean onCheckIsTextEditor() { return false; }
	 */

	/*
	 * @Override public boolean onTouchEvent(MotionEvent event) { if
	 * (MotionEvent.ACTION_DOWN == event.getAction()) {
	 * 
	 * return true; } return super.onTouchEvent(event); }
	 * 
	 * @Override public boolean onTouch(View v, MotionEvent event) {
	 * Toast.makeText(ctxt, "Paste!", Toast.LENGTH_SHORT).show(); return true; }
	 * 
	 * @Override public boolean onLongClick(View v) { Toast.makeText(ctxt,
	 * "Paste!aaa", Toast.LENGTH_SHORT).show(); return true; }
	 * 
	 * @Override public void onCreateContextMenu(ContextMenu menu, View v,
	 * ContextMenuInfo menuInfo) { menu.add(1, 1, 1, "Cut"); menu.add(1, 2, 2,
	 * "Copy"); menu.add(1, 3, 3, "Paste");
	 * 
	 * }
	 */
}
