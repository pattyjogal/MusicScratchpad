package com.viviose.musicscratchpad;

/**
 * Created by Patrick on 2/22/2016.
 */
public class DensityMetrics {
    public static float density = 3;
    public static float spaceHeight;
    public static float toolbarHeight;

    public void setDensity(float d){
        density = d;
    }

    public static void setSpaceHeight(float spaceHeight) {
        DensityMetrics.spaceHeight = spaceHeight;
    }

    public static void setToolbarHeight(float toolbarHeight) {
        DensityMetrics.toolbarHeight = toolbarHeight;
    }

    public static float y(float yposn){
        return yposn + toolbarHeight;
    }
}
