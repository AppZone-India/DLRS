

package com.sim2dial.dialer.util;

import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.sip.SipManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.internal.utils.UtilityWrapper;
import com.sim2dial.dialer.Engine;
import com.sim2dial.dialer.R;
import com.sim2dial.dialer.compatibility.Compatibility;

public class Theme {

	private static final String THIS_FILE = "Theme";
	
	private final PackageManager pm;
	private Resources remoteRes = null;
    private PackageInfo pInfos = null;
	
	public Theme(Context ctxt, String packageName) {
		pm = ctxt.getPackageManager();
		
		ComponentName cn = ComponentName.unflattenFromString(packageName);
		
		try {
            pInfos = pm.getPackageInfo(cn.getPackageName(), 0);
            remoteRes = pm.getResourcesForApplication(cn.getPackageName());
        } catch (NameNotFoundException e) {
            Log.e(THIS_FILE, "Impossible to get resources from " + cn.toShortString());
            remoteRes = null;
            pInfos = null;
        }
	}/*
	
	public static Theme getCurrentTheme(Context ctxt) {
	    String themeName = SipConfigManager.getPreferenceStringValue(ctxt, SipConfigManager.THEME);
	    if(!TextUtils.isEmpty(themeName)) {
	        return new Theme(ctxt, themeName);
	    }
	    return null;
	}
	*/
	
	/*public static HashMap<String, String> getAvailableThemes(Context ctxt){
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("", ctxt.getResources().getString(R.string.app_name));
		
		PackageManager packageManager = ctxt.getPackageManager();
		Intent it = new Intent(SipManager.ACTION_GET_DRAWABLES);
		
		List<ResolveInfo> availables = packageManager.queryBroadcastReceivers(it, 0);
		Log.d(THIS_FILE, "We found " + availables.size() + "themes");
		for(ResolveInfo resInfo : availables) {
			Log.d(THIS_FILE, "We have -- "+resInfo);
			ActivityInfo actInfos = resInfo.activityInfo;
			ComponentName cmp = new ComponentName(actInfos.packageName, actInfos.name);
			String label = (String) actInfos.loadLabel(packageManager);
			if(TextUtils.isEmpty(label)) {
			    label = (String) resInfo.loadLabel(packageManager);
			}
			result.put(cmp.flattenToString(), label);
		}
		
		return result;
	}
	*/
	
	public static Drawable getDrawableResource(String name) {
		
			int id = Engine.getContext().getResources().getIdentifier(name, "drawable", Engine.getContext().getPackageName());
            return Engine.getContext().getResources().getDrawable(id);
	}
		

   /* public Integer getDimension(String name) {
        if(remoteRes != null && pInfos != null) {
            int id = remoteRes.getIdentifier(name, "dimen", pInfos.packageName);
            if(id > 0) {
                return remoteRes.getDimensionPixelSize(id);
            }
        }else {
            Log.d(THIS_FILE, "No results yet !! ");
        }
        return null;
    }*/
    
    /*public Integer getColor(String name) {

        if(remoteRes != null && pInfos != null) {
            int id = remoteRes.getIdentifier(name, "color", pInfos.packageName);
            if(id > 0) {
                return remoteRes.getColor(id);
            }
        }else {
            Log.d(THIS_FILE, "No results yet !! ");
        }
        return null;
    }
*/
	/*public void applyBackgroundDrawable(View button, String res) {
		Drawable d = getDrawableResource(res);
		if(d != null) {
		    UtilityWrapper.getInstance().setBackgroundDrawable(button, d);
		}
	}*/
	

    /*public void applyImageDrawable(ImageView subV, String res) {
        Drawable d = getDrawableResource(res);
        if(d != null) {
            subV.setImageDrawable(d);
        }
    }*/
    

   /* public void applyTextColor(TextView subV, String name) {
        Integer color = getColor(name);
        if(color != null) {
            subV.setTextColor(color);
        }
    }*/
	
    public void applyBackgroundStateListDrawable(View v, String prefix) {
        Drawable pressed = getDrawableResource(prefix+"_inv");
        Drawable focused = getDrawableResource(prefix+"_inv");
        Drawable normal = getDrawableResource(prefix);
        if(focused == null) {
            focused = pressed;
        }
        StateListDrawable std = null;
        if(pressed != null && focused != null && normal != null) {
            std = new StateListDrawable();
            std.addState(new int[] {android.R.attr.state_pressed}, pressed);
            std.addState(new int[] {android.R.attr.state_focused}, focused);
            std.addState(new int[] {}, normal);
        }
        
        if(std != null) {
            UtilityWrapper.getInstance().setBackgroundDrawable(v, std);
        }
    }
    

    public static Drawable selectorDrawable(String prefix) {
        Drawable pressed = getDrawableResource(prefix+"_inv");
        Drawable focused = getDrawableResource(prefix+"_inv");
        Drawable selected = getDrawableResource(prefix+"_inv");
        Drawable unselected = getDrawableResource(prefix);
        if(focused == null) {
            focused = pressed;
        }
        StateListDrawable std = null;
        if(pressed != null && focused != null && selected != null && unselected != null) {
            std = new StateListDrawable();
            std.addState(new int[] {android.R.attr.state_pressed}, pressed);
            std.addState(new int[] {android.R.attr.state_focused}, focused);
            std.addState(new int[] {android.R.attr.state_selected}, selected);
            std.addState(new int[] {}, unselected);
        }
        return std;
        /*if(std != null) {
            UtilityWrapper.getInstance().setBackgroundDrawable(v, std);
        }*/
    }
    
    /*public void applyLayoutMargin(View v, String prefix) {
        ViewGroup.MarginLayoutParams lp = null;
        try {
            lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        }catch (ClassCastException e) {
            Log.e(THIS_FILE, "Trying to apply layout params to invalid layout " + v.getLayoutParams());
        }
        Integer marginTop = getDimension(prefix + "_top");
        Integer marginBottom = getDimension(prefix + "_bottom");
        Integer marginRight = getDimension(prefix + "_right");
        Integer marginLeft = getDimension(prefix + "_left");
        if(marginTop != null) {
            lp.topMargin = marginTop;
        }
        if(marginBottom != null) {
            lp.bottomMargin = marginBottom;
        }
        if(marginRight != null) {
            lp.rightMargin = marginRight;
        }
        if(marginLeft != null) {
            lp.leftMargin = marginLeft;
        }
        v.setLayoutParams(lp);
        
    }
    


    public void applyLayoutSize(View v, String prefix) {
        LayoutParams lp = v.getLayoutParams();
        Integer width = getDimension(prefix + "_width");
        Integer height = getDimension(prefix + "_height");
        if(width != null) {
            lp.width = width;
        }
        if(height != null) {
            lp.height = height;
        }
        v.setLayoutParams(lp);
    }
*/

	
	
	/*private static boolean needRepeatableFix() {
        // In ICS and upper the problem is fixed, so no need to apply by code
	    return (!Compatibility.isCompatible(14));
	}*/
	
    /**
     * @param v The view to fix background of.
     * @see #fixRepeatableDrawable(Drawable)
     */
    public static void fixRepeatableBackground(View v) {
        /*if(!needRepeatableFix()) {
            return;
        }*/
        fixRepeatableDrawable(v.getBackground());
    }
    
    /**
     * Fix the repeatable background of a drawable.
     * This support both bitmap and layer drawables
     * @param d the drawable to fix.
     */
    public static void fixRepeatableDrawable(Drawable d) {
       /* if(!needRepeatableFix()) {
            return;
        }*/
        if (d instanceof LayerDrawable) {
            LayerDrawable layer = (LayerDrawable) d;
            for (int i = 0; i < layer.getNumberOfLayers(); i++) {
                fixRepeatableDrawable(layer.getDrawable(i));
            }
        } else if (d instanceof BitmapDrawable) {
            fixRepeatableBitmapDrawable((BitmapDrawable) d);
        }
    
    }
    
    /**
     * Fix the repeatable background of a bitmap drawable.
     * This only support a BitmapDrawable
     * @param d the BitmapDrawable to set repeatable.
     */
    public static void fixRepeatableBitmapDrawable(BitmapDrawable d) {
        /*if(!needRepeatableFix()) {
            return;
        }*/
        // I don't want to mutate because it's better to share the drawable fix for all that share this constant state
        //d.mutate();
        //Log.d(THIS_FILE, "Exisiting tile mode : " + d.getTileModeX() + ", "+ d.getTileModeY());
        d.setTileModeXY(d.getTileModeX(), d.getTileModeY());
        
    }






}
