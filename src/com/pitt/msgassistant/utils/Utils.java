package com.pitt.msgassistant.utils;


import android.content.Context;

/**
 * Created by jeffwan on 10/17/13.
 */
public class Utils {

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int disp2px(Context context, float px) {
        final float scale = getScreenDensity(context);
        return (int)(px * scale + 0.5);
    }

}
