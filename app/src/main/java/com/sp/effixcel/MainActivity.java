package com.sp.effixcel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView nv;
    private BottomNavigationView navView;

    private CalendarFragment calendarfragment;
    private TasksFragment tasksfragment;
    private NotesFragment notesfragment;
    // private Toolbar toolbar;

    private int bottomSelectedMenu = R.id.nav_calendar;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Check the shared preferences to see if the user is already logged in.
        If the isLoggedIn flag is set to true, skip the login screen and directly open the main activity.
        */

        SharedPreferences preferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // User is not logged in, navigate to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Optional: Close MainActivity
            return;
        }

        //Bottom Navigation
        navView = findViewById(R.id.bottomNavigationView);
        navView.setOnItemSelectedListener(menuSelected);

        calendarfragment = new CalendarFragment();
        tasksfragment = new TasksFragment();
        notesfragment = new NotesFragment();

        // toolbar = findViewById(R.id.toolbar);

        //Navigation Drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //toggle menu icon to open drawer and back signin_button to close drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_view);
        nv = (NavigationView) findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav, R.string.close_nav);

        setupDrawerContent(nv);

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //Bottom Navigation Drawer
    @Override
    protected void onStart()
    {
        // Set calendar tab as the default
        navView.setSelectedItemId(R.id.nav_calendar);
        super.onStart();

    }

    NavigationBarView.OnItemSelectedListener menuSelected = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            invalidateOptionsMenu();

            // Check if the fragments are not null before performing the transaction
            if (id == R.id.nav_calendar && calendarfragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.effixcelFragmentContainer, calendarfragment)
                        .setReorderingAllowed(true)
                        .commit();
                return true;
            } else if (id == R.id.nav_tasks && tasksfragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.effixcelFragmentContainer, tasksfragment)
                        .setReorderingAllowed(true)
                        .commit();
                return true;
            } else if (id == R.id.nav_notes && notesfragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.effixcelFragmentContainer, notesfragment)
                        .setReorderingAllowed(true)
                        .commit();
                return true;
            }

            return false;
        }
    };


    NavigationView.OnNavigationItemSelectedListener navSelected = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                performLogout();
                return true;
            } else {
                Fragment fragment = null;
                if (id == R.id.nav_account)
                    fragment = new AccountFragment();
                else if (id == R.id.nav_about)
                    fragment = new AboutFragment();
                else if (id == R.id.nav_webview)
                    fragment = new WebViewFragment();

                if (fragment != null) {
                    fragmentManager.beginTransaction().replace(R.id.effixcelFragmentContainer, fragment).commit();
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        }
    };

    private void performLogout() {
        // Clear the user login session by updating the isLoggedIn flag in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Finish all activities in the task and redirect the user to the LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    // Navigation Drawer
    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_logout) {
            performLogout();
            return; // Exit the method early to avoid further execution
        }

        Fragment fragment = null;
        Class<? extends Fragment> fragmentClass = null;

        if (id == R.id.nav_home) {
            // Navigate to the MainActivity (home) page
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawers(); // Close the navigation drawer
            return;
        }

        // Handle the rest of the fragment replacements
        if (id == R.id.nav_account) {
            fragmentClass = AccountFragment.class;
        } else if (id == R.id.nav_about) {
            fragmentClass = AboutFragment.class;
        }  else if (id == R.id.nav_webview) {
            fragmentClass = WebViewFragment.class;
        } else {
            // Handle unrecognized menu item, or simply return if you don't want to replace the fragment
            return;
        }

        // Check if fragmentClass is not null before instantiating the fragment
        if (fragmentClass != null) {
            try {
                fragment = fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Insert the fragment by replacing an existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.effixcelFragmentContainer, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}