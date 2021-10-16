package br.unifacs.a1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class FusedLocationActivity extends AppCompatActivity {
    // Request Code para serem utilizados no gerenciamento da permissão para localização
    private static final int REQUEST_LAST_LOCATION = 1;
    private static final int REQUEST_LOCATION_UPDATES = 2;

    // Classe central na API Fused Location
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // atualiza informações na tela
//        atualizaLastLocationView(null);
        atualizaLocationUpdatesView(null);
    }

    private void startLocationUpdates() {
        // Se a app já possui a permissão, ativa a calamada de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // A permissão foi dada
            // Cria o cliente FusedLocation
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            // Configura solicitações de localização
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(5 * 1000);
            mLocationRequest.setFastestInterval(1000);
            // Programa o evento a ser chamado em intervalo regulares de tempo
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location=locationResult.getLastLocation();
                atualizaLocationUpdatesView(location);
                }
            };
            //
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
        } else {
            // Solicite a permissão
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_UPDATES);
        }
    }

    private void stopLocationUpdates() {
        if (mFusedLocationProviderClient != null)
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        atualizaLocationUpdatesView(null);
    }

    private void atualizaLocationUpdatesView(Location location) {
        TextView tView = (TextView) findViewById(R.id.txtStatusBar);
        String texto= getResources().getString(R.string.labelLoc);
        if (location != null) {
            texto += getResources().getString(R.string.labelLocLat) + location.getLatitude() + "\n"
                    + getResources().getString(R.string.labelLocLong) + location.getLongitude() + "\n"
                    + getResources().getString(R.string.labelLocSpeed) + location.getSpeed() + "\n"
                    + getResources().getString(R.string.labelLocCourse) + location.getBearing() + "\n"
                    + getResources().getString(R.string.labelLocAccuracy) + location.getAccuracy();
        } else
            texto += getResources().getString(R.string.labelUpdateUnavailable);
        tView.setText(texto);
    }

//    private void lastLocation() {
//        // Se a app já possui a permissão, ativa a camada de localização
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // A permissão foi dada
//            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    atualizaLastLocationView(location);
//                }
//            });
//        } else {
//            // Solicite a permissão
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LAST_LOCATION);
//        }
//    }

//    private void atualizaLastLocationView(Location location) {
//        TextView tv_ll=(TextView)findViewById(R.id.tv_lastlocation);
//        String texto= getResources().getString(R.string.labelLastLoc);
//
//        if (location != null) {
//            texto += String.valueOf(getResources().getString(R.string.labelLocLat) + location.getLatitude()) + "\n"
//                    + String.valueOf(getResources().getString(R.string.labelLocLong) + location.getLongitude()) + "\n"
//                    + String.valueOf(getResources().getString(R.string.labelLocSpeed) + location.getSpeed()) + "\n"
//                    + String.valueOf(getResources().getString(R.string.labelLocCourse) + location.getBearing());
//        }
//        else {
//            texto += getResources().getString(R.string.labelLocUnavailable);
//        }
//        tv_ll.setText(texto);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        if (requestCode == REQUEST_LAST_LOCATION) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // O usuário acabou de dar a permissão
//                lastLocation();
//            }
//            else {
//                // O usuário não deu a permissão solicitada
//                Toast.makeText(this, getResources().getString(R.string.msgLastLocationDenied), Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
        if (requestCode == REQUEST_LOCATION_UPDATES) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // O usuário acabou de dar a permissão
                startLocationUpdates();
            }
            else {
                // O usuário não deu a permissão solicitada
                Toast.makeText(this, getResources().getString(R.string.msgLastLocationDenied), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}