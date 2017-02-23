package android.example.locationsample;

/**
 * reference from http://blog.teamtreehouse.com/beginners-guide-location-android
 * https://developer.android.com/training/location/retrieve-current.html
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private static final String TAG = MainActivity.class.getSimpleName();

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    private Location mLastKnownLocation;
    private TextView mResultTextView;
    private Button btnStartUpdate,btnStopUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResultTextView=(TextView)findViewById(R.id.result_text_view);
        btnStartUpdate=(Button)findViewById(R.id.button1);
        btnStopUpdate=(Button)findViewById(R.id.button2);
        btnStartUpdate.setEnabled(false);
        btnStopUpdate.setEnabled(false);
        initLocation();
        createLocationRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectApiClient();
    }

    private void initLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please Grant the location Permission", Toast.LENGTH_LONG).show();

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);

        }

    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void updateLocationOnUi() {
        if (mLastKnownLocation!=null){
            String text="Last Know Location:\n Latitude:- "+mLastKnownLocation.getLatitude()+"  Longitude:- "+mLastKnownLocation.getLongitude();
            mResultTextView.setText(text);
        }
        else {
            Log.e(TAG,"Last Know Location is null");
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this,"Api client is connected now.",Toast.LENGTH_SHORT).show();
        btnStartUpdate.setEnabled(true);
        btnStopUpdate.setEnabled(false);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended "+cause);
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }



    public void startUpdate(View view) {

        if (mGoogleApiClient.isConnected()){
            startLocationUpdates();
            btnStartUpdate.setEnabled(false);
            btnStopUpdate.setEnabled(true);
        }
        else {
            Log.i(TAG, "Google Api Client is not connected");
        }
    }

    public void stopUpdate (View view) {
        stopLocationUpdates();
        btnStartUpdate.setEnabled(true);
        btnStopUpdate.setEnabled(false);
    }



    public void disconnectApiClient() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation=location;
        updateLocationOnUi();

    }
}
