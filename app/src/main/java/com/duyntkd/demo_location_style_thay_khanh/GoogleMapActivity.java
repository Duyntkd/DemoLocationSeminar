package com.duyntkd.demo_location_style_thay_khanh;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
    private boolean track = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        Toolbar mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        state = 0;
        status = 0;
        track = false;

        btnStyle = (Button) findViewById(R.id.btnStyle);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    public void clickToChangeStyle(View view) {
        String title = "";
        switch (state) {
            case 0:
                title = "SATELLITE - Change to HYBRID";
                btnStyle.setText(title);
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                state = 1;
                break;
            case 1:
                title = "HYBRID - Change to TERRAIN";
                btnStyle.setText(title);
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                state = 2;
                break;
            case 2:
                title = "TERRAIN - Change to NONE";
                btnStyle.setText(title);
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                state = 3;
                break;
            case 3:
                title = "NONE - Change to NORMAL";
                btnStyle.setText(title);
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
                state = 4;
                break;
            case 4:
                title = "NORMAL - Change to SATELLITE";
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
                Log.d("aaa", "bbb");
//                Marker currentMarker = map.addMarker(new MarkerOptions()
//                        .position(currentPos)
//                        .title("My position")
//                        .snippet("Mobile Programming")
//                        .icon(BitmapDescriptorFactory
//                                .fromResource(R.drawable.ic_launcher_foreground)));

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
            case R.id.menu_followcurrentlocation:
                if (track == false) {
                    track = true;
                    Toast.makeText(GoogleMapActivity.this, "Follow enabled", Toast.LENGTH_SHORT).show();

                } else {
                    track = false;
                    Toast.makeText(GoogleMapActivity.this, "Follow disabled", Toast.LENGTH_SHORT).show();
                }
                currentLocation = map.getMyLocation();
                LatLng currentLoc = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                CameraPosition myLoc = new CameraPosition.Builder().target(currentLoc).zoom(17).bearing(90).tilt(30).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(myLoc));
                break;

            case R.id.menu_connecttwopoints:
                //TODO request get direction code below
                if (listPoints.size() == 2) {
                    //Create URL to get request from first marker to second marker
                    String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                    TaskRequestDirection taskRequestDirection = new TaskRequestDirection();
                    taskRequestDirection.execute(url);

                }

            case R.id.menu_getlocationdata:
                //locationListener = new MyLocationListener();
                //status = 1;
                break;
            case R.id.menu_getlocationontouch:
                //map.setOnMapClickListener(new MyOnMapClickListener());
                break;

            case R.id.menu_activateSearchInRadius:
                activateSearchInRadius = !activateSearchInRadius;
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        //Value of dest
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find location
        String mode = "mode=walking";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + param + "&key=" + getString(R.string.map_key);
        return url;

    }

    private String requestDirection(String reqUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                httpURLConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return responseString;
    }

    private String requestLocation(String reqUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                httpURLConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return responseString;
    }

    public class TaskRequestDirection extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse Json here
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("routes")
                        .getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(0).getJSONArray("steps");

                int count = jsonArray.length();
                String[] polyline_array = new String[count];

                JSONObject jsonObject2;

                for (int i = 0; i < count; i++) {
                    jsonObject2 = jsonArray.getJSONObject(i);

                    String polygone = jsonObject2.getJSONObject("polyline").getString("points");

                    polyline_array[i] = polygone;
                }

                int count2 = polyline_array.length;

                for (int i = 0; i < count2; i++) {
                    PolylineOptions options2 = new PolylineOptions();
                    options2.color(Color.BLUE);
                    options2.width(10);
                    options2.addAll(PolyUtil.decode(polyline_array[i]));

                    map.addPolyline(options2);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class TaskRequestLocation extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse Json here
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                //.getJSONObject(0).getJSONArray("legs")
                //.getJSONObject(0).getJSONArray("steps");

                int count = jsonArray.length();
                LatLng[] location_array = new LatLng[count];

                JSONObject jsonObject2;

                for (int i = 0; i < count; i++) {
                    jsonObject2 = jsonArray.getJSONObject(i);

                    double lat = Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location").getString("lat"));

                    double lng = Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location").getString("lng"));

                    LatLng latLng = new LatLng(lat, lng);

                    location_array[i] = latLng;
                }

                int count2 = location_array.length;

                for (int i = 0; i < count2; i++) {

                    map.addMarker(new MarkerOptions().position(location_array[i]).title("Search result").snippet("HCM City"));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
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
                p = new LatLng(location.getLatitude(), location.getLongitude());

                map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        if (track) {
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(p, 18));
                        }
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

    private class MyOnMapClickListener implements GoogleMap.OnMapClickListener {

        @Override
        public void onMapClick(LatLng latLng) {

            Toast.makeText(getBaseContext(), "Position: " + latLng.longitude, Toast.LENGTH_SHORT).show();

        }


    }

    public void clickToFind(View view) {
        map.clear();
        EditText txtFind = (EditText) findViewById(R.id.edtLocation);
        String strLocation = txtFind.getText().toString();
        if (!activateSearchInRadius) {
            if (strLocation != null && !strLocation.trim().equals("")) {
                new GeocoderTask().execute(strLocation);
            }
        } else {
            EditText edtRadius = (EditText) findViewById(R.id.edtRadius);
            String radiusString = edtRadius.getText().toString();

            String baseApiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
            String query = "keyword=" + strLocation;
            String key = "&key=" + getString(R.string.map_key);
            String location = "&location=" + HOME.latitude + "," + HOME.longitude;
            String radius = "&radius=" + radiusString;

            String completeRequestUrl = baseApiUrl + query + location + radius + key;

            TaskRequestLocation taskRequestLocation = new TaskRequestLocation();
            taskRequestLocation.execute(completeRequestUrl);


        }
    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geo = new Geocoder(getBaseContext());
            List<Address> address = null;
            try {
                address = geo.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return address;
        }

        @Override
        protected void onPostExecute(List<Address> result) {
            super.onPostExecute(result);

            if (result == null || result.size() == 0) {
                Toast.makeText(getBaseContext(), "Not found", Toast.LENGTH_SHORT).show();
                return;
            }

            map.clear();
            for (int i = 0; i < result.size(); i++) {
                Address address = (Address) result.get(i);

                LatLng findPos = new LatLng(address.getLatitude(), address.getLongitude());
                String addressText = String.format("%s %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getCountryName());
                MarkerOptions mo = new MarkerOptions();
                mo.position(findPos);
                mo.title(addressText);

                map.addMarker(mo);


                if (i == 0) {
                    map.animateCamera(CameraUpdateFactory.newLatLng(findPos));
                }

            }
        }


    }
}


