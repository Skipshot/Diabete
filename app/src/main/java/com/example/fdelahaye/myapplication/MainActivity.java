package com.example.fdelahaye.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.fdelahaye.myapplication.SettingsFragment.OnFragmentInteractionListener;

public class MainActivity extends AppCompatActivity

        //Note : OnFragmentInteractionListener of all fragments
        implements
        CheckFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,

        NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*navigationView.setCheckedItem(R.id.nav_fragment_test);
        TestFragment fragmentMain = new TestFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,fragmentMain).commit();*/

        //NOTE:  Checks first item in the navigation drawer initially and init first fragment to show
        if (!JsonUtil.fileExists(getApplicationContext(), getString(R.string.SettingsJsonFilename))) {
            //First run : show settings
            navigationView.setCheckedItem(R.id.nav_fragment_settings);
            SettingsFragment fragmentMain = new SettingsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,fragmentMain).commit();
        } else {
            //Next runs : show check fragment
            navigationView.setCheckedItem(R.id.nav_fragment_check);
            CheckFragment fragmentMain = new CheckFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, fragmentMain).commit();
        }
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*@Override
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
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        //NOTE: creating fragment object
        Fragment fragment = null;
        if (id == R.id.nav_fragment_settings) {
            fragment = new SettingsFragment();
        } else if(id == R.id.nav_fragment_check) {
            fragment = new CheckFragment();
        }

        //NOTE: Fragment changing code
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,fragment).commit();
        }
        //NOTE:  Closing the drawer after selecting
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //Ya you can also globalize this variable :P
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onFragmentInteraction(String title) {
        // NOTE:  Code to replace the toolbar title based current visible fragment
        getSupportActionBar().setTitle(title);
    }
}
