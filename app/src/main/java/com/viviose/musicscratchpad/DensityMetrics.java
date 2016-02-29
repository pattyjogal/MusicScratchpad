package com.viviose.musicscratchpad;

import android.support.v7.widget.Toolbar;

/**
 * Created by Patrick on 2/22/2016.
 */
public class DensityMetrics {
    public static float density = 3;
    public static float spaceHeight;
    public static float toolbarHeight;
    public static Toolbar toolbar;
    public static float navBarHeight;

    public void setDensity(float d){
        density = d;
    }

    public static void setSpaceHeight(float spaceHeight) {
        DensityMetrics.spaceHeight = spaceHeight;
    }

    public static float getToolbarHeight() {
        return toolbar.getHeight();
    }

    public static void setNavBarHeight(float navBarHeight) {
        DensityMetrics.navBarHeight = navBarHeight;
    }

    public static float y(float yposn){
        return yposn + toolbar.getHeight();
    }
}
