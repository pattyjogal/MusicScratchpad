package com.viviose.musicscratchpad;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Patrick on 3/1/2016.
 */
public class DP {
    public static Resources r;
    public static float px(float dip){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
    }

}
