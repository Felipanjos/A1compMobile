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
import android.widget.TextView;
import android.widget.Toast;

public class GNSSActivity extends AppCompatActivity implements LocationListener {
    private LocationManager manager; 
    private LocationProvider provider; 
    private GnssStatusCallback callback;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnss);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = manager.getProvider(LocationManager.GPS_PROVIDER);
        GNSSOn();
    }
    @Override
    protected void onStop() {
        super.onStop();
        GNSSOff();
    }
    private void GNSSOn() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manager.requestLocationUpdates(provider.getName(),
                    5*1000,
                    0.1f,
                    this);
            callback = new GnssStatusCallback();
            manager.registerGnssStatusCallback(callback);
        } else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    private void GNSSOff() {
        manager.removeUpdates(this);
        manager.unregisterGnssStatusCallback(callback);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                GNSSOn();
            else
                locationDenied();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        TextView textViewLocation = (TextView)findViewById(R.id.LocationInfo);
        String texto = getResources().getString(R.string.labelLoc) + "\n",
                txtLat = getResources().getString(R.string.labelLocLat),
                txtLong = getResources().getString(R.string.labelLocLong),
                speed = getResources().getString(R.string.labelLocSpeed),
                bearing = getResources().getString(R.string.labelLocCourse),
                unavailable = getResources().getString(R.string.labelLocUnavailable);

        if (location != null) {
            texto += txtLat + Location.convert(location.getLatitude(), Location.FORMAT_SECONDS) + "\n"
                    + txtLong+ Location.convert(location.getLongitude(), Location.FORMAT_SECONDS) + "\n"
                    + speed + location.getSpeed() + "\n"
                    + bearing + location.getBearing();
        }
        else
            texto += unavailable;
        textViewLocation.setText(texto);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void locationDenied() {
        Toast.makeText(this,getResources().getString(R.string.msgLastLocationDenied), Toast.LENGTH_SHORT).show();
        finish();
    }

    private class GnssStatusCallback extends GnssStatus.Callback {
        public GnssStatusCallback() {
            super();
        }
        @Override
        public void onStarted() { }

        @Override
        public void onStopped() { }

        @Override
        public void onFirstFix(int ttffMillis) { }

        @Override
        public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
            TextView textViewSatellite = (TextView) findViewById(R.id.SatelliteInfo);
            String texto = "Dados do Sitema de Posicionamento \n";
            if (status != null) {
                texto += "Número de Satélites:" + status.getSatelliteCount() + "\n" +
                        "Organização: SVID   Azimuth   Elevation   Used In Fix   SNR \n";
                for(int position = 0; position < status.getSatelliteCount(); position++) {
                    texto += status.getSvid(position) + "-" + status.getConstellationType(position) +
                            "  |  " + status.getAzimuthDegrees(position) +
                            "  |  " + status.getElevationDegrees(position) +
                            "  |  " + status.usedInFix(position)  +
                            "  |  " + status.getCn0DbHz(position) + " |X| \n";
                }
            }
            else
                texto += "GNSS Não disponível";
            textViewSatellite.setText(texto);
        }
    }
}