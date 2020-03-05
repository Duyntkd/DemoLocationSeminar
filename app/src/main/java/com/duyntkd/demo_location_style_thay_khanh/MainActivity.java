package com.duyntkd.demo_location_style_thay_khanh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickToOpenMap(View view) {
        Intent intent = new Intent(this, GoogleMapActivity.class);
        startActivity(intent);

    }

    public void clickToGetLocation(View view) {
        Intent intent = new Intent(this, DataLocationActivity.class);
        startActivity(intent);
    }
}
