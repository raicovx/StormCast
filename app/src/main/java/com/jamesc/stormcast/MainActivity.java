package com.jamesc.stormcast;

        import android.Manifest;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.drawable.Drawable;
        import android.location.Location;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v4.content.ContextCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.CardView;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.animation.AlphaAnimation;
        import android.view.animation.Animation;
        import android.view.animation.DecelerateInterpolator;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.appindexing.AppIndex;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Timer;
        import java.util.TimerTask;

        import okhttp3.Call;
        import okhttp3.Callback;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.Response;

        public class MainActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener {

            private CurrentWeather mCurrentWeather;
            final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
            private OkHttpClient client;
            private Request request;
            private String forecastAPIKey = "f2e1d661e28dbdd28934a784f1b456a1";
            private String forecastURL;
            private String jsonData;

            //Action Bar + Side Drawer + UI Declarations

            private FloatingActionButton fabRefresh;
            private CardView temperatureCardView;
            private CardView autoSyncOffNotification;
            private CardView summaryCardView;
            ProgressBar refreshProgressBar;
            Drawable progressDrawable;

            //Relative Layout
            //RelativeLayout currentWeatherLayout;
            //Compatibility Actionbar
            Toolbar actionBar;

            //Drawer Layout Declarations
            private ListView mNavList;
            private ActionBarDrawerToggle mDrawerToggle;
            private DrawerLayout mDrawerLayout;
            private String mActivityTitle;
            private TextView mActivityTitleTextView;


            //Location Declarations
            private GoogleApiClient mGoogleApiClient;
            LocationRequest mLocationRequest;
            private boolean islocationPermissionGranted;
            private double longitude;
            private double latitude;
            private Location mLastLocation;
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

            //Humidity Declarations
            private ImageView humidityIv;
            private TextView humidityTv;
            private String humidityText;
            private CardView humidityCv;

            //Status Variables
            private boolean clicked = false;
            private boolean firstLoad = true;
            private boolean ableToSync;
            private TextView autoSyncOffTextView;

            // Settings and User Preference Data Type Declarations
            Intent settingsIntent;
            Intent hourlyWeatherIntent;
            String windSpeedFormatPref;
            String temperatureFormatPref;
            String distanceFormatPref;
            String autoSyncIntervalPref;

            //Member Variables for SideNav
            String mTitle;
            String [] appPages = {"Current Weather","Hourly Forecast","Weekly Forecast", "Settings"};
            TextView drawerItemSetOneTv;
            /**
             * ATTENTION: This was auto-generated to implement the App Indexing API.
             * See https://g.co/AppIndexing/AndroidStudio for more information.
             */
            private GoogleApiClient mClient;
            private TextView windspeedTv;
            private CardView windspeedCv;


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                settingsIntent = new Intent(this, SettingsActivity.class);
                hourlyWeatherIntent = new Intent(this, HourlyWeather.class);
                checkLocationPermissions();
                temperatureCardView = (CardView) findViewById(R.id.temperature_card_view);

                createSideNavItems();
                createSideNav();

                startGmsLocationServices();

                //startLocationManager();
                //Progress Bar
                createProgressBar();

                // Tool Bar
                createActionBar();
                //Tool Bar End

                //Layout
                //currentWeatherLayout = (RelativeLayout) findViewById(R.id.currentWeatherLayout);
                //Layout End

                //Text Views for Current Information
                autoSyncOffNotification = (CardView)findViewById(R.id.auto_sync_off_warning_card);
                autoSyncOffTextView = (TextView) findViewById(R.id.auto_sync_off_warning_textview);
                summaryCardView = (CardView) findViewById(R.id.summary_card_view);
                timeTextView  = (TextView)findViewById(R.id.timeTextView);
                temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
                locationTextView = (TextView) findViewById(R.id.locationTextView);
                summaryTextView = (TextView) findViewById(R.id.summaryTextView);
                iconImageView = (ImageView) findViewById(R.id.iconImageView);
                degreeImageView = (ImageView) findViewById(R.id.degreesImageView);
                humidityIv = (ImageView) findViewById(R.id.humidity_icon);
                humidityIv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_humidity_icon));
                humidityTv = (TextView) findViewById(R.id.humidity_tv);
                humidityCv = (CardView) findViewById(R.id.humidity_card);
                windspeedTv = (TextView) findViewById(R.id.windspeed_tv);
                windspeedCv = (CardView) findViewById(R.id.windspeed_card);


                //User Preference Declarations
                windSpeedFormatPref = getWindSpeedUnitPreferences();
                temperatureFormatPref = getTemperatureFormatPreferences();
                distanceFormatPref = getDistanceFormatPreferences();
                autoSyncIntervalPref = getAutoSyncIntervalPreferences();
                //User Preference Declarations End

                //Auto Sync settings
                if(!autoSyncIntervalPref.equals("0") || clicked) {// If the preference is not to Never Auto Refresh
                    ableToSync = true;
                    autoSyncOffNotification.setVisibility(View.GONE);
                    autoSyncOffTextView.setVisibility(View.GONE);
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
                    autoSyncOffTextView.setVisibility(View.VISIBLE);
                }


                //Auto Sync Settings End


                //Current Weather Activity's Floating Action Button
                fabRefresh = (FloatingActionButton) findViewById(R.id.refreshFab);
                fabRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clicked = true;
                        ableToSync = true;
                        makeCurrentUiDisappear();
                        autoSyncOffNotification.setVisibility(View.GONE);
                        autoSyncOffTextView.setVisibility(View.GONE);
                        refreshProgressBar.setVisibility(View.VISIBLE);
                        locationUpdate();

                    }
                });

                //Current Weather Activity's Floating Action Button End
                makeCurrentUiDisappear();
                // ATTENTION: This was auto-generated to implement the App Indexing API.
                // See https://g.co/AppIndexing/AndroidStudio for more information.
                mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
            }

            private void checkLocationPermissions() {
                int hasLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }

            protected void onStart() {
                if(mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }
                super.onStart();
            }
            protected void onPause(){
                super.onPause();
                if(mGoogleApiClient.isConnected()) {
                    stopLocationUpdates();
                }
            }
            protected void onStop() {
                if(mGoogleApiClient != null) {
                    mGoogleApiClient.disconnect();

                }
                super.onStop();
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
                        Log.v("StormCast", "Action bar was null..");
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
                actionBar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
            }

            private void createSideNavItems() {
                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mNavList = (ListView) findViewById(R.id.navList);
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
                        break;
                    case 1:
                        startActivity(hourlyWeatherIntent);
                        fabRefresh.setVisibility(View.GONE);
                        break;
                    case 2:
                        break;
                    case 3:
                        startActivity(settingsIntent);
                        fabRefresh.setVisibility(View.GONE);
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
        temperatureCardView.setVisibility(View.INVISIBLE);
        humidityTv.setVisibility(View.INVISIBLE);
        humidityIv.setVisibility(View.INVISIBLE);
        humidityCv.setVisibility(View.INVISIBLE);
        windspeedCv.setVisibility(View.INVISIBLE);
        windspeedTv.setVisibility(View.INVISIBLE);
        fabRefresh.setVisibility(View.INVISIBLE);
        summaryCardView.setVisibility(View.INVISIBLE);
    }
    private void makeCurrentUiReappear() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshProgressBar.setVisibility(View.INVISIBLE);
                autoSyncOffNotification.setVisibility(View.GONE);
                autoSyncOffTextView.setVisibility(View.GONE);
                Animation animationOne = new AlphaAnimation(0, 1);
                animationOne.setInterpolator(new DecelerateInterpolator()); //add this
                animationOne.setDuration(500);
                animationOne.setStartOffset(0);
                temperatureCardView.setVisibility(View.VISIBLE);
                temperatureCardView.startAnimation(animationOne);
                temperatureTextView.setVisibility(View.VISIBLE);
                temperatureTextView.startAnimation(animationOne);
                timeTextView.setVisibility(View.VISIBLE);
                timeTextView.startAnimation(animationOne);
                degreeImageView.setVisibility(View.VISIBLE);
                degreeImageView.startAnimation(animationOne);
                locationTextView.setVisibility(View.VISIBLE);
                locationTextView.startAnimation(animationOne);


                Animation animationTwo = new AlphaAnimation(0, 1);
                animationTwo.setInterpolator(new DecelerateInterpolator()); //add this
                animationTwo.setDuration(1000);
                animationTwo.setStartOffset(1100);
                summaryCardView.setVisibility(View.VISIBLE);
                summaryCardView.startAnimation(animationTwo);
                summaryTextView.setVisibility(View.VISIBLE);
                summaryTextView.startAnimation(animationTwo);
                iconImageView.setVisibility(View.VISIBLE);
                iconImageView.startAnimation(animationTwo);



                Animation animationThree = new AlphaAnimation(0, 1);
                animationThree.setInterpolator(new DecelerateInterpolator()); //add this
                animationThree.setDuration(1000);
                animationThree.setStartOffset(2200);
                humidityCv.setVisibility(View.VISIBLE);
                humidityCv.startAnimation(animationThree);
                humidityTv.setVisibility(View.VISIBLE);
                humidityTv.startAnimation(animationThree);
                humidityIv.setVisibility(View.VISIBLE);
                humidityIv.startAnimation(animationThree);

                Animation animationFour = new AlphaAnimation(0, 1);
                animationFour.setInterpolator(new DecelerateInterpolator()); //add this
                animationFour.setDuration(1000);
                animationFour.setStartOffset(2800);
                windspeedCv.setVisibility(View.VISIBLE);
                windspeedCv.startAnimation(animationFour);
                windspeedTv.setVisibility(View.VISIBLE);
                windspeedCv.startAnimation(animationFour);

                Animation animationFive = new AlphaAnimation(0, 1);
                animationFive.setInterpolator(new DecelerateInterpolator()); //add this
                animationFive.setDuration(1000);
                animationFive.setStartOffset(4400);
                fabRefresh.setVisibility(View.VISIBLE);
                //iconImageView.startAnimation(animationFive);
            }
        });

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

    private void createConnectionAndCall() {
        client = new OkHttpClient();
        request = new Request.Builder()
                .url(forecastURL)
                .build();
        apiCall();
    }

    private void apiCall() {
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
                        prepareUiData();
                        updateUiElements();
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

    private void updateUiElements() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                timeTextView.setText("Refreshed at: " + mCurrentWeather.getFormattedTime());
                temperatureTextView.setText(String.format("%.1f", temperatureText));
                humidityTv.setText(humidityText);
                locationTextView.setText(locationText);
                summaryTextView.setText(summaryText);
                iconImageView.setImageResource(iconImageId);
                summaryCardView.setCardBackgroundColor(mCurrentWeather.getSummaryCardColor(getApplicationContext()));



            }
        });
    }

    private void prepareUiData() {
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
        humidityText = ((mCurrentWeather.getHumidity()*100)+"%");
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


    private void toTwoDecimalPlaces(double longitude, double latitude) {
        String tempLatitude = String.format("%.2f", latitude);
        String tempLongitude = String.format("%.2f", longitude);
        latitude = Double.valueOf(tempLatitude);
        longitude = Double.valueOf(tempLongitude);
    }


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

    public String getWindSpeedUnitPreferences() {
        SharedPreferences sharedPref = initializeSharedPrefs();
        String windSpeedFormatPref = sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_WIND_SPEED_UNITS, "0");
        return windSpeedFormatPref;
    }

    public String getTemperatureFormatPreferences(){
        SharedPreferences sharedPref = initializeSharedPrefs();
        String temperatureFormatPref = sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_TEMPERATURE_UNITS, "0");
        return temperatureFormatPref;
    }

    public String getDistanceFormatPreferences(){
        SharedPreferences sharedPref = initializeSharedPrefs();
        String distanceFormatPref = sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_DISTANCE_UNITS, "0");
        return distanceFormatPref;
    }
    public String getAutoSyncIntervalPreferences() {
        SharedPreferences sharedPref = initializeSharedPrefs();
        String autoSyncIntervalPref = sharedPref.getString(SettingsActivity.PrefsFragment.KEY_PREF_AUTO_SYNC_INTERVAL, "0");
        return autoSyncIntervalPref;
    }
    public LocationRequest createLocationRequest() {
        mLocationRequest = new LocationRequest();
        switch (autoSyncIntervalPref) {
            case "1":
                mLocationRequest.setInterval(180000);
                break;
            case "2":
                mLocationRequest.setInterval(3600000);
                break;
            case "3":
                mLocationRequest.setInterval(7200000);
                break;
            case "4":
                mLocationRequest.setInterval(14400000);
                break;
            case "5":
                mLocationRequest.setInterval(21600000);
                break;
            default:
                mLocationRequest.setInterval(2000000000);
                break;
        }
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        return mLocationRequest;
    }
    public Location getLastKnownLocation() throws SecurityException {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        return mLastLocation;
    }
    protected void startLocationUpdates() throws SecurityException{
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else{
            mGoogleApiClient.connect();
            if(mGoogleApiClient.isConnected()) {
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
        forecastURL = "https://api.forecast.io/forecast/" + forecastAPIKey + "/" + latitude + "," + longitude + "?exclude=['minutely','hourly','daily','alerts','flags']";
        if(ableToSync){
        refreshCurrentWeatherData();
            ableToSync = false;
        }
    }


    // User Preferences End
}
