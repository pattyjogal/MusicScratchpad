package com.viviose.musicscratchpad

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.VectorDrawable
import android.media.AudioManager
import android.media.Image
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.LinearLayout
import com.github.amlcurran.showcaseview.targets.ViewTarget
import android.animation.AnimatorListenerAdapter
import android.view.animation.OvershootInterpolator
import java.util.ArrayList
import com.github.clans.fab.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    internal var rBar: LinearLayout? = null
    internal var showCaseIterator: Int = 0
    internal var ev: View? = null
    private val pianoSwitch: SwitchCompat? = null
    internal var dl: DrawerLayout? = null
    internal val PREFS_NAME = "StaffpadPrefs"
    internal val SELECTED_COLOR: Int = Color.rgb(229, 115, 115)
    internal var REGULAR_COLOR: Int = Color.RED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val con = this
        ev = findViewById(R.id.editor_canvas)

        //Loading Prefs
        val preferences: SharedPreferences = getSharedPreferences(PREFS_NAME, 0)
        val editor: SharedPreferences.Editor = preferences.edit()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val singleTap = navigationView.menu.getItem(0).actionView.findViewById(R.id.singletap_switch) as SwitchCompat

        singleTap.isChecked = preferences.getBoolean("single_tap", false)
        Settings.piano = preferences.getBoolean("single_tap", false)

        singleTap.setOnCheckedChangeListener { buttonView, isChecked ->
            Settings.piano = isChecked
            editor.putBoolean("single_tap", isChecked)
            editor.commit()
            Log.d("Switch", "You a-toggled mah switch")
        }

        Thread(Runnable {
            val quarterNoteHead = ContextCompat.getDrawable(con, R.drawable.quarter_note_head) as VectorDrawable
            NoteBitmap.qnh = NoteBitmap.getBitmap(quarterNoteHead)
            //VectorDrawable halfNoteHead = (VectorDrawable) ContextCompat.getDrawable(con, R.drawable.half_note_head);
            //NoteBitmap.hnh = NoteBitmap.getBitmap(halfNoteHead);
        }).start()
        Thread(Runnable {
            //VectorDrawable quarterNoteHead = (VectorDrawable) ContextCompat.getDrawable(con, R.drawable.quarter_note_head);
            //NoteBitmap.qnh = NoteBitmap.getBitmap(quarterNoteHead);
            val halfNoteHead = ContextCompat.getDrawable(con, R.drawable.half_note_head) as VectorDrawable
            NoteBitmap.hnh = NoteBitmap.getBitmap(halfNoteHead)
        }).start()
        DP.r = resources
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        DensityMetrics.toolbar = toolbar
        val settings = getSharedPreferences(PREFS_NAME, 0)

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time")

            // first time task
            //SHOWCASES[0].build().show();
            // record the fact that the app has been started at least once
            //settings.edit().putBoolean("my_first_time", false).commit();
        }
        //Setting the correct media stream
        volumeControlStream = AudioManager.STREAM_MUSIC

        val upInc = findViewById(R.id.inc_octave) as ImageButton
        upInc.setOnClickListener { Octave.octave += 1 }

        val downDec = findViewById(R.id.dec_octave) as ImageButton
        downDec.setOnClickListener { Octave.octave -= 1 }

        dl = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, dl, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        dl?.setDrawerListener(toggle)
        toggle.syncState()


        navigationView.setNavigationItemSelectedListener(this)

        /*val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { nextInput() }*/
        val rhythmMenu: FloatingActionMenu = findViewById(R.id.rhythm_menu) as FloatingActionMenu
        REGULAR_COLOR = rhythmMenu.menuButtonColorNormal
        //Loading fabs
        val fab_sixteenth: FloatingActionButton = findViewById(R.id.sixteenth_note) as FloatingActionButton
        fab_sixteenth.setOnClickListener {
            LastRhythm.value = .25
            rhythmMenu.close(true)
        }

        val fab_eighth: FloatingActionButton = findViewById(R.id.eighth_note) as FloatingActionButton
        fab_eighth.setOnClickListener {
            LastRhythm.value = .5
            rhythmMenu.close(true)
        }
        val fab_quarter: FloatingActionButton = findViewById(R.id.quarter_note) as FloatingActionButton
        fab_quarter.setOnClickListener {
            LastRhythm.value = 1.0
            rhythmMenu.close(true)
        }
        val fab_half: FloatingActionButton = findViewById(R.id.half_note) as FloatingActionButton
        fab_half.setOnClickListener {
            LastRhythm.value = 2.0
            rhythmMenu.close(true)
        }
        val fab_whole: FloatingActionButton = findViewById(R.id.whole_note) as FloatingActionButton
        fab_whole.setOnClickListener {
            LastRhythm.value = 4.0
            rhythmMenu.close(true)
        }


        var set: AnimatorSet = AnimatorSet()

        var scaleOutX: ObjectAnimator = ObjectAnimator.ofFloat(rhythmMenu.menuIconView, "scaleX", 1.0f, 0.2f)
        var scaleOutY: ObjectAnimator = ObjectAnimator.ofFloat(rhythmMenu.menuIconView, "scaleY", 1.0f, 0.2f)
        var scaleInX: ObjectAnimator = ObjectAnimator.ofFloat(rhythmMenu.menuIconView, "scaleX", 0.2f, 1.0f)
        var scaleInY: ObjectAnimator = ObjectAnimator.ofFloat(rhythmMenu.menuIconView, "scaleY", 0.2f, 1.0f)

        scaleOutX.duration = 50
        scaleOutY.duration = 50
        scaleInX.duration = 150
        scaleInY.duration = 150

        scaleInX.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator) {
                rhythmMenu.menuIconView.setImageResource(if (rhythmMenu.isOpened)
                    R.drawable.ic_audiotrack_24dp_white
                else
                    R.drawable.ic_close_white_24dp)
                if (!rhythmMenu.isOpened) {
                    when (LastRhythm.value) {
                        4.0 -> fab_whole.colorNormal = SELECTED_COLOR
                        2.0 -> fab_half.colorNormal = SELECTED_COLOR
                        1.0 -> fab_quarter.colorNormal = SELECTED_COLOR
                        .5 -> fab_eighth.colorNormal = SELECTED_COLOR
                        .25 -> fab_sixteenth.colorNormal = SELECTED_COLOR
                    }
                } else {
                    fab_whole.colorNormal = REGULAR_COLOR
                    fab_half.colorNormal = REGULAR_COLOR
                    fab_quarter.colorNormal = REGULAR_COLOR
                    fab_eighth.colorNormal = REGULAR_COLOR
                    fab_sixteenth.colorNormal = REGULAR_COLOR


                }
            }
        })
        set.play(scaleOutX).with(scaleOutY)
        set.play(scaleInX).with(scaleInY).after(scaleOutX)
        set.interpolator = OvershootInterpolator(2f)

        rhythmMenu.iconToggleAnimatorSet = set


    }


    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        } else if (id == R.id.action_rhythm) {
            rBar!!.visibility = View.VISIBLE
        } else if (id == R.id.action_undo) {
            if (MusicStore.activeNotes.size == 0) {
                if (MusicStore.sheet.size > 0) {
                    MusicStore.sheet.removeAt(MusicStore.sheet.size - 1)
                }
            } else {
                MusicStore.activeNotes.clear()
            }
        }


        return super.onOptionsItemSelected(item)
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_keys) {
            val sendToKeys = Intent(this, KeyChanger::class.java)
            startActivity(sendToKeys)

        } else if (id == R.id.nav_clefs) {
            val sendToClefs = Intent(this, ClefChanger::class.java)
            startActivity(sendToClefs)
        } else if (id == R.id.nav_view_composition) {
            val sendToComp = Intent(this, Composition::class.java)
            startActivity(sendToComp)
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.singletap) {

        }


        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun nextInput() {
        object : Thread() {
            override fun run() {
                MusicStore.sheet.add(MusicStore.activeNotes)
                System.gc()
                for (note in MusicStore.activeNotes) {
                    val mediaPlayer = MediaPlayer()
                    try {
                        mediaPlayer.setDataSource(applicationContext, Uri.parse("android.resource://com.viviose.musicscratchpad/raw/" + note.name!!.toString() + Integer.toString(note.octave)))
                    } catch (e: Exception) {

                    }

                    Log.i("Media Playing:", "Player created!")
                    mediaPlayer.setOnPreparedListener { player -> player.start() }
                    mediaPlayer.setOnCompletionListener { mp -> mp.release() }
                    try {
                        mediaPlayer.prepareAsync()
                    } catch (e: Exception) {

                    }


                }

                MusicStore.activeNotes = ArrayList<Note>()
            }
        }.start()
    }


}
