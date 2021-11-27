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

import java.util.ArrayList;

public class GNSSRadarActivity extends AppCompatActivity implements LocationListener {

    private LocationManager manager;
    private LocationProvider provider;
    private MyGnssStatusCallback callback;
    private static final int REQUEST_LOCATION = 1;
    private GNSSRadarDraw radarView;
    private TextView count, use;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnssradar);
        radarView = findViewById(R.id.radarView);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = manager.getProvider(LocationManager.GPS_PROVIDER);
        count = (TextView) findViewById(R.id.satelliteCount);
        use = (TextView) findViewById(R.id.satelliteUse);
        setCaptions();
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
                    5 * 1000,
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

            int using = 0;
            int satelites = status.getSatelliteCount();
            for (int count = 0; count < satelites; count++) {
                if (status.usedInFix(count))
                    using++;
            }

            count.setText(getResources().getString(R.string.labelSatelliteCount) + String.valueOf(satelites));
            use.setText(getResources().getString(R.string.labelSatelliteUse) + String.valueOf(using));
        }
    }

    public void setCaptions() {
        TextView sinal = (TextView) findViewById(R.id.txtSignal),
                formatos = (TextView) findViewById(R.id.txtGeometrics);
        String msg,
                red = getResources().getString(R.string.red),
                orange = getResources().getString(R.string.laranja),
                yellow = getResources().getString(R.string.yellow),
                lightgreen = getResources().getString(R.string.lightgreen),
                green = getResources().getString(R.string.verde),
                square = getResources().getString(R.string.square),
                losango = getResources().getString(R.string.losango),
                triangulo = getResources().getString(R.string.triangulo),
                trapezio = getResources().getString(R.string.trapezio),
                circulo = getResources().getString(R.string.circulo);

        msg = getResources().getString(R.string.figures) + "\n" +
                square + " GPS \uD83C\uDDFA\uD83C\uDDF8 \n" +
                losango + " BeiDou \uD83C\uDDE8\uD83C\uDDF3 \n" +
                triangulo + " GLONASS \uD83C\uDDF7\uD83C\uDDFA \n" +
                trapezio + " Galileo \uD83C\uDDEA\uD83C\uDDFA \n" +
                circulo + " Unknown satellite/origin";
        formatos.setText(msg);

        msg = getResources().getString(R.string.signalQuality) + "\n" +
                red + " 0 ~ 10\n" +
                orange + " 10.1 ~ 20\n" +
                yellow + " 20.1 ~ 30\n" +
                lightgreen + " 30.1 ~ 50\n" +
                green + " 50.1 ~ 100";
        sinal.setText(msg);
    }
}