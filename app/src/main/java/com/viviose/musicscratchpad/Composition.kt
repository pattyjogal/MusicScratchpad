package com.viviose.musicscratchpad

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Looper
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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
import android.widget.Toast

import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.CountDownLatch

class Composition : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private fun nameToNum(note: Note): Int {
        var noteNum = -1
        if (note.name == Note.NoteName.c) {
            noteNum = 12 * note.octave
        } else if (note.name == Note.NoteName.cs) {
            noteNum = 12 * note.octave + 1
        } else if (note.name == Note.NoteName.d) {
            noteNum = 12 * note.octave + 2
        } else if (note.name == Note.NoteName.ds) {
            noteNum = 12 * note.octave + 3
        } else if (note.name == Note.NoteName.e) {
            noteNum = 12 * note.octave + 4
        } else if (note.name == Note.NoteName.f) {
            noteNum = 12 * note.octave + 5
        } else if (note.name == Note.NoteName.fs) {
            noteNum = 12 * note.octave + 6
        } else if (note.name == Note.NoteName.g) {
            noteNum = 12 * note.octave + 7
        } else if (note.name == Note.NoteName.gs) {
            noteNum = 12 * note.octave + 8
        } else if (note.name == Note.NoteName.a) {
            noteNum = 12 * note.octave + 9
        } else if (note.name == Note.NoteName.`as`) {
            noteNum = 12 * note.octave + 10
        } else if (note.name == Note.NoteName.b) {
            noteNum = 12 * note.octave + 11
        }
        return noteNum
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_composition)

        //PERMISSIONS:

        val hasPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_STORAGE)
        }


        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        //MIDI Composition

        val tempoTrack = MidiTrack()
        val noteTrack = MidiTrack()

        //TODO: Make the timesignatures dynamic
        val ts = TimeSignature()
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)

        //TODO: Make BPM Dynamic
        val tempo = Tempo()
        tempo.bpm = 60f

        tempoTrack.insertEvent(ts)
        tempoTrack.insertEvent(tempo)
        var lastR = 0.0
        var tick: Long = 0
        var tempTick: Long = 0

        for (chord in MusicStore.sheet) {
            var chordMargin: Long = 0
            for (note in chord) {
                tempTick = (tick + 480 * lastR).toLong()

                noteTrack.insertNote(0, nameToNum(note), 100, tempTick + chordMargin, 120 * note.rhythm.toLong())
                chordMargin += .00001 as Long
                lastR = note.rhythm
            }
            //TODO: This won't work for polyrhythmic chords lmao
            tick = tempTick
        }


        val tracks = ArrayList<MidiTrack>()
        tracks.add(tempoTrack)
        noteTrack.insertEvent(ProgramChange(0, 0, 1))
        tracks.add(noteTrack)

        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)
        val sdCard = Environment.getExternalStorageDirectory()
        val o = File(sdCard, "music.mid")
        try {
            midi.writeToFile(o)

            Toast.makeText(this, "File WRITTEN!, find it here: " + "/sdcard/Music/music.mid", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            System.err.println(e)
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()

        }

        System.gc()
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val mp = MediaPlayer()
            try {
                mp.setDataSource(o.path)
            } catch (e: IOException) {
                System.err.println("Couldn't init media player")
            }

            mp.setOnPreparedListener { mp ->
                mp.start()
                Log.i("MP", "Playing MIDI")
            }
            try {
                mp.prepare()
            } catch (e: Exception) {
                Log.e("MP", "Error with media player prepare")
            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            val sendBackToMain = Intent(this, MainActivity::class.java)
            startActivity(sendBackToMain)
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.composition, menu)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    companion object {

        private val REQUEST_WRITE_STORAGE = 112
    }

}
