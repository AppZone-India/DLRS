package com.sim2dial.dialer;

import com.sim2dial.dialer.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlphaDialog extends Dialog
{

	EditText				edtx;
	AlphaListner			pos, neg;
	Context					context;
	public static TextView	headTextView;
	ImageView				img;
	TextView				msg;
	private Button			yes, no;
	public static RelativeLayout	head, mid;
	public static LinearLayout		container, foot;
	View							view;
	boolean							notext	= false;

	public Button getPosButon()
	{
		return yes;
	}
	public Button getNegButon()
	{
		return no;
	}
	public interface AlphaListner
	{
		public void positive(View v);
	}

	public AlphaDialog(Context context)
	{
		super(context);

		this.context = context;
		notext = false;

	}

	public AlphaDialog(Context context, View v)
	{
		super(context);

		this.context = context;
		this.headTextView = new TextView(context);
		view = v;
		notext = true;

	}

	public boolean isNotext()
	{
		return notext;
	}

	public void setNotext(boolean notext)
	{
		this.notext = notext;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams fparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams btnprm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, (float) .5);
		container = new LinearLayout(context);
		container.setOrientation(LinearLayout.VERTICAL);

		head = new RelativeLayout(context);
		head.setPadding(4, 10, 4, 10);
		RelativeLayout.LayoutParams hprml = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		hprml.addRule(RelativeLayout.CENTER_IN_PARENT);
		hprml.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		img = new ImageView(context);

		img.setId(1);
		RelativeLayout.LayoutParams hprmr = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		hprmr.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		hprmr.addRule(RelativeLayout.RIGHT_OF, img.getId());
		if (headTextView != null)
		{
			headTextView.setText("Header");

			head.addView(img, hprml);
			head.addView(headTextView, hprmr);
		}
		mid = new RelativeLayout(context);

		if (!notext)
		{
			
			RelativeLayout.LayoutParams msprm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			
			msg = new TextView(context);
			msg.setText("Message");
			msg.setGravity(Gravity.CENTER_HORIZONTAL);
			mid.addView(msg,msprm);
		}
		else mid.addView(view);
		foot = new LinearLayout(context);
		foot.setPadding(0, 0, 0, 0);
		yes = new Button(context);
		// yes.setBackgroundResource(R.drawable.btn_dial_no_corner);
		yes.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (pos != null) pos.positive(v);
				AlphaDialog.this.dismiss();

			}
		});
		yes.setText("OK");
		no = new Button(context);
		no.setText("Cancle");

		// no.setBackgroundResource(R.drawable.btn_dial_no_corner);
		no.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (neg != null) neg.positive(v);
				AlphaDialog.this.dismiss();

			}
		});
		foot.addView(yes, btnprm);
		foot.addView(no, btnprm);

		head.getLayoutParams();
		container.addView(head);
		head.requestLayout();
		container.addView(mid);
		container.addView(foot, fparams);

		container.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shp_login_bg));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(container, params);
		getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shp_dialog_bg));

	}

	public void setAlphaPositiveListner(AlphaListner pos)
	{
		this.pos = pos;
	}

	public void setAlphaNegativeListner(AlphaListner neg)
	{
		this.neg = neg;
	}

	public void setIcon(int id)
	{
		img.setImageDrawable(context.getResources().getDrawable(id));
	}

	void setWinBG(Drawable d)

	{
		getWindow().setBackgroundDrawable(d);
	}

	void setPositiveBtnBG(Drawable d)

	{
		// yes.setBackgroundResource(R.drawable.alphamid);
	}

	public void setNegativeBtnBG(int id)

	{
		no.setBackgroundResource(id);
	}

	public void setNegativeBtnText(String ss)

	{
		no.setText(ss);
	}

	public void setPositiveBtnBG(int id)

	{
		yes.setBackgroundResource(id);
	}

	public void setPositiveBtnText(String ss)

	{
		yes.setText(ss);
	}

	void setNegativeBtnBG(Drawable d)

	{
		// no.setBackgroundResource(R.drawable.alphamid);
	}

	public void setMessage(String ms)
	{
		msg.setText(ms);
	}
	public TextView getMessage()
	{
		return msg;
	}
}
