package com.lucasrizzotto.edgeweather.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.lucasrizzotto.edgeweather.R;
import com.lucasrizzotto.edgeweather.weather.Current;
import com.lucasrizzotto.edgeweather.weather.Day;
import com.lucasrizzotto.edgeweather.weather.Forecast;
import com.lucasrizzotto.edgeweather.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public final static String DAILY_FORECAST = "DAILY_FORECAST";
    public final static String HOURLY_FORECAST = "HOURLY_FORECAST";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000; // Time for resolution
    private static final int NUMBER_OF_LIST_ITEMS = 7;

    // Google API variables
    private GoogleApiClient mGoogleApiClient;
    private LocationListener mLocationListener;
    private LocationRequest mLocationRequest;

    // User location
    private double mUserLat;
    private double mUserLon;

    // Forecast Object that holds all weather data
    private Forecast mForecast;

    // View variables to be manipulated
    @BindView(R.id.tempTextView) TextView mTemperatureTextView;
    @BindView(R.id.centralImageView) ImageView mCentralImageView;
    @BindView(R.id.backgroundLayout) RelativeLayout mBackgroundLayout;
    @BindView(R.id.locationTextView) TextView mLocationTextView;
    @BindView(R.id.refreshImageView) ImageView mRefreshImageView;
    @BindView(R.id.refreshProgressBar) ProgressBar mRefreshProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER) // Accuracy of the location
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast();
            }
        });

        getForecast();

    }

    // Main function, creates the Google Map API client, the Location request and makes the API request to the forecast (thus gathering the JSON data)
    private void getForecast() {

        // Setting up all the variables necessary to build the URL used in the API call
        String apiKey = "50dd8a5cdff768afd6e1a424e59d3249";
        String url = "https://api.forecast.io/forecast/" + apiKey + "/" + mUserLat + "," + mUserLon;
        Log.d(TAG, "API call URL: " + url);


        if(isNetworkAvailable())
        {
            toggleRefresh();
            Log.d(TAG, "Network is available");
            // Creating the OKHttpClient object and setting the creating the request, the call and executing the enqueue method
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Call call = client.newCall(request);

            Log.d(TAG, "Performing callback");
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "CALLBACK FAILED");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    Log.d(TAG, "Response to callback received. Performing the refresh");

                    String jsonData = response.body().string();

                    if(response.isSuccessful())
                    {
                        Log.d(TAG, "Response was successful. Building the CurrentWeather object.");
                        try {
                            Log.d(TAG, jsonData);
                            mForecast = parseForecastDetails(jsonData); // Build the CurrentWeather object
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG,  "Just about to run the activity");
                                    UpdateWeatherActivity();
                                }
                            });
                        } catch (JSONException e) {
                            Log.d(TAG, "Try/catch block has failed");
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    }







    /*
    *
    * SETTING UP GOOGLE SERVICES
    * AND GRABBING LAT/LON
    * TO BE USED IN THE FORECAST
    *
     */

    // After the activity is created, we will connect the API Client asynchronously
    // We are using onResume because it's after onCreate and onStart, so the app will already be functional
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    // Once connected, get the location (needs a special permission)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connection to Location Services active");

        // Generally the user's last registered location is their current location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        // But what if the user has never had a location registered before?
        if(location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    // Handling the change of locations
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        // Grab the Lat and Lon data
        mUserLat = location.getLatitude();
        mUserLon = location.getLongitude();

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(mUserLat, mUserLon, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i=0; i<maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
                Log.d(TAG, address.get(0).getAddressLine(i));
            }

            String finalAddress = builder.toString(); // This is the complete address.
            String userCity = address.get(0).getAddressLine(1);
            Log.d(TAG, userCity);
            mForecast.getCurrent().setLocation(userCity);
        } catch (IOException e) {}
        catch (NullPointerException e) {}
    }

    // If the Activity is paused, the connection will be broken
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this); // Removing the updates we acquired
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection to Location Services interrupted");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection to Location Services active");

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }







    /*
    *
    * GATHERING THE WEATHER JSON DATA
    * AND DISPLAYING IT CORRECTLY
    * ON THE RELEVANT ACTIVITY
    *
     */

        // Creates the Forecast object using the methods we have for setting up the Current and Dailyweather objects
        private Forecast parseForecastDetails(String jsonData) throws JSONException {
            Forecast forecast = new Forecast();
            forecast.setCurrent(getCurrentWeatherInformation(jsonData));
            forecast.setDailyForecast(getDailyWeatherInformation(jsonData));
            forecast.setHourlyForecast(getHourlyWeatherInformation(jsonData));

            // Grabs the Timezone and places it in the Forecast object
            JSONObject fullInfo = new JSONObject(jsonData);
            JSONObject currently = fullInfo.getJSONObject("currently");
            forecast.setTimeZone(fullInfo.getString("timezone"));

            return forecast;
        }

        // Organizes the DAILY weather data using the model
        private Day[] getDailyWeatherInformation(String jsonData) throws JSONException {

            // Creating JSONObject with full JSON data
            JSONObject fullInfo = new JSONObject(jsonData);

            // Get the specific object pertinent to the days
            JSONObject daily = fullInfo.getJSONObject("daily");

            // Get the array DATA inside the object
            JSONArray data = daily.getJSONArray("data");

            // Create a new DAY array of objects that will hold all the values of interest
            Day[] days = new Day[NUMBER_OF_LIST_ITEMS];

            for(int i=0; i < NUMBER_OF_LIST_ITEMS; i++) {
                JSONObject jsonDayWeather = data.getJSONObject(i);
                Day day = new Day();
                day.setSummary(jsonDayWeather.getString("summary"));
                day.setIcon(jsonDayWeather.getString("icon"));
                day.setTemperatureMax(jsonDayWeather.getDouble("temperatureMin"));
                day.setTemperatureMin(jsonDayWeather.getDouble("temperatureMin"));
                day.setTime(jsonDayWeather.getLong("time"));
                days[i] = day;
            }

            return days;
        }

    private Hour[] getHourlyWeatherInformation(String jsonData) throws JSONException {
        JSONObject fullInfo = new JSONObject(jsonData); // Get first object
        JSONObject hourly = fullInfo.getJSONObject("hourly"); // Get second object
        JSONArray data = hourly.getJSONArray("data"); // Get full data array

        Hour[] allHours = new Hour[NUMBER_OF_LIST_ITEMS];

        for(int i = 0; i < NUMBER_OF_LIST_ITEMS; i++) {
            JSONObject jsonHourlyWeather = data.getJSONObject(i); // Get object according to data
            Hour thisHour = new Hour();
            thisHour.setIcon(jsonHourlyWeather.getString("icon"));
            thisHour.setTemperature(jsonHourlyWeather.getDouble("apparentTemperature"));
            thisHour.setTime(jsonHourlyWeather.getLong("time"));
            thisHour.setSummary(jsonHourlyWeather.getString("summary"));
            thisHour.setTimeZone(fullInfo.getString("timezone"));
            allHours[i] = thisHour;
        }
        return allHours;
    }

    //This function will build the CurrentWeather object that will be used in the Forecast
    private Current getCurrentWeatherInformation(String jsonData) throws JSONException {
        JSONObject fullInfo = new JSONObject(jsonData); // Turning all data into a JSONObject
        JSONObject currently = fullInfo.getJSONObject("currently"); // Being more specific getting the "currently" JSONObject within the JSONObject

        Current currentForecast = new Current(); // Create the CurrentWeather object that will store all the data
        currentForecast.setTemperature(currently.getDouble("temperature"));
        currentForecast.setIcon(currently.getString("icon"));
        currentForecast.setLocation(fullInfo.getString("timezone"));

        return currentForecast;
    }

    // Updates the view variables with the current weather data present in the Forecast object
    private void UpdateWeatherActivity()
    {
        Current current = mForecast.getCurrent();
        mTemperatureTextView.setText(current.getTemperature() + "°");
        mLocationTextView.setText(current.getLocation());

        // Set background according to time of the day
        mBackgroundLayout.setBackgroundColor(Color.parseColor(current.getBackgroundResource()));

        // Setting main image in accordance to the forecast
        mCentralImageView.setImageResource(current.getMainIconResource());
}

    // Checks if the Network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    // Handles the spinning animations when you press the refresh button
    private void toggleRefresh() {

        if(mRefreshProgressBar.getVisibility() == View.INVISIBLE)
        {
            mRefreshProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mRefreshProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }

    }





    /*
        OTHER UI INTERACTIONS
    */

    // Button that changes to the Daily Activity
    @OnClick(R.id.dailyForecastButton)
    public void startDailyActivity(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
        startActivity(intent);
    }

    @OnClick(R.id.hourlyForecastButton)
    public void startHourlyActivity(View view) {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
        startActivity(intent);
    }



    /*
        THIS CODE WOULD BE USED IF YOU WANTED TO DO THINGS LIKE ADDING MARKERS TO A NON-EXISTING GOOGLE MAP
    */
    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

}


