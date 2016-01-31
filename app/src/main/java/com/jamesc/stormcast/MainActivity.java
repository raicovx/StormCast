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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

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
    private double longitude;
    private double latitude;
    private String forecastAPIKey = "f2e1d661e28dbdd28934a784f1b456a1";
    private String forecastURL;
    private String jsonData;

    //Compatibility Actionbar
    Toolbar actionBar;

    private LocationManager locationManager;
    RelativeLayout currentWeatherLayout;
    private int iconImageId;
    private double temperatureText;
    private String locationText;
    private String summaryText;
    private String timeText;
    private TextView autoSyncOffNotification;
    private TextView timeTextView;
    private TextView temperatureTextView;
    private TextView locationTextView;
    private TextView summaryTextView;
    private ImageView iconImageView;
    private ImageView degreeImageView;
    private FloatingActionButton fabRefresh;
    ProgressBar refreshProgressBar;
    Drawable progressDrawable;

    //Status Variables
    private boolean clicked = false;
    private boolean firstLoad = true;
    private boolean ableToSync;

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
        startLocationManager();
        //Progress Bar
        createProgressBar();

        // Tool Bar
        actionBar = (Toolbar) findViewById(R.id.current_weather_toolbar);
        if(actionBar != null) {
            actionBar.setTitle("Current Weather");
            int actionBarColour = ContextCompat.getColor(getApplicationContext(), R.color.actionBarColorPrimary);
            actionBar.setBackgroundColor(actionBarColour);
            setSupportActionBar(actionBar);
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.current_weather_abs_layout);
        }
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

        //Current Weather Activity's Settings button
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_settings_button));
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(settingsIntent);
            }
        });
        //Current Weather Activity's Settings button End
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
