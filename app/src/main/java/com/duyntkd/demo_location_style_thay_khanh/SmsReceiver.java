package com.duyntkd.demo_location_style_thay_khanh;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class SmsReceiver extends BroadcastReceiver {

    LocationManager lm;
    LocationListener locationListener;
    String senderTel;

    @Override
    public void onReceive(Context context, Intent intent) {
        //---get the SMS message that was received---
        Bundle myBundle = intent.getExtras();
        SmsMessage[] messages = null;
        String strMessage = "";

        if (myBundle != null) {
            Object[] pdus = (Object[]) myBundle.get("pdus");

            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = myBundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    if (i == 0) {
                        senderTel = messages[i].getOriginatingAddress();
                    }
                } else {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    if (i == 0) {
                        senderTel = messages[i].getOriginatingAddress();
                    }
                }

                strMessage += messages[i].getMessageBody();
                strMessage += "\n";
            }
            if (strMessage.startsWith("Where are you?")) {
                //---use the LocationManager class to obtain locations data---
                lm = (LocationManager)
                        context.getSystemService(Context.LOCATION_SERVICE);

                //---request location updates---
                locationListener = new MyLocationListener();

                try {
                   lm.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            60000,
                            0,
                            locationListener);
                } catch (SecurityException e) {

                }
//---abort the broadcast; SMS messages won’t be broadcasted---
                this.abortBroadcast();
            }
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                //---send a SMS containing the current location---
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(senderTel, null,
                        "http://maps.google.com/maps?q=" + loc.getLatitude() + "," +
                                loc.getLongitude(), null, null);
                //---stop listening for location changes---
//                lm.removeUpdates(locationListener);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
        }
    }
}
