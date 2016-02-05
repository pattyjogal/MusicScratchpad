package com.viviose.musicscratchpad;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Patrick on 2/2/2016.
 */
public class EditorView extends View {

    Canvas can;
    Context conx;
    private static final String TAG = "EditorView";
    private final float NOTE_WIDTH = 150;
    private final float NOTE_HEIGHT = 100;
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
        paint.setStrokeWidth(10);
        c.drawLine(20, 500, x - 20, 500, paint);
        c.drawLine(20, 700, x - 20, 700, paint);
        c.drawLine(20, 900, x - 20, 900, paint);
        c.drawLine(20, 1100, x - 20, 1100, paint);
        c.drawLine(20, 1300, x - 20, 1300, paint);
        if (drawNote){
            drawNoteHead(touchX, touchY, c);
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
    private void drawNoteHead(float x, float y,Canvas canvas){
        MediaPlayer mediaPlayer = new MediaPlayer();
        Note note = new Note(x,y);

        try {
            mediaPlayer.setDataSource(getContext(), Uri.parse("android.resource://com.viviose.musicscratchpad/raw/" + note.name.toString() + Integer.toString(note.octave)));
        } catch(Exception e){
            Log.println(100, "Whoopsie", e.toString());
        }
        Log.i("Media Playing:", "Player created!");
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer player) {
                player.start();
                Log.i("Media Playing:", "Player should have played!");
            }
        });
       /* mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                Log.i("Completion Listener", "Song Complete");
                mp.stop();
                mp.reset();

            }
        });*/
        try {
            mediaPlayer.prepareAsync();
        }catch(Exception e){
            Log.println(100, "Whoopsie", e.toString());
        }

        canvas.drawOval(note.x - NOTE_WIDTH, note.y - NOTE_HEIGHT, note.x + NOTE_WIDTH, note.y + NOTE_HEIGHT, paint);


    }



}