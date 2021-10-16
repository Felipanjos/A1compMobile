package br.unifacs.a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FusedLocationActivity extends AppCompatActivity {
    // Request Code para serem utilizados no gerenciamento da permissão para localização
    private static final int REQUEST_LAST_LOCATION = 1;
    private static final int REQUEST_LOCATION_UPDATES = 2;
    private GoogleMap map;

    // Classe central na API Fused Location
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // atualiza informações na tela
        atualizaLocationUpdatesView(null);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        // Se a app já possui a permissão, ativa a calamada de localização
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // A permissão foi dada
            // Cria o cliente FusedLocation
            client = LocationServices.getFusedLocationProviderClient(this);
            // Configura solicitações de localização
            LocationRequest request = LocationRequest.create();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(5000);
            request.setFastestInterval(1000);
            // Programa o evento a ser chamado em intervalo regulares de tempo
            LocationCallback callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    atualizaLocationUpdatesView(location);
                }
            };
            //
            client.requestLocationUpdates(request, callback,null);
        } else {
            // Solicite a permissão
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_UPDATES);
        }
    }

    private void atualizaLocationUpdatesView(Location location) {
        TextView textView = findViewById(R.id.txtStatusBar);
        String texto = getResources().getString(R.string.labelLoc) + "\n";

        if (location != null) {
            texto += getResources().getString(R.string.labelLocLat) + location.getLatitude() + "\n"
                    + getResources().getString(R.string.labelLocLong) + location.getLongitude() + "\n"
                    + getResources().getString(R.string.labelLocSpeed) + location.getSpeed() + "\n"
                    + getResources().getString(R.string.labelLocCourse) + location.getBearing() + "\n"
                    + getResources().getString(R.string.labelLocAccuracy) + location.getAccuracy();
        }
        else
            texto += getResources().getString(R.string.labelUpdateUnavailable);
        textView.setText(texto);
    }

    private void lastLocation() {
        // Se a app já possui a permissão, ativa a camada de localização
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // A permissão foi dada
            client = LocationServices.getFusedLocationProviderClient(this);
            client.getLastLocation().addOnSuccessListener(this,
                    location -> {
                        //TODO: adiciona marker da última posição
                        MarkerOptions ultimaPosicao = new MarkerOptions();
                            ultimaPosicao.position(new LatLng(location.getLatitude(), location.getLongitude()));
                            ultimaPosicao.title("Última posição");
                        map.addMarker(ultimaPosicao);
                    });
        } else
            // Solicite a permissão
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LAST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == REQUEST_LAST_LOCATION) {
            if(grantResults.length == 1 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                // O usuário acabou de dar a permissão
                lastLocation();
            }
            else {
                // O usuário não deu a permissão solicitada
                Toast.makeText(this, getResources().getString(R.string.msgLocPermissionDenied),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == REQUEST_LOCATION_UPDATES) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                // O usuário acabou de dar a permissão
                startLocationUpdates();
            else {
                // O usuário não deu a permissão solicitada
                Toast.makeText(this, getResources().getString(R.string.msgLocPermissionDenied), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}