package com.viviose.musicscratchpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.VectorDrawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.os.Vibrator;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnShowcaseEventListener{
    LinearLayout rBar = null;
    int showCaseIterator;
    View ev;
    private SwitchCompat pianoSwitch;
    DrawerLayout dl;
    final String PREFS_NAME = "MyPrefsFile";
    ShowcaseView.Builder[] SHOWCASES;

    public void showBar(){
        rBar.setVisibility(LinearLayout.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context con = this;
        rBar = (LinearLayout) findViewById(R.id.rhythm_bar);
        rBar.setVisibility(View.GONE);

        ev = findViewById(R.id.editor_canvas);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        SwitchCompat singleTap = (SwitchCompat) navigationView.getMenu().getItem(0).getActionView().findViewById(R.id.singletap_switch);

        singleTap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.piano = isChecked;
                Log.d("Switch", "You a-toggled mah switch");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                VectorDrawable quarterNoteHead = (VectorDrawable) ContextCompat.getDrawable(con, R.drawable.quarter_note_head);
                NoteBitmap.qnh = NoteBitmap.getBitmap(quarterNoteHead);
                //VectorDrawable halfNoteHead = (VectorDrawable) ContextCompat.getDrawable(con, R.drawable.half_note_head);
                //NoteBitmap.hnh = NoteBitmap.getBitmap(halfNoteHead);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //VectorDrawable quarterNoteHead = (VectorDrawable) ContextCompat.getDrawable(con, R.drawable.quarter_note_head);
                //NoteBitmap.qnh = NoteBitmap.getBitmap(quarterNoteHead);
                VectorDrawable halfNoteHead = (VectorDrawable) ContextCompat.getDrawable(con, R.drawable.half_note_head);
                NoteBitmap.hnh = NoteBitmap.getBitmap(halfNoteHead);
            }
        }).start();
        DP.r = getResources();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DensityMetrics.setToolbar(toolbar);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task
            //SHOWCASES[0].build().show();
            // record the fact that the app has been started at least once
            //settings.edit().putBoolean("my_first_time", false).commit();
        }


        ViewTarget vtarget = new ViewTarget(findViewById(R.id.editor_canvas));



        //Setting the correct media stream
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        ImageButton eighthButton = (ImageButton) findViewById(R.id.eighth_note);
        eighthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Rythym", "Change to 1/8 note");
                LastRhythm.value = .5;
                rBar.setVisibility(View.GONE);
            }
        });

        ImageButton quarterButton = (ImageButton) findViewById(R.id.quarter_note);
        quarterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Rythym", "Change to 1/4 note");
                LastRhythm.value = 1;
                rBar.setVisibility(View.GONE);
            }
        });

        ImageButton halfButton = (ImageButton) findViewById(R.id.half_note);
        halfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Rythym", "Change to 1/2 note");
                LastRhythm.value = 2;
                rBar.setVisibility(View.GONE);
            }
        });
        ImageButton wholeButton = (ImageButton) findViewById(R.id.whole_note);
        wholeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Rythym", "Change to whole note");
                LastRhythm.value = 4;
                rBar.setVisibility(View.GONE);
            }
        });


        ImageButton upInc = (ImageButton) findViewById(R.id.inc_octave);
        upInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Octave.octave += 1;
            }
        });

        ImageButton downDec = (ImageButton) findViewById(R.id.dec_octave);
        downDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Octave.octave -= 1;
            }
        });

        dl = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, dl, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.setDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextInput();
            }
        });



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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        }else if (id == R.id.action_rhythm){
            rBar.setVisibility(View.VISIBLE);
        }else if (id == R.id.action_undo){
            if(MusicStore.activeNotes.size() == 0){
                if (MusicStore.sheet.size() > 0){
                    MusicStore.sheet.remove(MusicStore.sheet.size() - 1);
                }
            }else{
                MusicStore.activeNotes.clear();
            }
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
            Intent sendToKeys = new Intent(this, KeyChanger.class);
            startActivity(sendToKeys);

        } else if (id == R.id.nav_clefs) {
            Intent sendToClefs = new Intent(this, ClefChanger.class);
            startActivity(sendToClefs);
        } else if (id == R.id.nav_view_composition) {
            Intent sendToComp = new Intent(this, Composition.class);
            startActivity(sendToComp);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.singletap) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void nextInput(){
        new Thread() {
            @Override
            public void run() {
                MusicStore.sheet.add(MusicStore.activeNotes);
                System.gc();
                for (
                        Note note
                        : MusicStore.activeNotes)

                {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(), Uri.parse("android.resource://com.viviose.musicscratchpad/raw/" + note.getName().toString() + Integer.toString(note.getOctave())));
                    } catch (Exception e) {

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
                    } catch (Exception e) {

                    }


                }

                MusicStore.activeNotes = new ArrayList<Note>();
            }
        }.start();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void dimView(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setAlpha(0.0f);
            findViewById(R.id.dimmable_layout).setAlpha(0);
        }
    }
    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
            dimView(dl);
        Log.d("ShowcaseView", "Show");
    }
    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

    }


    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        dl.setAlpha(1f);
        Log.d("ShowcaseView", "Hid");
        SHOWCASES[showCaseIterator].build();
        showCaseIterator++;
        Log.d("ShowcaseView", "Hid");
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }



}
