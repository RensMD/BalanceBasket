package com.example.group21.balancebasket;

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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class BasketDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ImuFragment.OnFragmentInteractionListener, JoystickFragment.OnFragmentInteractionListener, FollowFragment.OnFragmentInteractionListener, ConnectscreenFragment.OnFragmentInteractionListener, ShoppingListFragment.OnFragmentInteractionListener {
    private static final String TAG = "BasketDrawer";
    public static final boolean D = BuildConfig.DEBUG; // This is automatically set when building
    private static final String NAV_ITEM_ID = "navItemId";

    private static Activity activity;
    private static Context context;

    public static boolean joystickReleased = true;
    public static boolean follow =false;

    public static Bluetooth bluetoothService;

    private ImuFragment imuFragment;
    private JoystickFragment joystickFragment;
    private ConnectscreenFragment connectscreenFragment;
    private FollowFragment followFragment;
    private ShoppingListFragment shoppinglistFragment;

    protected static boolean buttonState;

    public final static String sendStop = "CS;";
    public final static String sendIMUValues = "CM,";
    public final static String sendJoystickValues = "CJ,";
    public final static String sendFollow = "CF,";

    public static SensorFusion mSensorFusion = null;

    private static boolean isConnected = false;


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
        getSupportActionBar().setTitle(null);

        if (findViewById(R.id.drawer_layout) != null) {
            imuFragment = new ImuFragment();
            imuFragment.setArguments(getIntent().getExtras());

            joystickFragment = new JoystickFragment();
            joystickFragment.setArguments(getIntent().getExtras());

            followFragment = new FollowFragment();
            followFragment.setArguments(getIntent().getExtras());

            connectscreenFragment = new ConnectscreenFragment();
            connectscreenFragment.setArguments(getIntent().getExtras());

            shoppinglistFragment = new ShoppingListFragment();
            shoppinglistFragment.setArguments(getIntent().getExtras());
        }

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

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.basketDrawerFrame, connectscreenFragment);
        transaction.commit();
        bindConnection();
        isIOIOConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
        isIOIOConnected();
        bindConnection();
    }

    private void bindConnection() {
         bindService(new Intent(this, Bluetooth.class), blueConnection, Context.BIND_AUTO_CREATE);
    }

    public static boolean isIOIOConnected() {
        if(bluetoothService != null) {
            if(bluetoothService.getState() == 2){
                isConnected = true;
            }
        }
        return isConnected;
    }

    @Override
    public void onBackPressed() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            transaction.replace(R.id.basketDrawerFrame, connectscreenFragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_follow) {

            if(!follow){
                item.setIcon(R.drawable.ic_follow_on);
                follow=true;
                transaction.add(R.id.basketDrawerFrame, followFragment);
            } else {
                item.setIcon(R.drawable.ic_portable_wifi_off_24dp);
                follow=false;
                transaction.remove(followFragment);
            }
            transaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
            transaction.replace(R.id.basketDrawerFrame, shoppinglistFragment);
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

//     Setup connection with Bluetooth service
    protected static ServiceConnection blueConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Bluetooth.BlueBinder b = (Bluetooth.BlueBinder) binder;
            bluetoothService = b.getBluetooth();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            bluetoothService = null;
        }
    };
}