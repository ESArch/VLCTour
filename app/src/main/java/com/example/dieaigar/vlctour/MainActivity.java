package com.example.dieaigar.vlctour;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dieaigar.vlctour.databases.MySqliteOpenHelper;
import com.example.dieaigar.vlctour.fragments.NearMeFragment;
import com.example.dieaigar.vlctour.fragments.POIFragment;
import com.example.dieaigar.vlctour.fragments.RoutesFragment;
import com.example.dieaigar.vlctour.fragments.ListRoutesFragment;
import com.example.dieaigar.vlctour.fragments.SignInFragment;
import com.example.dieaigar.vlctour.fragments.SignUpFragment;
import com.example.dieaigar.vlctour.fragments.SaveRouteFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = new String[5];
    int ICONS[] = {R.drawable.ic_home,R.drawable.ic_near,R.drawable.ic_pois,R.drawable.ic_routes, R.drawable.ic_login};

    Fragment fragment = null;
    ArrayList<Integer> ruta = new ArrayList<>();

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME;
    String EMAIL;
    int PROFILE = R.drawable.avatar;

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TITLES[0] = getString(R.string.option1_drawer);
        TITLES[1] = getString(R.string.option2_drawer);
        TITLES[2] = getString(R.string.option3_drawer);
        TITLES[3] = getString(R.string.option4_drawer);
        TITLES[4] = getString(R.string.option5_drawer);

        //Similarly we Create a String Resource for the name and email in the header view
        //And we also create a int resource for profile picture in the header view

        NAME = getString(R.string.user_name_drawer);
        EMAIL = getString(R.string.user_email_drawer);

        MySqliteOpenHelper sqliteOpenHelper = MySqliteOpenHelper.getInstance(this);

    /* Assinging the toolbar object ot the view
    and setting the the Action bar to our toolbar
     */
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView


        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });


        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());


                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Drawer.closeDrawers();
                    int position = recyclerView.getChildPosition(child);
                    Intent intent = null;


                    switch (position){
                        case 1:
                            break;
                        case 2:
                            fragment = new NearMeFragment();
                            break;
                        case 3:
                            fragment = new POIFragment();
                            break;
                        case 4:
                            fragment = new ListRoutesFragment();
                            break;
                        case 5:
                            fragment = new SignInFragment();
                            break;
                    }


                    if(fragment != null){
                        // Insert the fragment by replacing any existing fragment
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .commit();
                    }

                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.drawer_layout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
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
        if (id == R.id.menu_credits) {
            Toast.makeText(MainActivity.this, "Credits", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
    public void filterChange(View view) {
        //TODO mostrar u ocultar en el mapa el marker.
        NearMeFragment nearMeFragment = (NearMeFragment) fragment;
        Log.d("filterChange1Before",nearMeFragment.getFilters().get(1).toString());
        Log.d("filterChange0Before",nearMeFragment.getFilters().get(0).toString());
        if(nearMeFragment.getFilters().get(0).contains(getResources().getResourceEntryName(view.getId()))) {
            nearMeFragment.getFilters().get(1).add(getResources().getResourceEntryName(view.getId()));
            Log.d("DEBUG","Añadido a 1");
            nearMeFragment.getFilters().get(0).remove(getResources().getResourceEntryName(view.getId()));
            Log.d("DEBUG","Eliminado de 0");
            nearMeFragment.changeColor(getResources().getResourceEntryName(view.getId()),1);
        }
        else if(nearMeFragment.getFilters().get(1).contains(getResources().getResourceEntryName(view.getId()))) {
            nearMeFragment.getFilters().get(0).add(getResources().getResourceEntryName(view.getId()));
            Log.d("DEBUG","Añadido a 0");
            nearMeFragment.getFilters().get(1).remove(getResources().getResourceEntryName(view.getId()));
            Log.d("DEBUG","Eliminado de 1");
            nearMeFragment.changeColor(getResources().getResourceEntryName(view.getId()),0);
        }
        Log.d("filterChange1After",nearMeFragment.getFilters().get(1).toString());
        Log.d("filterChange0After",nearMeFragment.getFilters().get(0).toString());
    }
    */
    public void login(String username, String email){
        NAME = username;
        mAdapter.notifyDataSetChanged();
    }
}