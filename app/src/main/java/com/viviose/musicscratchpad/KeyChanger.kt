package com.viviose.musicscratchpad

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Button

class KeyChanger : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val sendBack = Intent(this, MainActivity::class.java)
        setContentView(R.layout.activity_key_changer)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


        val C = findViewById(R.id.key_c) as Button
        C.setOnClickListener {
            Key.COUNT = 0
            Key.SHARP = true
            startActivity(sendBack)
        }

        //FLATS
        val F = findViewById(R.id.key_f) as Button
        F.setOnClickListener {
            Key.COUNT = 1
            Key.SHARP = false
            startActivity(sendBack)
        }

        val Bf = findViewById(R.id.key_bf) as Button
        Bf.setOnClickListener {
            Key.COUNT = 2
            Key.SHARP = false
            startActivity(sendBack)
        }
        val Ef = findViewById(R.id.key_ef) as Button
        Ef.setOnClickListener {
            Key.COUNT = 3
            Key.SHARP = false
            startActivity(sendBack)
        }
        val Af = findViewById(R.id.key_af) as Button
        Af.setOnClickListener {
            Key.COUNT = 4
            Key.SHARP = false
            startActivity(sendBack)
        }
        val Df = findViewById(R.id.key_df) as Button
        Df.setOnClickListener {
            Key.COUNT = 5
            Key.SHARP = false
            startActivity(sendBack)
        }
        val Gf = findViewById(R.id.key_gf) as Button
        Gf.setOnClickListener {
            Key.COUNT = 6
            Key.SHARP = false
            startActivity(sendBack)
        }
        //SHARPS
        val G = findViewById(R.id.key_g) as Button
        G.setOnClickListener {
            Key.COUNT = 1
            Key.SHARP = true
            startActivity(sendBack)
        }

        val D = findViewById(R.id.key_d) as Button
        D.setOnClickListener {
            Key.COUNT = 2
            Key.SHARP = true
            startActivity(sendBack)
        }
        val A = findViewById(R.id.key_a) as Button
        A.setOnClickListener {
            Key.COUNT = 3
            Key.SHARP = true
            startActivity(sendBack)
        }
        val E = findViewById(R.id.key_e) as Button
        E.setOnClickListener {
            Key.COUNT = 4
            Key.SHARP = true
            startActivity(sendBack)
        }
        val B = findViewById(R.id.key_b) as Button
        B.setOnClickListener {
            Key.COUNT = 5
            Key.SHARP = true
            startActivity(sendBack)
        }
        val Fs = findViewById(R.id.key_fs) as Button
        Fs.setOnClickListener {
            Key.COUNT = 6
            Key.SHARP = true
            startActivity(sendBack)
        }


        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        navigationView.menu.getItem(1).isChecked = true
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
        menuInflater.inflate(R.menu.key_changer, menu)
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
}
