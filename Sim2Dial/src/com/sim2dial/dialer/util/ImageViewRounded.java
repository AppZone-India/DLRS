package com.sim2dial.dialer.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sim2dial.dialer.R;

public class ImageViewRounded extends ImageView
{
	boolean	isBorder	= false, isNumber = false;
	float	borderWidth;
	float	numberRadius, numberSize;
	int		numberColor, numberBackgroundColor, borderColor;

	public float getNumberSize()
	{
		return numberSize;
	}

	public void setNumberSize(int numberSize)
	{
		this.numberSize = numberSize;
	}

	public float getNumberRadius()
	{
		return numberRadius;
	}

	public void setNumberRadius(int numberRadius)
	{
		this.numberRadius = numberRadius;
	}

	String	numberText	= "0";

	public String getNumberText()
	{
		return numberText;
	}

	public void setNumberText(String numberText)
	{
		this.numberText = numberText;
	}

	public float getBorderWidth()
	{
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth)
	{
		this.borderWidth = borderWidth;
	}

	public boolean isNumber()
	{
		return isNumber;
	}

	public void setNumber(boolean isNumber)
	{
		this.isNumber = isNumber;
	}

	public boolean isBorder()
	{
		return isBorder;
	}

	public void setBorder(boolean isBorder)
	{
		this.isBorder = isBorder;
	}

	public ImageViewRounded(Context context)
	{
		super(context);
	}

	public ImageViewRounded(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs);
	}

	public ImageViewRounded(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs);
	}

	void init(AttributeSet attrs)
	{
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircularImage);

		isBorder = a.getBoolean(R.styleable.CircularImage_enable_border, false);
		isNumber = a.getBoolean(R.styleable.CircularImage_enable_number, false);

		borderWidth = a.getDimension(R.styleable.CircularImage_border_width, 5);
		numberRadius = a.getDimension(R.styleable.CircularImage_numberRadius, 10);
		numberSize = a.getDimension(R.styleable.CircularImage_numberSize, 12);

		numberText = a.getString(R.styleable.CircularImage_numberText);

		numberColor = a.getColor(R.styleable.CircularImage_numberColor, 0xffffffff);
		numberBackgroundColor = a.getColor(R.styleable.CircularImage_numberBackgroundColor, 0xff880000);
		borderColor = a.getColor(R.styleable.CircularImage_borderColor, 0xff880000);

		a.recycle();

	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		BitmapDrawable drawable = (BitmapDrawable) getDrawable();

		if (drawable == null) { return; }

		if (getWidth() == 0 || getHeight() == 0) { return; }

		Bitmap fullSizeBitmap = drawable.getBitmap();

		int scaledWidth = getMeasuredWidth();
		int scaledHeight = getMeasuredHeight();

		Bitmap mScaledBitmap;
		if (scaledWidth == fullSizeBitmap.getWidth() && scaledHeight == fullSizeBitmap.getHeight())
		{
			mScaledBitmap = fullSizeBitmap;
		}
		else
		{
			mScaledBitmap = Bitmap.createScaledBitmap(fullSizeBitmap, scaledWidth, scaledHeight, true /* filter */);
		}

		// Bitmap roundBitmap = getRoundedCornerBitmap(mScaledBitmap);

		// Bitmap roundBitmap = getRoundedCornerBitmap(getContext(),
		// mScaledBitmap, 10, scaledWidth, scaledHeight, false, false,
		// false, false);
		// canvas.drawBitmap(roundBitmap, 0, 0, null);

		Bitmap circleBitmap = getCircledBitmap(mScaledBitmap);

		canvas.drawBitmap(circleBitmap, 0, 0, null);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{

		int desiredWidth = 100;
		int desiredHeight = 100;
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = Math.min(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = heightSize;

		int width;
		int height;
		if (widthMode == MeasureSpec.EXACTLY)
		{
			width = widthSize;
		}
		else if (widthMode == MeasureSpec.AT_MOST)
		{
			width = Math.min(desiredWidth, widthSize);
		}
		else
		{
			width = desiredWidth;
		}

		//Measure Height
		if (heightMode == MeasureSpec.EXACTLY)
		{
			height = heightSize;
		}
		else if (heightMode == MeasureSpec.AT_MOST)
		{
			height = Math.min(desiredHeight, heightSize);
		}
		else
		{
			height = desiredHeight;
		}
		height= Math.max(width, height);
		setMeasuredDimension(height, height);
	}

	/*
		public Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels, int w, int h, boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR)
		{

			Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final float densityMultiplier = context.getResources().getDisplayMetrics().density;

			final int color = 0xff424242;

			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, w, h);
			final RectF rectF = new RectF(rect);

			// make sure that our rounded corner is scaled appropriately
			final float roundPx = pixels * densityMultiplier;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			// draw rectangles over the corners we want to be square
			if (squareTL)
			{
				canvas.drawRect(0, 0, w / 2, h / 2, paint);
			}
			if (squareTR)
			{
				canvas.drawRect(w / 2, 0, w, h / 2, paint);
			}
			if (squareBL)
			{
				canvas.drawRect(0, h / 2, w / 2, h, paint);
			}
			if (squareBR)
			{
				canvas.drawRect(w / 2, h / 2, w, h, paint);
			}

			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(input, 0, 0, paint);

			return output;
		}
	*/
	Bitmap getCircledBitmap(Bitmap bitmap)
	{

		Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(result);
		canvas.drawARGB(1, 1, 0, 0);
		float Cx, Cy;
		Cx = bitmap.getWidth() / 2;
		Cy = bitmap.getHeight() / 2;
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
		canvas.drawCircle(Cx, Cy, Cy, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		canvas.drawBitmap(bitmap, rect, rect, paint);

		if (isBorder)
		{
			paint.setColor(borderColor);
			paint.setStyle(Style.STROKE);
			paint.setStrokeJoin(Join.ROUND);
			paint.setStrokeCap(Cap.ROUND);
			paint.setStrokeWidth(borderWidth);
			canvas.drawCircle(Cx, Cy, Cy - borderWidth / 2, paint);
		}
		if (isNumber)
		{
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(numberBackgroundColor);
			paint.setStyle(Style.FILL);

			canvas.drawCircle(bitmap.getWidth() - numberRadius, numberRadius, numberRadius, paint);
			paint.setColor(numberColor);
			paint.setTextSize(numberSize);
			paint.setTextAlign(Align.CENTER);
			Rect bounds = new Rect();

			paint.getTextBounds(numberText, 0, 1, bounds);
			canvas.drawText(numberText, bitmap.getWidth() - numberRadius, numberRadius + bounds.height() / 2, paint);
		}
		return result;
	}

}
