package com.viviose.musicscratchpad;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Composition extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_WRITE_STORAGE = 112;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);

        //PERMISSIONS:

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //MIDI Composition

        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack noteTrack = new MidiTrack();

        //TODO: Make the timesignatures dynamic
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        //TODO: Make BPM Dynamic
        Tempo tempo = new Tempo();
        tempo.setBpm(60);

        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(tempo);
        long iterator = 0;
        long tick = 0;

        for (ArrayList<Note> chord : MusicStore.sheet){
            long chordMargin = 0;
            for (Note note : chord){
                int noteNum = 0;
                if(note.name == Note.NoteName.c){
                    noteNum = 12 * note.octave;
                }else if(note.name == Note.NoteName.cs){
                    noteNum = 12 * note.octave + 1;
                }else if(note.name == Note.NoteName.d){
                    noteNum = 12 * note.octave + 2;
                }else if(note.name == Note.NoteName.cs){
                    noteNum = 12 * note.octave + 3;
                }else if(note.name == Note.NoteName.e){
                    noteNum = 12 * note.octave + 4;
                }else if(note.name == Note.NoteName.f){
                    noteNum = 12 * note.octave + 5;
                }else if(note.name == Note.NoteName.fs){
                    noteNum = 12 * note.octave + 6;
                }else if(note.name == Note.NoteName.g){
                    noteNum = 12 * note.octave + 7;
                }else if(note.name == Note.NoteName.gs){
                    noteNum = 12 * note.octave + 8;
                }else if(note.name == Note.NoteName.a){
                    noteNum = 12 * note.octave + 9;
                }else if(note.name == Note.NoteName.as){
                    noteNum = 12 * note.octave + 10;
                }else if(note.name == Note.NoteName.b){
                    noteNum = 12 * note.octave + 11;
                }
                tick += 480 * note.rhythm;

                noteTrack.insertNote(0, noteNum, 100, tick  + chordMargin, 120);
                chordMargin += .00001;
            }
            iterator++;
        }

        ArrayList<MidiTrack> tracks = new ArrayList<>();
        tracks.add(tempoTrack);
        noteTrack.insertEvent(new ProgramChange(0, 0, 1));
        tracks.add(noteTrack);

        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
        final File sdCard = Environment.getExternalStorageDirectory();
        final File o = new File(sdCard, "music.mid");
        try{
            midi.writeToFile(o);
            Toast.makeText(this, "File WRITTEN!, find it here: " + "/sdcard/Music/music.mid", Toast.LENGTH_SHORT).show();
        }catch(IOException e){
            System.err.println(e);
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mp = new MediaPlayer();
                try{
                    mp.setDataSource(o.getPath());
                }catch(IOException e){
                    System.err.println("Couldn't init media player");
                }
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        Log.i("MP", "Playing MIDI");
                    }
                });
                try{
                    mp.prepare();
                }catch (Exception e){
                    Log.e("MP", "Error with media player prepare");
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.composition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_keys) {

        } else if (id == R.id.nav_clefs) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
