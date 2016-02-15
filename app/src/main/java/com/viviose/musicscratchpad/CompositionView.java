package com.viviose.musicscratchpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Patrick on 2/14/2016.
 */
public class CompositionView extends View {
    private static final String TAG = "CompositionView";
    Paint paint = new Paint();
    Context conx;
    public final float NOTE_WIDTH = 150;
    public final float NOTE_HEIGHT = 100;
    public CompositionView(Context con){
        super(con);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint.setColor(Color.BLACK);
    }
    public CompositionView(Context con, AttributeSet attrs){
        super(con, attrs);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint.setColor(Color.BLACK);
    }
    public CompositionView(Context con, AttributeSet attrs, int defStyle){
        super(con, attrs, defStyle);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint.setColor(Color.BLACK);
    }
    @Override
    @TargetApi(21)
    public void onDraw(Canvas c){
        paint.setStrokeWidth(10);
        c.drawLine(20, 500, 6000, 500, paint);
        c.drawLine(20, 700, 6000, 700, paint);
        c.drawLine(20, 900, 6000, 900, paint);
        c.drawLine(20, 1100, 6000, 1100, paint);
        c.drawLine(20, 1300, 6000, 1300, paint);
        float drawX = 320;
        for (ArrayList<Note> chord : MusicStore.sheet){
            for (Note note : chord){
                c.drawOval(drawX - NOTE_WIDTH, note.y - NOTE_HEIGHT, drawX + NOTE_WIDTH, note.y + NOTE_HEIGHT, paint);
            }
            drawX += 600;
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(6000, 1500);

    }
}
