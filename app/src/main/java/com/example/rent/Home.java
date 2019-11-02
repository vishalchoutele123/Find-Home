package com.example.rent;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rent.Fragments.MyPropertyFragment;
import com.example.rent.Fragments.NotificationFragment;
import com.example.rent.Fragments.ProfileFragment;
import com.example.rent.Fragments.ShortListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private android.support.v7.widget.Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    private View navHeaderView ;
    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListner ;

    private DatabaseReference mDatabase ;
    private  String user_id ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeStuff();

        navHeaderView = navigationView.getHeaderView(0);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid() ;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                {
                    Intent mainIntent = new Intent(Home.this , MainActivity.class);
                    startActivity(mainIntent);
                }

            }
        };


        setSupportActionBar(toolbar);

        setUpNavigationView(navigationView);

        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameContent,new TabFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
        setTitle("Find Home");

    }

    void initializeStuff(){
        drawerLayout   =  findViewById(R.id.drawerLayout);
        toolbar        =  findViewById(R.id.toolbar);
        navigationView =  findViewById(R.id.navigationDrawer);


    }

    private void setUpNavigationView(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //replace the current fragment with the new fragment.

                        if (menuItem.getItemId()!=R.id.nav_logout) {
                            Fragment selectedFragment = selectDrawerItem(menuItem);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.frameContent, selectedFragment).commit();
                            // the current menu item is highlighted in navigation tray.
                            navigationView.setCheckedItem(menuItem.getItemId());
                            if (menuItem.getItemId() == R.id.nav_home) {
                                setTitle("Find Home");
                            } else {
                                setTitle(menuItem.getTitle());
                            }
                        }
                        else if (menuItem.getItemId() == R.id.nav_logout){
                            toLogin();
                        }
//                        else if (menuItem.getItemId() == R.id.nav_my_property){
//                            toMyProperty();
//                        }

                        //close the drawer when user selects a nav item.
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    public Fragment selectDrawerItem(MenuItem menuItem){
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = new TabFragment();
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
            case R.id.nav_logout:
                break;
//            case  R.id.nav_shortlist:
//                fragment = new ShortListFragment();
//                break;
            case R.id.nav_my_property:
                fragment = new MyPropertyFragment();
                break;
        }
        return fragment;
    }


    void toLogin()
    {
        mAuth.signOut();
    }

    void toMyProperty(){
        Intent toProperty = new Intent(Home.this,MyProperty.class);
        startActivity(toProperty);
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.drawer_open,R.string.drawer_close);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }


    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);



    }
//
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
