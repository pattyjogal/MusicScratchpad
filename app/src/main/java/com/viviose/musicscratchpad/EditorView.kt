package com.viviose.musicscratchpad

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.VectorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by Patrick on 2/2/2016.
 */
class EditorView : View {

    fun pxdp(dp: Float): Float {
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        return px
    }

    internal var DEBUG_TAG = "MusicDebug"
    internal var navigationBarHeight = 0
    internal var conx: Context
    val NOTE_WIDTH = 150f
    val NOTE_HEIGHT = 100f
    internal var paint = Paint()
    private val mBitmap: Bitmap? = null
    private val mCanvas: Canvas? = null
    internal var metrics = context.resources.displayMetrics
    internal var density = metrics.density
    internal var x: Float = 0.toFloat()
    internal var y: Float = 0.toFloat()
    internal var ma = context as MainActivity
    private var mDetector: GestureDetector? = null
    internal var accidental = 0
    internal var renderableNote: Note = Note(0f,0f,0)

    internal var touchX: Float = 0.toFloat()
    internal var touchY: Float = 0.toFloat()
    internal var drawNote: Boolean = false
    //Loading note images
    internal var qnh: Bitmap? = null
    internal var hnh: Bitmap? = null

    internal var drawDot: Boolean = false


    constructor(con: Context) : super(con) {
        conx = con
        isFocusable = true
        isFocusableInTouchMode = true

        paint.color = Color.BLACK
    }

    constructor(con: Context, attrs: AttributeSet) : super(con, attrs) {
        conx = con
        isFocusable = true
        isFocusableInTouchMode = true

        paint.color = Color.BLACK
    }

    constructor(con: Context, attrs: AttributeSet, defStyle: Int) : super(con, attrs, defStyle) {
        conx = con
        isFocusable = true
        isFocusableInTouchMode = true

        paint.color = Color.BLACK
    }

    @TargetApi(21)
    public override fun onDraw(c: Canvas) {
        // navigation bar height

        mDetector = GestureDetector(this.context, mListener())
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId)
            DensityMetrics.navBarHeight = (navigationBarHeight.toFloat())
        }
        x = width.toFloat()
        y = height.toFloat()
        DensityMetrics.spaceHeight = ((y - DensityMetrics.getToolbarHeight()) / 8)
        paint.strokeWidth = 10f
        for (i in 2..6) {
            c.drawLine(20f, DensityMetrics.spaceHeight * i + DensityMetrics.getToolbarHeight(), x - 20, DensityMetrics.spaceHeight * i + DensityMetrics.getToolbarHeight(), paint)
        }

        val altoClef = DensityMetrics.getToolbarHeight() + 2 * DensityMetrics.spaceHeight
        if (ClefSetting.clef == Clef.ALTO) {
            val b = BitmapFactory.decodeResource(resources, R.drawable.alto_clef)
            c.drawBitmap(Bitmap.createScaledBitmap(b, (4 * DensityMetrics.spaceHeight.toInt() / 1.5).toInt(), 4 * DensityMetrics.spaceHeight.toInt(), true), 20f, altoClef, paint)

        } else if (ClefSetting.clef == Clef.TREBLE) {
            val b = BitmapFactory.decodeResource(resources, R.drawable.treble_clef)
            c.drawBitmap(Bitmap.createScaledBitmap(b, 420, 1200, true), 1f, 380f, paint)
        } else if (ClefSetting.clef == Clef.BASS) {
            val b = BitmapFactory.decodeResource(resources, R.drawable.bass_clef)
            c.drawBitmap(Bitmap.createScaledBitmap(b, 420, 610, true), 20f, 500f, paint)
        }

        if (drawNote) {
            drawNoteHead(renderableNote, c)
            drawNote = false
        }


    }

    //TODO: Make noteheads for different note values AND stack x values
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val result = mDetector!!.onTouchEvent(event)
        if (!result) {
            Log.d("What", "is this gesture?")
        }
        val x = event.x
        val y = event.y - DensityMetrics.getToolbarHeight()
        //rhythmBar.setVisibility(View.GONE);
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = x
                touchY = y
            }
            MotionEvent.ACTION_UP -> {


                drawNote = true
                invalidate()
                renderableNote = Note(touchX, touchY, accidental)
                var renderNote = true
                for (note in MusicStore.activeNotes) {
                    if (renderableNote.name == note.name && renderableNote.octave == note.octave) {
                        renderNote = false
                        Log.d("Note", "Dupe note skipped!")
                    }
                }
                if (renderNote) {
                    MusicStore.activeNotes.add(renderableNote)
                }
                if (Settings.piano) {

                    val main = MainActivity()
                    main.nextInput()
                }
            }
        }

        return result
    }

    @TargetApi(21)
    private fun drawNoteHead(note: Note, canvas: Canvas) {
        val mediaPlayer = MediaPlayer()

        try {
            mediaPlayer.setDataSource(context, Uri.parse("android.resource://com.viviose.musicscratchpad/raw/" + note.name.toString() + Integer.toString(note.octave)))
        } catch (e: Exception) {

        }

        Log.i("Media Playing:", "Player created!")
        mediaPlayer.setOnPreparedListener { player -> player.start() }
        mediaPlayer.setOnCompletionListener { mp -> mp.release() }
        try {
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {

        }

        if (note.y - DensityMetrics.getToolbarHeight() <= DensityMetrics.spaceHeight * 2) {
            canvas.drawLine(note.x - 200, DensityMetrics.spaceHeight + DensityMetrics.getToolbarHeight(), note.x + 200, DensityMetrics.spaceHeight + DensityMetrics.getToolbarHeight(), paint)
        }
        if (note.y - DensityMetrics.getToolbarHeight() >= DensityMetrics.spaceHeight * 6) {
            canvas.drawLine(note.x - 200, DensityMetrics.spaceHeight * 7 + DensityMetrics.getToolbarHeight(), note.x + 200, DensityMetrics.spaceHeight * 7 + DensityMetrics.getToolbarHeight(), paint)
        }
        //canvas.drawOval(note.x - NOTE_WIDTH, note.y - DensityMetrics.spaceHeight / 2, note.x + NOTE_WIDTH, note.y + DensityMetrics.spaceHeight / 2, paint);
        val headBmap: Bitmap
        if (note.rhythm == 2.0) {
            headBmap = NoteBitmap.hnh

        } else {
            headBmap = NoteBitmap.qnh
        }


        canvas.drawBitmap(Bitmap.createScaledBitmap(headBmap, (DensityMetrics.spaceHeight * 1.697).toInt(), DensityMetrics.spaceHeight.toInt(), true), (note.x - DensityMetrics.spaceHeight * 1.697 / 2).toInt().toFloat(), note.y - DensityMetrics.spaceHeight / 2, paint)
        if (drawDot) {
            //TODO: I don't want these hardcoded obviously
            canvas.drawCircle(note.x + 20, note.y + 20, 5f, paint)
        }
        if ((accidental == 1 && note.name != Note.NoteName.b && note.name != Note.NoteName.e) || note.accidental == 1) {
            val vd = ContextCompat.getDrawable(context, R.drawable.sharp) as VectorDrawable
            val b = NoteBitmap.getBitmap(vd)
            canvas.drawBitmap(Bitmap.createScaledBitmap(b, (NOTE_HEIGHT * 3 / 2).toInt(), NOTE_HEIGHT.toInt() * 3, true), note.x - NOTE_WIDTH * 2, note.y - NOTE_HEIGHT * 3 / 2, paint)
        }else if (accidental == -1 && note.name != Note.NoteName.f && note.name != Note.NoteName.c){
            val vd = ContextCompat.getDrawable(context, R.drawable.flat) as VectorDrawable
            val b = NoteBitmap.getBitmap(vd)
            canvas.drawBitmap(Bitmap.createScaledBitmap(b, (NOTE_HEIGHT * 1.35).toInt(), NOTE_HEIGHT.toInt() * 3, true), note.x - NOTE_WIDTH * 2, note.y - NOTE_HEIGHT * 3 / 2, paint)
        }

        accidental = 0

        //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.alto_clef);
        //c.drawBitmap(Bitmap.createScaledBitmap(b, (int) ((4 * (int) DensityMetrics.spaceHeight) / 1.5), 4 * (int) DensityMetrics.spaceHeight, true), 20, altoClef, paint);

    }

    internal inner class mListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            Log.d(DEBUG_TAG, "onDown: " + event.toString())
            return true
        }

        override fun onFling(ev1: MotionEvent, ev2: MotionEvent, velX: Float, velY: Float): Boolean {
            if (velY > 5000) {
                accidental = -1
            }
            if (velY < -5000) {
                accidental = 1
            }

            if (velX < -5000) {
                if (drawDot) {
                    LastRhythm.value /= 1.5
                }
                drawDot = false

            }
            if (velX > 5000) {
                if (!drawDot) {
                    LastRhythm.value *= 1.5
                }
                drawDot = true
                Log.d("Fling", "Flung")
            }
            return true
        }

        override fun onLongPress(event: MotionEvent) {
            Log.d(DEBUG_TAG, "onLongPress: " + event.toString())
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float,
                              distanceY: Float): Boolean {
            return true
        }

        override fun onShowPress(event: MotionEvent) {
            Log.d(DEBUG_TAG, "onShowPress: " + event.toString())
        }

        override fun onSingleTapUp(event: MotionEvent): Boolean {
            Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString())
            return true
        }

    }

    companion object {
        private val TAG = "EditorView"
    }


}