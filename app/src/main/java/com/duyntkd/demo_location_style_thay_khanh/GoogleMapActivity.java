package com.duyntkd.demo_location_style_thay_khanh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private Button btnStyle;
    private int state;
    private Location currentLocation;
    //private LocationClient locationClient;
    private static final LatLng BEN_THANH_MARKET = new LatLng(10.7731, 106.6983);
    private static final LatLng SAIGON_OPERA_HOUSE = new LatLng(10.7767, 106.7032);
    private static final LatLng HOME = new LatLng(10.829108, 106.642812);
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng p;
    private int status;
    private ArrayList<LatLng> listPoints;
    private boolean activateSearchInRadius = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        Toolbar mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        state = 0;
        status = 0;

        btnStyle = (Button) findViewById(R.id.btnStyle);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationListener = new MyLocationListener();
    }


    public void clickToChangeStyle(View view) {
        String title = "";

        switch (state) {
            case 0:
                title = "SATELLITE - Change to Normal";
                btnStyle.setText(title);
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                state = 1;
                break;
            case 1:
                title = "HYBRID - Change to None";
                btnStyle.setText(title);
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                state = 2;
                break;
            case 2:
                title = "TERRAIN - Change to Satelite";
                btnStyle.setText(title);
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                state = 3;
                break;
            case 3:
                title = "NONE - Change to SATELLITE";
                btnStyle.setText(title);
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
                state = 4;
                break;
            case 4:
                title = "NORMAL - Change to TERRAIN";
                btnStyle.setText(title);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                state = 0;
                break;

        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setCompassEnabled(true);
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            map.setMyLocationEnabled(true);
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            List<String> pr = locationManager.getProviders(true);
            currentLocation = null;
            for (int i = 0; i < pr.size(); i++) {
                currentLocation = locationManager.getLastKnownLocation(pr.get(i));
                if (currentLocation != null) {
                    Log.d("bbb", currentLocation.getLatitude() + "");
                    break;
                }
            }

            if (currentLocation != null) {
                LatLng currentPos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 15));
                map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            }

            map.getUiSettings().setZoomControlsEnabled(true);
            map.setMyLocationEnabled(true);

            listPoints = new ArrayList<>();
            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    //reset marker when alredy 2
                    Toast.makeText(getBaseContext(), "Position: " + latLng.longitude, Toast.LENGTH_SHORT).show();
                    if (listPoints.size() == 2) {
                        listPoints.clear();
                        map.clear();
                    }
                    //save first point select
                    listPoints.add(latLng);
                    //create marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    if (listPoints.size() == 1) {
                        //Add first marker to the map
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else {
                        //Add second marker
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    }
                    map.addMarker(markerOptions);
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_showtraffic:
                map.setTrafficEnabled(true);
                break;
            case R.id.menu_zoomin:
                map.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.menu_zoomout:
                map.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.menu_gotoLocation:
                CameraPosition cameraPos = new CameraPosition.Builder().target(HOME).zoom(17).bearing(90).tilt(30).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

                map.addMarker(new MarkerOptions().position(HOME).title("Home").snippet("HCM City"));

                break;
            case R.id.menu_showcurrentlocation:
                currentLocation = map.getMyLocation();
                LatLng currentPos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                CameraPosition myPosition = new CameraPosition.Builder().target(currentPos).zoom(17).bearing(90).tilt(30).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        if (status != 0) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (status != 0) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("ddd", "ddd");
                Toast.makeText(getBaseContext(), "Position" + location.getLatitude() + ":" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                p = new LatLng(location.getLatitude(), location.getLongitude());
                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(p, 18));
                    }
                });
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String strStatus = "";
            switch (status) {
                case LocationProvider
                        .AVAILABLE:
                    strStatus = "Available";
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    strStatus = "Out of service";
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    strStatus = "temporarily unvailable";
                    break;
            }
            Toast.makeText(getBaseContext(), provider + " " + strStatus, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(), "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(), "Enabled provider " + provider, Toast.LENGTH_SHORT).show();
        }
    }
}
