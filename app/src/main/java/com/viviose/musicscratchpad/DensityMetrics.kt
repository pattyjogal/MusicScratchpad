package com.viviose.musicscratchpad

import android.content.*
import android.support.v7.widget.Toolbar

/**
 * Created by Patrick on 2/22/2016.
 */
class DensityMetrics {

    companion object {
        @JvmStatic var spaceHeight: Float = 0.toFloat()
        @JvmStatic var toolbar: Toolbar? = null
        @JvmStatic var navBarHeight: Float = 0.toFloat()

        fun getToolbarHeight() : Float{
            return toolbar?.height?.toFloat() ?: 0f
        }

    }
}
