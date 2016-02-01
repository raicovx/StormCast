package com.jamesc.stormcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private CurrentWeather mCurrentWeather;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private String forecastAPIKey = "f2e1d661e28dbdd28934a784f1b456a1";
    private String forecastURL;
    private String jsonData;

    //Action Bar + Side Drawer + UI Declarations

    private FloatingActionButton fabRefresh;
    ProgressBar refreshProgressBar;
    Drawable progressDrawable;
    //Relative Layout
    RelativeLayout currentWeatherLayout;
    //Compatibility Actionbar
    Toolbar actionBar;
    //Drawer Layout Declarations
    private ListView mNavList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private TextView mActivityTitleTextView;


    //Location Declarations

    private LocationManager locationManager;
    private double longitude;
    private double latitude;
    private String locationText;
    private TextView locationTextView;

    //Weather Icon Declarations
    private int iconImageId;
    private ImageView iconImageView;


    //Temperature Declarations
    private double temperatureText;
    private TextView temperatureTextView;

    //Summary Declarations
    private String summaryText;
    private TextView summaryTextView;

    //Time Declarations
    private String timeText;
    private TextView timeTextView;

    //Degrees Images Declartaions
    private ImageView degreeImageView;

    //Status Variables
    private boolean clicked = false;
    private boolean firstLoad = true;
    private boolean ableToSync;
    private TextView autoSyncOffNotification;

    // Settings and User Preference Data Type Declarations
    Intent settingsIntent;
    String windSpeedFormatPref;
    String temperatureFormatPref;
    String distanceFormatPref;
    String autoSyncIntervalPref;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;
    private Object unitPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSideNavItems();
        createSideNav();
        startLocationManager();
        //Progress Bar
        createProgressBar();

        // Tool Bar
        createActionBar();
        //Tool Bar End

        //Layout
        currentWeatherLayout = (RelativeLayout) findViewById(R.id.currentWeatherLayout);
        //Layout End

        //Text Views for Current Information
        autoSyncOffNotification = (TextView)findViewById(R.id.autoSyncOffWarning);
        timeTextView  = (TextView)findViewById(R.id.timeTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        summaryTextView = (TextView) findViewById(R.id.summaryTextView);
        iconImageView = (ImageView) findViewById(R.id.iconImageView);
        degreeImageView = (ImageView) findViewById(R.id.degreesImageView);
        settingsIntent = new Intent(this, SettingsActivity.class);

        //User Preference Declarations
        windSpeedFormatPref = getWindSpeedUnitPreferences(windSpeedFormatPref);
        temperatureFormatPref = getTemperatureFormatPreferences(temperatureFormatPref);
        distanceFormatPref = getDistanceFormatPreferences(distanceFormatPref);
        autoSyncIntervalPref = getAutoSyncIntervalPreferences(autoSyncIntervalPref);
        //User Preference Declarations End

        //Auto Sync settings
        if(!autoSyncIntervalPref.equals("0") || clicked) {// If the preference is not to Never Auto Refresh
                ableToSync = true;
                autoSyncOffNotification.setVisibility(View.GONE);
                refreshProgressBar.setVisibility(View.VISIBLE);
                locationUpdate();
            if (autoSyncIntervalPref.equals("1")) {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        ableToSync = true;
                        refreshProgressBar.setVisibility(View.VISIBLE);
                        locationUpdate();
                    }
                }, 0, 1800000);
            } else if (autoSyncIntervalPref.equals("2")) {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        ableToSync = true;
                        refreshProgressBar.setVisibility(View.VISIBLE);
                        locationUpdate();
                    }
                }, 0, 3600000);
            } else if (autoSyncIntervalPref.equals("3")) {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        ableToSync = true;
                        refreshProgressBar.setVisibility(View.VISIBLE);
                        locationUpdate();
                    }
                }, 0, 7200000);
            }else if(autoSyncIntervalPref.equals("4")){
                new Timer().scheduleAtFixedRate(new TimerTask(){
                    @Override
                    public void run(){
                        ableToSync = true;
                        refreshProgressBar.setVisibility(View.VISIBLE);
                        locationUpdate();
                    }
                },0,14400000);
            }else if(autoSyncIntervalPref.equals("5")){
                new Timer().scheduleAtFixedRate(new TimerTask(){
                    @Override
                    public void run(){
                        ableToSync = true;
                        refreshProgressBar.setVisibility(View.VISIBLE);
                        locationUpdate();
                    }
                },0,21600000);
            }
        }
        if(autoSyncIntervalPref.equals("0")){
            ableToSync = false;
            refreshProgressBar.setVisibility(View.INVISIBLE);
            autoSyncOffNotification.setVisibility(View.VISIBLE);
        }
        Log.v("Stormy", autoSyncIntervalPref);
        //Auto Sync Settings End


        //Current Weather Activity's Floating Action Button
        fabRefresh = (FloatingActionButton) findViewById(R.id.refreshFab);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = true;
                locationUpdate();
                makeCurrentUiDisappear();
                autoSyncOffNotification.setVisibility(View.GONE);
                refreshProgressBar.setVisibility(View.VISIBLE);

            }
        });

        //Current Weather Activity's Floating Action Button End
        makeCurrentUiDisappear();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_settings){
            startActivity(settingsIntent);
        }else if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }


    private void createActionBar() {
        actionBar = (Toolbar) findViewById(R.id.current_weather_toolbar);
        mActivityTitle = getTitle().toString();
        mActivityTitleTextView = (TextView) findViewById(R.id.mActivityTitle);
        if(actionBar != null) {
            int actionBarColour = ContextCompat.getColor(getApplicationContext(), R.color.actionBarColorPrimary);
            actionBar.setBackgroundColor(actionBarColour);
            actionBar.inflateMenu(R.menu.menu_main);
            setSupportActionBar(actionBar);
            mActivityTitleTextView.setText(mActivityTitle);
            if(getSupportActionBar() != null) {
                displayCustomActionBar();
            }else{
                try{
                    setSupportActionBar(actionBar);
                    if(getSupportActionBar()!=null){
                        displayCustomActionBar();
                    }
                }catch(Exception e){
                    Log.e("StormCast", "Could not set Support ToolBar: "+e);
                    alertUserAboutError();
                }
            }
        }
    }

    private void displayCustomActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void createSideNavItems() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavList = (ListView) findViewById(R.id.navList);
        String [] appPages = {"Current Weather","Hourly Forecast","Weekly Forecast"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appPages);
        mNavList.setAdapter(mAdapter);
        mNavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createSideNav(){
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerClosed(drawerView);
                mActivityTitleTextView.setText(" ");
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mActivityTitle = getTitle().toString();
                mActivityTitleTextView.setText(mActivityTitle);
                invalidateOptionsMenu();
            }

        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }
    private void createProgressBar() {
        progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_drawable);
        refreshProgressBar = (ProgressBar) findViewById(R.id.refreshProgress);
        refreshProgressBar.setProgressDrawable(progressDrawable);
    }


    // UI Animations
    private void makeCurrentUiDisappear() {
        temperatureTextView.setVisibility(View.INVISIBLE);
        locationTextView.setVisibility(View.INVISIBLE);
        summaryTextView.setVisibility(View.INVISIBLE);
        iconImageView.setVisibility(View.INVISIBLE);
        degreeImageView.setVisibility(View.INVISIBLE);
        timeTextView.setVisibility(View.INVISIBLE);
    }
    private void makeCurrentUiReappear() {
        refreshProgressBar.setVisibility(View.INVISIBLE);
        autoSyncOffNotification.setVisibility(View.GONE);
        Animation animationOne = new AlphaAnimation(0, 1);
        animationOne.setInterpolator(new DecelerateInterpolator()); //add this
        animationOne.setDuration(1000);
        animationOne.setStartOffset(1000);
        temperatureTextView.setVisibility(View.VISIBLE);
        temperatureTextView.startAnimation(animationOne);


        Animation animationTwo = new AlphaAnimation(0, 1);
        animationTwo.setInterpolator(new DecelerateInterpolator()); //add this
        animationTwo.setDuration(1000);
        animationTwo.setStartOffset(1000);
        animationTwo.setStartOffset(100);
        degreeImageView.startAnimation(animationTwo);
        degreeImageView.setVisibility(View.VISIBLE);

        Animation animationThree = new AlphaAnimation(0, 1);
        animationThree.setInterpolator(new DecelerateInterpolator()); //add this
        animationThree.setDuration(1000);
        animationThree.setStartOffset(1000);
        animationThree.setStartOffset(500);

        timeTextView.startAnimation(animationThree);
        timeTextView.setVisibility(View.VISIBLE);

        Animation animationFour = new AlphaAnimation(0, 1);
        animationFour.setInterpolator(new DecelerateInterpolator()); //add this
        animationFour.setDuration(1000);
        animationFour.setStartOffset(1000);

        locationTextView.setVisibility(View.VISIBLE);
        locationTextView.startAnimation(animationFour);

        Animation animationFive = new AlphaAnimation(0, 1);
        animationFive.setInterpolator(new DecelerateInterpolator()); //add this
        animationFive.setDuration(1000);
        animationFive.setStartOffset(1000);
        summaryTextView.setVisibility(View.VISIBLE);
        summaryTextView.startAnimation(animationFive);
        iconImageView.setVisibility(View.VISIBLE);
        iconImageView.startAnimation(animationFive);
        fabRefresh.setVisibility(View.VISIBLE);
        //iconImageView.startAnimation(animationFive);


    }

    // UI Animations End

    //Weather Data
    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        return currentWeather;

    }

    private void refreshCurrentWeatherData() {
        if (isNetworkAvailable()) {
            if (clicked || firstLoad) {
                    if (forecastURL != null) {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(forecastURL)
                                .build();
                        Call call = client.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                alertUserAboutError();
                            }


                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {

                                    if (response.isSuccessful()) {
                                        jsonData = response.body().string();
                                        mCurrentWeather = getCurrentDetails(jsonData);
                                        //User Preference Do-Stuff Begin (Needs to start after mCurrentWeather is Initialized) -- Positioned in Response Block to Achieve this
                                        if (temperatureFormatPref.equals("0")) {
                                            temperatureText = mCurrentWeather.getTemperature();
                                            temperatureText = mCurrentWeather.convertFahrenheitToCelsius(temperatureText);

                                        } else if (temperatureFormatPref.equals("1")) {
                                            temperatureText = mCurrentWeather.getTemperature();
                                        }
                                        //User Preference Do-Stuff End
                                        locationText = mCurrentWeather.getTimeZone();
                                        summaryText = mCurrentWeather.getSummary();
                                        iconImageId = mCurrentWeather.getIconId();
                                        Log.v("Stormy", forecastURL);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                timeTextView.setText("Refreshed at: " + mCurrentWeather.getFormattedTime());
                                                temperatureTextView.setText(String.format("%.1f", temperatureText));
                                                locationTextView.setText(locationText);
                                                summaryTextView.setText(summaryText);
                                                iconImageView.setImageResource(iconImageId);

                                                if (firstLoad) {
                                                    firstLoad = false;
                                                }
                                                makeCurrentUiReappear();
                                                clicked = false;

                                            }
                                        });

                                    } else {
                                        alertUserAboutError();

                                    }
                                } catch (IOException e) {
                                    Log.e("Stormy - Error", "IO Exception Caught:", e);
                                } catch (JSONException e) {
                                    Log.e("Stormy - Error", "JSON Exception Caught:", e);
                                }


                            }
                        });
                    }
            }
        }
    }
    //Weather Data End

    //Networking
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else {
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    //Networking End

    //Error Management & Dialogs
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }


    //Error Management & Dialogs End

    //Location Services
    private void startLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        toTwoDecimalPlaces(longitude, latitude);
        forecastURL = "https://api.forecast.io/forecast/" + forecastAPIKey + "/" + latitude + "," + longitude+"?exclude=['minutely','hourly','daily','alerts','flags']";
        if(clicked || ableToSync) {
            refreshCurrentWeatherData();
        }
    }

    private void toTwoDecimalPlaces(double longitude, double latitude) {
        String tempLatitude = String.format("%.2f",latitude);
        String tempLongitude = String.format("%.2f", longitude);
        latitude = Double.valueOf(tempLatitude);
        longitude = Double.valueOf(tempLongitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void locationUpdate() {
        int hasLocationPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    if(!autoSyncIntervalPref.equals("0") || clicked) {
                        locationUpdate();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //Location Services End

    // User Preference functions
    private SharedPreferences initializeSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this);

    }

    public String getWindSpeedUnitPreferences(String windSpeedFormatPref) {
        SharedPreferences sharedPref = initializeSharedPrefs();
        windSpeedFormatPref = sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_WIND_SPEED_UNITS,"0");
        return windSpeedFormatPref;
    }

    public String getTemperatureFormatPreferences(String temperatureFormatPref){
        SharedPreferences sharedPref = initializeSharedPrefs();
        temperatureFormatPref = sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_TEMPERATURE_UNITS, "0");
        return temperatureFormatPref;
    }

    public String getDistanceFormatPreferences(String distanceFormatPref){
        SharedPreferences sharedPref = initializeSharedPrefs();
        distanceFormatPref = sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_DISTANCE_UNITS, "0");
        return distanceFormatPref;
    }
    public String getAutoSyncIntervalPreferences(String autoSyncIntervalPref) {
        SharedPreferences sharedPref = initializeSharedPrefs();
        autoSyncIntervalPref = sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_AUTO_SYNC_INTERVAL, "0");
        return autoSyncIntervalPref;
    }


    // User Preferences End
};
