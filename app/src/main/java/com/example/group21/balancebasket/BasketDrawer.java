package com.example.group21.balancebasket;

// TODO: first page should be Connectivity screen. Not showing the drawer button. When a mode is chosen by the user, go to selected fragment
// TODO: if bluetooth connection is not established or lost, disable the drawer menu items that require bluetooth connection.
// TODO: Add Shopping list

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class BasketDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ImuFragment.OnFragmentInteractionListener, JoystickFragment.OnFragmentInteractionListener, FollowFragment.OnFragmentInteractionListener {
    private static final String TAG = "BasketDrawer";
    public static final boolean D = BuildConfig.DEBUG; // This is automatically set when building
    private static final String NAV_ITEM_ID = "navItemId";

    private static Activity activity;
    private static Context context;

    public static boolean joystickReleased = true;

    public static Bluetooth bluetoothService;

    private ImuFragment imuFragment;
    private JoystickFragment joystickFragment;
    private FollowFragment followFragment;
    private NewProductActivity newProductActivity;

    protected static boolean buttonState;

    public final static String sendStop = "CS;";
    public final static String sendIMUValues = "CM,";
    public final static String sendJoystickValues = "CJ,";
    public final static String sendFollow = "CF,";

    public static SensorFusion mSensorFusion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket_drawer);

        activity = this;
        context = getApplicationContext();

        // Start Bluetooth service
        startService(new Intent(this, Bluetooth.class));

        // get sensorManager and initialize sensor listeners
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorFusion = new SensorFusion(getApplicationContext(), mSensorManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.drawer_layout) != null) {
            imuFragment = new ImuFragment();
            imuFragment.setArguments(getIntent().getExtras());

            joystickFragment = new JoystickFragment();
            joystickFragment.setArguments(getIntent().getExtras());

            followFragment = new FollowFragment();
            followFragment.setArguments(getIntent().getExtras());

            newProductActivity = new NewProductActivity();
            newProductActivity.setArguments(getIntent().getExtras());
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        BasketDrawer.buttonState = false;

        // set up the hamburger icon to open and close the drawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // listen for navigation events
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        bindService(new Intent(this, Bluetooth.class), blueConnection, Context.BIND_AUTO_CREATE);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.basket_drawer, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int itemId = item.getItemId();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (itemId == R.id.nav_motion) {
            transaction.replace(R.id.basketDrawerFrame, imuFragment);
        } else if (itemId == R.id.nav_joystick) {
            transaction.replace(R.id.basketDrawerFrame, joystickFragment);
        } else if (itemId == R.id.nav_follow) {
            transaction.replace(R.id.basketDrawerFrame, followFragment);
        } else if (itemId == R.id.nav_shopping) {
            transaction.replace(R.id.basketDrawerFrame, newProductActivity);
        } else if (itemId == R.id.nav_settings) {
            Intent settings;
            settings = new Intent(this, Settings_Activity.class);
            startActivity(settings);
        }
        transaction.commit();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // Setup connection with Bluetooth service
    protected static ServiceConnection blueConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Bluetooth.BlueBinder b = (Bluetooth.BlueBinder) binder;
            bluetoothService = b.getBluetooth();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    };

}
