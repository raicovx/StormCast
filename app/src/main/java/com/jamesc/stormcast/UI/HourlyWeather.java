package com.jamesc.stormcast.UI;

import android.Manifest;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.jamesc.stormcast.R;
import com.jamesc.stormcast.UI.Navigation.sideNavAdapter;
import com.jamesc.stormcast.UI.Navigation.sideNavItem;
import com.jamesc.stormcast.Weather.Current;
import com.jamesc.stormcast.Weather.Forecast;
import com.jamesc.stormcast.Weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by James on 5/02/2016.
 */
public class HourlyWeather extends AppCompatActivity implements LocationListener {
    //Toolbar
    private Toolbar actionBar;
    private String mActivityTitle;
    private TextView mActivityTitleTextView;
    private OkHttpClient client;
    private Request request;
    private String forecastURL;
    private double longitude;
    private double latitude;
    private String jsonData;
    private String temperatureFormatPref;
    private String windSpeedFormatPref;
    private String distanceFormatPref;
    private String autoSyncIntervalPref;
    private Intent settingsIntent;
    private Intent currentWeatherIntent;
    private Intent prevIntent;
    private Bundle extras;
    private static Context mContext;
    // Content
    //FAB
    private FloatingActionButton fabHourlyRefresh;

    //// Cards
    FrameLayout hourlyWeatherCard;

    //// Progress Bar
    ProgressBar hourlyActivityProgressBar;
    Drawable progressDrawable;
    private DrawerLayout mDrawerLayout;
    private ListView mNavList;
    private RecyclerView hourlyList;
    private ActionBarDrawerToggle mDrawerToggle;
    String [] appPages = {"Current Weather","Hourly Forecast","Weekly Forecast", "Settings"};
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private boolean clicked;
    private boolean ableToSync;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 124;
    private LocationRequest mLocationRequest;
    private boolean firstLoad;
    private String forecastAPIKey;
    public AnimatorSet leftIn;
    public AnimatorSet rightIn;
    public AnimatorSet leftOut;
    public AnimatorSet rightOut;
    private LinearLayoutManager hourlyLayoutManager;
    private HourlyItemsRecyclerViewAdapter adapter;
    private Hour[] mHours;
    private Forecast mForecast;
    private boolean isExistingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourly_weather_activity_layout);
        mContext = this;
        //Checking Permissions
        checkLocationPermissions();

        //status variables
        firstLoad = true;
        forecastAPIKey = "f2e1d661e28dbdd28934a784f1b456a1";

        //Intents and Extra Bundle Data
        settingsIntent = new Intent(this, SettingsActivity.class);
        currentWeatherIntent = new Intent(this, MainActivity.class);

            //get existing hourly data
            //Make sure it exists and store, otherwise activate api call via status variable
            retrieveExistingData();

        //Cards
        hourlyWeatherCard = (CardView)findViewById(R.id.hourly_weather_card_view);

        //Content
        hourlyList = (RecyclerView)findViewById(R.id.hourly_recycler_view);
        createHourlyRecyclerView();

        //User Preference Declarations
        windSpeedFormatPref = getWindSpeedUnitPreferences(windSpeedFormatPref);
        temperatureFormatPref = getTemperatureFormatPreferences(temperatureFormatPref);
        distanceFormatPref = getDistanceFormatPreferences(distanceFormatPref);
        autoSyncIntervalPref = getAutoSyncIntervalPreferences(autoSyncIntervalPref);
        //User Preference Declarations End

        //OnCreate Functions
        createSideNavItems();
        createSideNav();



        //Progress Bar
        createProgressBar();

        // Tool Bar
        createActionBar();


        makeCurrentUiDisappear();
        //Start Google play Location Services if there is no existing data
        //Otherwise, use the existing data
        if(!isExistingData) {
            startGmsLocationServices();
        }else{
            prepareUiData();
            makeCurrentUiReappear();
        }
        fabHourlyRefresh = (FloatingActionButton) findViewById(R.id.refresh_hourly_fab);
        fabHourlyRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = true;
                ableToSync = true;
                makeCurrentUiDisappear();
                hourlyActivityProgressBar.setVisibility(View.VISIBLE);
                if(mGoogleApiClient != null) {
                    locationUpdate();
                }else{
                startGmsLocationServices();
                    locationUpdate();
                }

            }
        });

        if(firstLoad){
            ableToSync = true;
            if(!isExistingData) {
                if(mGoogleApiClient != null) {
                    locationUpdate();
                }else{
                    startGmsLocationServices();
                    locationUpdate();
                }
            }
        }
    }
    public static Context getContext(){
        return mContext;
    }
    private void retrieveExistingData() {
        prevIntent = getIntent();
        extras = prevIntent.getExtras();
        if(extras != null) {
            if(extras.containsKey(MainActivity.HOURLY_DATA)){
                Parcelable[] parcelables = prevIntent.getParcelableArrayExtra(MainActivity.HOURLY_DATA);
                mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);
                isExistingData = true;
            } else {
                isExistingData = false;
            }
        }
    }

    private void createHourlyRecyclerView() {
        hourlyLayoutManager = new LinearLayoutManager(this);
        hourlyList.setLayoutManager(hourlyLayoutManager);
        hourlyList.setHasFixedSize(true);
        hourlyList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }else if(id == R.id.action_settings){
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }
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
    private void refreshCurrentWeatherData() {
        if (isNetworkAvailable()) {
            if (clicked || firstLoad || ableToSync) {
                if (forecastURL != null) {
                    createConnectionAndCall();
                }
            }
        }
        clicked = false;
        if (firstLoad) {
            firstLoad = false;
        }

    }

    private void makeCurrentUiDisappear() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hourlyActivityProgressBar.setVisibility(View.VISIBLE);
                hourlyList.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void makeCurrentUiReappear(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation animationZero = new AlphaAnimation(1, 0);
                animationZero.setDuration(500);
                animationZero.setStartOffset(0);
                hourlyActivityProgressBar.startAnimation(animationZero);
                hourlyActivityProgressBar.setVisibility(View.INVISIBLE);
                hourlyList.setVisibility(View.VISIBLE);
            }
        });
    }
    private void createProgressBar() {
        progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_drawable);
        hourlyActivityProgressBar = (ProgressBar)findViewById(R.id.hourly_activity_progress_bar);
        hourlyActivityProgressBar.setProgressDrawable(progressDrawable);
    }
    private void createActionBar() {
        actionBar = (Toolbar) findViewById(R.id.hourly_weather_toolbar);
        mActivityTitle = getTitle().toString();
        mActivityTitleTextView = (TextView) findViewById(R.id.hourly_activity_title);
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
                    Log.e("StormCast", "Could not set Support ToolBar: " + e);
                    alertUserAboutError();
                }
            }
        }
    }

    private void displayCustomActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
    }
    //DrawerLayout
    private void createSideNavItems() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.hourly_drawer_layout);
        mNavList = (ListView) findViewById(R.id.hourly_nav_list);
        ArrayList<sideNavItem> sideNavItemArrayList = sideNavItem.fromStringArray(appPages);
        // mAdapter = new ArrayAdapter<String>(this, R.layout.custom_list_item_1, appPages);
        ArrayAdapter<sideNavItem> adapter = new sideNavAdapter(this, sideNavItemArrayList);
        mNavList.setAdapter(adapter);
        mNavList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
    }
    private void selectItem(int position) {
        mNavList.setItemChecked(position, true);
        mActivityTitleTextView.setText(appPages[position]);
        switch(position){
            case 0:
                startActivity(currentWeatherIntent);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                startActivity(settingsIntent);

                break;

        }

    }
    private void createSideNav() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerClosed(drawerView);
                mActivityTitleTextView.setText(" ");
                invalidateOptionsMenu();
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             */

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
    //Misc. UI
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    // Data Model
   /* private HourlyWeatherModel getHourlyWeatherData(String jsonData) throws JSONException{
        JSONObject mHourlyData = new JSONObject(jsonData);
        JSONObject mHourlyObject = mHourlyData.getJSONObject("hourly");
        JSONArray mHourlyArray = mHourlyObject.getJSONArray("data");
        ArrayList<HourlyItemsRecyclerViewAdapter.HourlyItemsViewHolder> mHourlyWeather = new ArrayList<HourlyItemsRecyclerViewAdapter.HourlyItemsViewHolder>();
        int i;

        for(i=0; i < mHourlyArray.length(); i++){
            JSONObject h = mHourlyArray.getJSONObject(i);
            Long time = h.getLong("time");
            String summary = h.getString("summary");
            String icon = h.getString("icon");
            Double temperature = h.getDouble("temperature");
            Double apparentTemperature = h.getDouble("apparentTemperature");
           // mHourlyWeather.add(new HourlyWeatherModel(time, summary, icon, temperature, apparentTemperature));

        }
        prepareUiData();
        makeCurrentUiReappear();

        return mHourlyWeather;
    }*/

    //API Call
    private void createConnectionAndCall() {
        client = new OkHttpClient();
        request = new Request.Builder()
                .url(forecastURL)
                .build();
        apiCall();
    }
    private void apiCall() {
        Call call = client.newCall(request);
        Log.v("StormCast", "API Call made");
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
                        mForecast = parseForecastDetails(jsonData);
                        mHours = mForecast.getHourlyForecast();
                        prepareUiData();
                        makeCurrentUiReappear();
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

    //UI

            private void prepareUiData() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new HourlyItemsRecyclerViewAdapter(mHours);
                        hourlyList.setAdapter(adapter);
                    }
                });


            }
    //Location Services

        //Permissions
            private void checkLocationPermissions() {
                int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }


        //Requests
            private void locationUpdate() {
                int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                } else {
                    if(ableToSync) {
                        mLocationRequest = createLocationRequest();
                        startLocationUpdates();

                    }
                }
            }
            public LocationRequest createLocationRequest() {
                mLocationRequest = new LocationRequest();
                if (autoSyncIntervalPref.equals("1")) {
                    mLocationRequest.setInterval(180000);
                } else if (autoSyncIntervalPref.equals("2")) {
                    mLocationRequest.setInterval(3600000);
                } else if (autoSyncIntervalPref.equals("3")) {
                    mLocationRequest.setInterval(7200000);
                }else if(autoSyncIntervalPref.equals("4")){
                    mLocationRequest.setInterval(14400000);
                }else if(autoSyncIntervalPref.equals("5")){
                    mLocationRequest.setInterval(21600000);
                }else{
                    mLocationRequest.setInterval(2000000000);
                }
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                return mLocationRequest;
            }
            protected void startLocationUpdates() throws SecurityException {
                    if (mGoogleApiClient.isConnected()) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    } else {
                        mGoogleApiClient.connect();
                        if (mGoogleApiClient.isConnected()) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                        }
                    }
            }


            public void stopLocationUpdates() {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            }
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                toTwoDecimalPlaces(longitude, latitude);
                forecastURL = "https://api.forecast.io/forecast/" + forecastAPIKey + "/" + latitude + "," + longitude + "?exclude=['currently','minutely','daily','alerts','flags']";
                if(ableToSync){
                    refreshCurrentWeatherData();
                    ableToSync = false;
                }
            }

            private void toTwoDecimalPlaces(double longitude, double latitude) {
                String tempLatitude = String.format("%.2f", latitude);
                String tempLongitude = String.format("%.2f", longitude);
                latitude = Double.valueOf(tempLatitude);
                longitude = Double.valueOf(tempLongitude);
            }
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                switch (requestCode) {
                    case REQUEST_CODE_ASK_PERMISSIONS:
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            // Permission Granted
                            if(clicked || firstLoad || ableToSync) {
                                locationUpdate();
                            }
                        } else {
                            // Permission Denied
                            Toast.makeText(HourlyWeather.this, "Location Permission Denied", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        break;
                    default:
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
            private void startGmsLocationServices(){
                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                                        @Override
                                                        public void onConnected(Bundle bundle) {

                                                            if (mLastLocation == null || clicked || mGoogleApiClient != null) {
                                                                locationUpdate();

                                                            }
                                                        }
                                                        @Override
                                                        public void onConnectionSuspended ( int i){
                                                            Toast.makeText(getApplicationContext(), "Connection to Google Play Services Suspended", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                            )
                            .

                                    addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                                                                      @Override
                                                                      public void onConnectionFailed (ConnectionResult connectionResult){
                                                                          Toast.makeText(getApplicationContext(), "Connection to Google Play Services Failed", Toast.LENGTH_SHORT).show();
                                                                      }
                                                                  }

                                    ).addApi(LocationServices.API).build();
                }
            }

            protected void onStart() {
                if(mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                    Log.v("StormCast", "mGoogleApiClient connect function running");
                }
                super.onStart();
            }
            protected void onPause(){
                super.onPause();
                if(mGoogleApiClient != null){
                    if(mGoogleApiClient.isConnected()) {
                        stopLocationUpdates();
                    }
                }
            }
            protected void onStop() {
                if(mGoogleApiClient != null) {
                    mGoogleApiClient.disconnect();

                }
                super.onStop();
            }


    //Weather Data

            private Forecast parseForecastDetails(String jsonData) throws JSONException {
                Forecast forecast = new Forecast();
                // forecast.setCurrent(getCurrentDetails(jsonData));
                //forecast.setDailyForecast(getDailyForecast(jsonData));
                forecast.setHourlyForecast(getHourlyForecast(jsonData));
                return forecast;
            }

            private Hour[] getHourlyForecast(String jsonData) throws JSONException {
                JSONObject forecast = new JSONObject(jsonData);
                String timezone = forecast.getString("timezone");
                JSONObject hourly = forecast.getJSONObject("hourly");
                JSONArray data = hourly.getJSONArray("data");
                Hour[] hours = new Hour[data.length()];
                int i;

                for(i=0; i < data.length(); i++){
                    JSONObject h = data.getJSONObject(i);
                    Long time = h.getLong("time");
                    String summary = h.getString("summary");
                    String icon = h.getString("icon");
                    Double temperature = h.getDouble("temperature");
                    Double apparentTemperature = h.getDouble("apparentTemperature");
                    hours[i] = new Hour(time, timezone, summary, icon, temperature, apparentTemperature);
                }
                return hours;
            }
            /*
            private Day[] getDailyForecast(String jsonData) throws JSONException {
                JSONObject forecast = new JSONObject(jsonData);
                String timezone = forecast.getString("timezone");
                JSONObject daily = forecast.getJSONObject("daily");
                JSONArray data = forecast.getJSONArray("data");
                Day[] days = new Day[data.length()];
                int i;
                for(i=0; i < data.length(); i++){
                    JSONObject h = data.getJSONObject(i);
                    Long time = h.getLong("time");
                    String summary = h.getString("summary");
                    String icon = h.getString("icon");
                    Double temperature = h.getDouble("temperature");
                    Double apparentTemperature = h.getDouble("apparentTemperature");
                    days[i] = new Day(time, timezone, summary, icon, temperature, apparentTemperature);
                }
                return days;

            }*/


            private Current getCurrentDetails(String jsonData) throws JSONException {
                JSONObject forecast = new JSONObject(jsonData);
                String timezone = forecast.getString("timezone");
                JSONObject currently = forecast.getJSONObject("currently");
                Current current = new Current();
                current.setHumidity(currently.getDouble("humidity"));
                current.setTime(currently.getLong("time"));
                current.setIcon(currently.getString("icon"));
                current.setPrecipChance(currently.getDouble("precipProbability"));
                current.setSummary(currently.getString("summary"));
                current.setTemperature(currently.getDouble("temperature"));
                current.setTimeZone(timezone);

                return current;

            }



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
}
