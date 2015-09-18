package activity.contacts.com.contacts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener
{

    private MapFragment map;
    private LocationManager manager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private MarkerOptions markerOptions;
    private Marker marker;
    private LatLng myCoordinates;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        buildGoogleApiClient();

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            buildAlertMessageNoGps();

        settingUpActionBar();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        markerOptions = new MarkerOptions();
        getMapFragment();
    }

    private void getMapFragment()
    {
        this.map = (MapFragment) getFragmentManager().findFragmentById(R.id.add_location_map);
        this.map.getMap().setOnMapClickListener(this);
        this.map.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);

        if (mLastLocation != null) {
            LatLng myLocation = new LatLng(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude());

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                    17));

            marker = googleMap.addMarker(this.markerOptions
                    .position(myLocation));

            myCoordinates = myLocation;
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void settingUpActionBar()
    {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Choose your location");
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_location:
                addLocationToExpense();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addLocationToExpense()
    {
        if(myCoordinates != null)
        {
            Intent intent = new Intent();
            intent.putExtra("latitude", String.valueOf(myCoordinates.latitude));
            intent.putExtra("longitude", String.valueOf(myCoordinates.longitude));
            setResult(RESULT_OK, intent);
            Toast.makeText(this, "Location Added", Toast.LENGTH_SHORT).show();
            finish();
        }else
            Toast.makeText(this, "Couldn't retrieve your location. Please try again", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onMapClick(LatLng latLng) {
        GoogleMap googleMap = this.map.getMap();

        if(marker != null)
            marker.remove();

        marker = googleMap.addMarker(this.markerOptions
                .position(latLng));

        myCoordinates = latLng;

    }
}