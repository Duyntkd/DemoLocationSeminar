package com.duyntkd.demo_location_style_thay_khanh;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class DataLocationActivity extends AppCompatActivity implements LocationListener {
    private TextView txtLat;
    private TextView txtLong;
    private LocationManager manager;
    private String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_location);

        txtLat = (TextView) findViewById(R.id.edtLat);
        txtLong = (TextView) findViewById(R.id.edtLong);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = manager.getBestProvider(criteria, false);
        Log.d("pro", provider);
        @SuppressLint("MissingPermission") Location location = manager.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        } else {
            txtLat.setText("N/A");
            txtLong.setText("N/A");
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        manager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        txtLat.setText(location.getLatitude() + "");
        txtLong.setText(location.getLongitude() + "");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Disabled provider" + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Enabled provider" + provider, Toast.LENGTH_SHORT).show();
    }
}
