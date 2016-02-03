package com.viviose.musicscratchpad;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Patrick on 2/2/2016.
 */
public class EditorView extends View {
    Canvas can;
    Context conx;
    private static final String TAG = "EditorView";
    private final float NOTE_WIDTH = 200;
    private final float NOTE_HEIGHT = 120;
    Paint paint = new Paint();
    private Bitmap mBitmap;
    private Canvas mCanvas;
    DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
    float x = metrics.widthPixels;
    float y = metrics.heightPixels;
    float touchX;
    float touchY;
    boolean drawNote;
    public EditorView(Context con){
        super(con);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint.setColor(Color.BLACK);
    }
    public EditorView(Context con, AttributeSet attrs){
        super(con, attrs);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint.setColor(Color.BLACK);
    }
    public EditorView(Context con, AttributeSet attrs, int defStyle){
        super(con, attrs, defStyle);
        conx = con;
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint.setColor(Color.BLACK);
    }

    @Override
    @TargetApi(21)
    public void onDraw(Canvas c){
        can = c;
        paint.setStrokeWidth(10);
        c.drawLine(20, 500, x - 20, 500, paint);
        c.drawLine(20, 700, x - 20, 700, paint);
        c.drawLine(20, 900, x - 20, 900, paint);
        c.drawLine(20, 1100, x - 20, 1100, paint);
        c.drawLine(20, 1300, x - 20, 1300, paint);
        if (drawNote){
            c.drawOval(touchX - NOTE_WIDTH, touchY - NOTE_HEIGHT, touchX + NOTE_WIDTH, touchY + NOTE_HEIGHT, paint);
            drawNote = false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchX = x;
                touchY = y;
                drawNote = true;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:

                break;
            case MotionEvent.ACTION_UP:
                ;
                break;
        }

        return true;
    }
    @TargetApi(21)
    private void drawNoteHead(float x, float y) {
        Bitmap bmp = Bitmap.createBitmap(64,64, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        mBitmap = bmp;
        mCanvas = c;


    }



}