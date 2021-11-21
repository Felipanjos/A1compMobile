package br.unifacs.a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

public class GNSSRadarActivity extends AppCompatActivity implements LocationListener {

    private LocationManager manager;
    private LocationProvider provider;
    private MyGnssStatusCallback callback;
    private static final int REQUEST_LOCATION = 1;
    private GNSSRadarDraw radarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnssradar);
        radarView = findViewById(R.id.radarView);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = manager.getProvider(LocationManager.GPS_PROVIDER);
        GNSSOn();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GNSSOf();
    }

    private void GNSSOn() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manager.requestLocationUpdates(provider.getName(),
                    5*1000,
                    0.1f,
                    this);
            callback = new MyGnssStatusCallback();
            manager.registerGnssStatusCallback(callback);
        } else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    private void GNSSOf() {
        manager.unregisterGnssStatusCallback(callback);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                GNSSOn();
            else {
                Toast.makeText(this, getResources().getString(R.string.labelGnssUnavailable), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {}

    private class MyGnssStatusCallback extends GnssStatus.Callback {
        public MyGnssStatusCallback() {
            super();
        }

        @Override
        public void onStarted() {}

        @Override
        public void onStopped() {}

        @Override
        public void onFirstFix(int ttffMillis) {}

        @Override
        public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
            radarView.onSatelliteStatusChanged(status);
            radarView.invalidate();
        }
    }
}