/*
NumpadView.java
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

import java.util.ArrayList;
import java.util.Collection;

import com.sim2dial.dialer.R;
import com.sim2dial.dialer.util.Theme;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author Guillaume Beraudo
 *
 */
public class Numpad extends LinearLayout implements AddressAware
{

	private boolean mPlayDtmf;

	public void setPlayDtmf(boolean sendDtmf)
	{
		this.mPlayDtmf = sendDtmf;
	}

	private void applyTheme()
	{

		findViewById(R.id.Digit1).setBackground(
				getResources().getDrawable(R.drawable.numpad_one));
		findViewById(R.id.Digit2).setBackground(
				getResources().getDrawable(R.drawable.numpad_two));
		findViewById(R.id.Digit3).setBackground(
				getResources().getDrawable(R.drawable.numpad_three));
		findViewById(R.id.Digit4).setBackground(
				getResources().getDrawable(R.drawable.numpad_four));
		findViewById(R.id.Digit5).setBackground(
				getResources().getDrawable(R.drawable.numpad_five));
		findViewById(R.id.Digit6).setBackground(
				getResources().getDrawable(R.drawable.numpad_six));
		findViewById(R.id.Digit7).setBackground(
				getResources().getDrawable(R.drawable.numpad_seven));
		findViewById(R.id.Digit8).setBackground(
				getResources().getDrawable(R.drawable.numpad_eight));
		findViewById(R.id.Digit9).setBackground(
				getResources().getDrawable(R.drawable.numpad_nine));
		findViewById(R.id.DigitHash).setBackground(
				getResources().getDrawable(R.drawable.numpad_sharp));
		findViewById(R.id.Erase).setBackground(
				getResources().getDrawable(R.drawable.numpad_star));
		findViewById(R.id.Digit00).setBackground(
				getResources().getDrawable(R.drawable.numpad_zero));
	}

	public Numpad(Context context, boolean playDtmf)
	{
		super(context);
		mPlayDtmf = playDtmf;
		LayoutInflater.from(context).inflate(R.layout.numpad, this);
		setLongClickable(true);
		onFinishInflate();
		applyTheme();
	}

	public Numpad(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.Numpad);
		mPlayDtmf = 1 == a.getInt(
				com.sim2dial.dialer.R.styleable.Numpad_play_dtmf, 1);
		a.recycle();
		LayoutInflater.from(context).inflate(R.layout.numpad, this);
		setLongClickable(true);
		applyTheme();
	}

	@Override
	protected final void onFinishInflate()
	{
		for (Digit v : retrieveChildren(this, Digit.class))
		{
			v.setPlayDtmf(mPlayDtmf);
		}
		super.onFinishInflate();
	}

	public void setAddressWidget(AddressText address)
	{
		for (AddressAware v : retrieveChildren(this, AddressAware.class))
		{
			v.setAddressWidget(address);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

	private final <T> Collection<T> retrieveChildren(ViewGroup viewGroup,
			Class<T> clazz)
	{
		final Collection<T> views = new ArrayList<T>();

		for (int i = 0; i < viewGroup.getChildCount(); i++)
		{
			View v = viewGroup.getChildAt(i);
			if (v instanceof ViewGroup)
			{
				views.addAll(retrieveChildren((ViewGroup) v, clazz));
			} else
			{
				if (clazz.isInstance(v))
					views.add(clazz.cast(v));
			}
		}

		return views;
	}

}
