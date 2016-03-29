package com.viviose.musicscratchpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Vector;

/**
 * Created by Patrick on 2/2/2016.
 */
public class EditorView extends View{

    public float pxdp(float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return px;
    }
    String DEBUG_TAG = "MusicDebug";
    int navigationBarHeight = 0;
    Context conx;
    private static final String TAG = "EditorView";
    public final float NOTE_WIDTH = 150;
    public final float NOTE_HEIGHT = 100;
    Paint paint = new Paint();
    private Bitmap mBitmap;
    private Canvas mCanvas;
    DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
    float density = metrics.density;
    float x;
    float y;
    MainActivity ma = (MainActivity)getContext();
    private GestureDetector mDetector;
    int accidental = 0;
    Note renderableNote;

    float touchX;
    float touchY;
    boolean drawNote;
    //Loading note images
    Bitmap qnh;
    Bitmap hnh;


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
        // navigation bar height

        mDetector = new GestureDetector(this.getContext(), new mListener());
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
            DensityMetrics.setNavBarHeight(navigationBarHeight);
        }
        x = getWidth();
        y = getHeight();
        DensityMetrics.setSpaceHeight((y - DensityMetrics.getToolbarHeight()) / 8);
        paint.setStrokeWidth(10);
        for (int i = 2; i < 7; i++){
            c.drawLine(20, DensityMetrics.spaceHeight * i + DensityMetrics.getToolbarHeight(), x - 20, DensityMetrics.spaceHeight * i + DensityMetrics.getToolbarHeight(), paint);
        }

        float altoClef = DensityMetrics.getToolbarHeight() + 2 * DensityMetrics.spaceHeight;
        if (ClefSetting.clef == Clef.ALTO) {
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.alto_clef);
            c.drawBitmap(Bitmap.createScaledBitmap(b, (int) ((4 * (int) DensityMetrics.spaceHeight) / 1.5), 4 * (int) DensityMetrics.spaceHeight, true), 20, altoClef, paint);

        } else if (ClefSetting.clef == Clef.TREBLE){
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.treble_clef);
            c.drawBitmap(Bitmap.createScaledBitmap(b, 420, 1200, true), 1, 380, paint);
        } else if (ClefSetting.clef == Clef.BASS){
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.bass_clef);
            c.drawBitmap(Bitmap.createScaledBitmap(b, 420, 610, true), 20, 500, paint);
        }

        if (drawNote){
            drawNoteHead(renderableNote, c);
            drawNote = false;
        }



    }
    //TODO: Make noteheads for different note values AND stack x values
    @Override
   public boolean onTouchEvent(MotionEvent event){
        boolean result = mDetector.onTouchEvent(event);
        if (!result){
            Log.d("What", "is this gesture?");
        }
        float x = event.getX();
        float y = event.getY() - DensityMetrics.getToolbarHeight();
        //rhythmBar.setVisibility(View.GONE);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchX = x;
                touchY = y;

                break;
            case MotionEvent.ACTION_UP:

                if (Settings.piano){

                    MainActivity main = new MainActivity();
                    main.nextInput();
                }
                drawNote = true;
                invalidate();
                renderableNote = new Note(touchX,touchY, accidental);
                boolean renderNote = true;
                for (Note note : MusicStore.activeNotes){
                    if (renderableNote.name == note.name && renderableNote.octave == note.octave){
                        renderNote = false;
                        Log.d("Note", "Dupe note skipped!");
                    }
                }
                if (renderNote) {
                    MusicStore.activeNotes.add(renderableNote);
                }

                break;
        }

        return result;
    }
    @TargetApi(21)
    private void drawNoteHead(Note note,Canvas canvas){
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(getContext(), Uri.parse("android.resource://com.viviose.musicscratchpad/raw/" + note.name.toString() + Integer.toString(note.octave)));
        } catch(Exception e){

        }
        Log.i("Media Playing:", "Player created!");
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer player) {
                player.start();

            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        try {
            mediaPlayer.prepareAsync();
        }catch(Exception e){

        }
        if (note.y - DensityMetrics.getToolbarHeight() <= DensityMetrics.spaceHeight * 2){
            canvas.drawLine(note.x - 200, DensityMetrics.spaceHeight + DensityMetrics.getToolbarHeight(), note.x + 200, DensityMetrics.spaceHeight+ DensityMetrics.getToolbarHeight(), paint);
        }
        if (note.y - DensityMetrics.getToolbarHeight() >= DensityMetrics.spaceHeight * 6){
            canvas.drawLine(note.x - 200, DensityMetrics.spaceHeight * 7 + DensityMetrics.getToolbarHeight(), note.x + 200, DensityMetrics.spaceHeight * 7 + DensityMetrics.getToolbarHeight(), paint);
        }
        //canvas.drawOval(note.x - NOTE_WIDTH, note.y - DensityMetrics.spaceHeight / 2, note.x + NOTE_WIDTH, note.y + DensityMetrics.spaceHeight / 2, paint);
        Bitmap headBmap;
        if (note.rhythm == 2){
            headBmap = NoteBitmap.hnh;

        }else{
            headBmap = NoteBitmap.qnh;
        }


        canvas.drawBitmap(Bitmap.createScaledBitmap(headBmap, (int) (DensityMetrics.spaceHeight * 1.697), (int) DensityMetrics.spaceHeight, true), (int) (note.x - (DensityMetrics.spaceHeight * 1.697 / 2)) , note.y - DensityMetrics.spaceHeight / 2, paint);
        if (accidental == 1){
            VectorDrawable vd = (VectorDrawable) ContextCompat.getDrawable(getContext(), R.drawable.sharp);
            Bitmap b = NoteBitmap.getBitmap(vd);
            canvas.drawBitmap(Bitmap.createScaledBitmap(b, (int) (NOTE_HEIGHT * 3 / 2), (int) NOTE_HEIGHT * 3, true), note.x - NOTE_WIDTH * 2, note.y - NOTE_HEIGHT * 3 / 2, paint);
        }

        accidental = 0;

        //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.alto_clef);
        //c.drawBitmap(Bitmap.createScaledBitmap(b, (int) ((4 * (int) DensityMetrics.spaceHeight) / 1.5), 4 * (int) DensityMetrics.spaceHeight, true), 20, altoClef, paint);

    }

    class mListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG,"onDown: " + event.toString());
            return true;
        }
        @Override
        public boolean onFling(MotionEvent ev1, MotionEvent ev2, float velX, float velY){
            if (velY > 5000){
                accidental = -1;
            }
            if (velY < -5000){
                accidental = 1;
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent event) {
            Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
            return true;
        }

    }











}