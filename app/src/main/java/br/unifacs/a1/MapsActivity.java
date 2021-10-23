package br.unifacs.a1;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import br.unifacs.a1.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_LOCATION_UPDATES = 2;
    private static final int REQUEST_LAST_LOCATION = 3;

    private GoogleMap mMap;
    private Circle circle;
    private Marker marker, salvador;

    private boolean update = false;

    private SharedPreferences dados;

    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dados = getSharedPreferences("Config", Context.MODE_PRIVATE);

        br.unifacs.a1.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        markers();
        mapConfig();
        updateStatusBar(null);
        locationUpdates();
    }

    public boolean onMyLocationButtonClick() {
        return false;
    }

    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this,
                getResources().getString(R.string.msgLocAtual) + "(" + location.getLatitude() + "," + location.getLongitude() + "," + location.getAltitude() + ")",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // TODO: Adiciona um marcador em Salvador (case 1)
        if (requestCode == REQUEST_LAST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                lastLocation();
        } else
                permissionDenied();

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                enableLocation();
            else
                permissionDenied();
        }

        if (requestCode == REQUEST_LOCATION_UPDATES) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // O usuário acabou de dar a permissão
                locationUpdates();
            } else
                permissionDenied();
        }
    }

    private void locationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            client = LocationServices.getFusedLocationProviderClient(this);

            LocationRequest request = LocationRequest.create();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(5 * 1000);
            request.setFastestInterval(100);

            //marcador da posição atual
            //criação do círculo no marcador
            //course up
            LocationCallback callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    update = true;

                    updateStatusBar(location);

                    //marcador da posição atual
                    marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    marker.setAnchor(0.5f, 0.5f);
                    marker.setRotation(location.getBearing());
                    marker.setVisible(true);

                    //criação do círculo no marcador
                    circle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
                    circle.setRadius(location.getAccuracy());
                    circle.setVisible(true);

                    //course up
                    if (dados.getString("Orientacao", getResources().getString(R.string.labelMap3)).equals(getResources().getString(R.string.labelMap3))) {
                        CameraPosition cameraPosition =
                                new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .bearing(location.getBearing())
                                        .zoom(18.0f)
                                        .tilt(0)
                                        .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            };
            client.requestLocationUpdates(request, callback, null);
        } else {
            // Solicite a permissão
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_UPDATES);
        }
    }

    private void updateStatusBar(Location location) {
        TextView textView = findViewById(R.id.txtStatusBar);
        String texto = getResources().getString(R.string.labelLoc) + "\n",
                lat = getResources().getString(R.string.labelLocLat),
                longi = getResources().getString(R.string.labelLocLong),
                speed = getResources().getString(R.string.labelLocSpeed),
                DD = getResources().getString(R.string.labelGrau1),
                DDM = getResources().getString(R.string.labelGrau2),
                DMS = getResources().getString(R.string.labelGrau3);

        if (location != null) {
            //TODO: status bar config
            salvador.setVisible(false);

            if (dados.getBoolean("Km/h", true)) {
                location.setSpeed(location.getSpeed() * 3.6f);
                speed = getResources().getString(R.string.labelLocSpeedKm);
            }
            if (dados.getBoolean("Mph", true)) {
                location.setSpeed(location.getSpeed() * 2.23694f);
                speed = getResources().getString(R.string.labelLocSpeedMph);
            }

            //coordinates config
            if (dados.getString("Formato", DD).equals(DD)) {
                texto += lat + location.getLatitude() + "\n"
                        + longi + location.getLongitude() + "\n";
            }
            if (dados.getString("Formato", DD).equals(DDM)) {
                lat = getResources().getString(R.string.labelLocLatDDM);
                longi = getResources().getString(R.string.labelLocLongDDM);
                texto += lat + Location.convert(location.getLatitude(), Location.FORMAT_MINUTES) + "\n"
                        + longi + Location.convert(location.getLongitude(), Location.FORMAT_MINUTES) + "\n";
            }
            if (dados.getString("Formato", DD).equals(DMS)) {
                lat = getResources().getString(R.string.labelLocLongDMS);
                longi = getResources().getString(R.string.labelLocLongDMS);
                texto += lat + Location.convert(location.getLatitude(), Location.FORMAT_SECONDS) + "\n"
                        + longi + Location.convert(location.getLongitude(), Location.FORMAT_SECONDS) + "\n";
            }

            texto += speed + location.getSpeed();
        } else {
            texto += getResources().getString(R.string.labelUpdateUnavailable);
            salvador.setVisible(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(salvador.getPosition(), 15.0f));
        }
        textView.setText(texto);
    }

    private void lastLocation() {
        // Se a app já possui a permissão, ativa a camada de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // A permissão foi dada
            client.getLastLocation().addOnSuccessListener(this, location -> {
                //TODO: adicionar marker da última posição (case 2)
                if (location != null && !update) {
                    marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    marker.setTitle(getResources().getString(R.string.labelLastLoc));
                    marker.setVisible(true);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15.0f));
                }
            });
        } else {
            // Solicite a permissão
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LAST_LOCATION);
        }
    }

    public void mapConfig() {
        //satelite
        if (dados.getBoolean("Satelite", true))
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //vetorial
        if (dados.getBoolean("Vetorial", true))
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //trafego
        if (dados.getBoolean("Mostrar trafego", true))
            mMap.setTrafficEnabled(true);

        //configurações gerais
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);

        //orientações
        UiSettings mapUI = mMap.getUiSettings();
        mapUI.setZoomControlsEnabled(true);

        String mapNone = getResources().getString(R.string.labelMap1),
                mapNorth = getResources().getString(R.string.labelMap2),
                mapCourse = getResources().getString(R.string.labelMap3);
        //none
        if (dados.getString("Orientacao", mapNone).equals(mapNone)) {
            mapUI.setAllGesturesEnabled(true);
            mapUI.setCompassEnabled(true);
        }
        //north up
        else if (dados.getString("Orientacao", mapNorth).equals(mapNorth)) {
            mapUI.setZoomGesturesEnabled(true);
            mapUI.setRotateGesturesEnabled(false);
            mapUI.setCompassEnabled(false);
        }
        //course up
        else if (dados.getString("Orientacao", mapCourse).equals(mapCourse)) {
            mapUI.setCompassEnabled(false);
            mapUI.setRotateGesturesEnabled(false);
            mapUI.setZoomGesturesEnabled(true);
        }

        enableLocation();
    }

    private void enableLocation() {
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    private void markers() {
        salvador = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-12.9704, -38.5124))
                .title(getResources().getString(R.string.labelMarker))
                .visible(false));
        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-12.937620, -38.413260))
                .title(getResources().getString(R.string.labelLocMarker))
                .visible(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carro)));
        circle = mMap.addCircle(new CircleOptions()
                .visible(false)
                .center(new LatLng(-12.977620, -38.513260))
                .radius(10)
                .strokeColor(0xAA000000)
                .fillColor(0x5DE5FC1E));
    }

    private void permissionDenied() {
        //O usuário não deu a permissão solicitada
        Toast.makeText(this, getResources().getString(R.string.msgLocPermissionDenied), Toast.LENGTH_SHORT).show();
        finish();
    }
}