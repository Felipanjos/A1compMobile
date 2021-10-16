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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import br.unifacs.a1.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private SharedPreferences dados;
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationActivity place;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dados = getSharedPreferences("Config", Context.MODE_PRIVATE);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng salvador = new LatLng(-12.9704, -38.5124);
        mMap.addMarker(new MarkerOptions().position(salvador).title(getResources().getString(R.string.labelMarker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(salvador));
        setElements();
    }

    public void setMap() {
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

        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
    }

    private void setLocation() {
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);
        }
    }

    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, getResources().getString(R.string.msgLocButtonClicked), Toast.LENGTH_SHORT).show();
        return false;
    }

    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this,
                getResources().getString(R.string.msgLocAtual) + "(" + location.getLatitude() + "," + location.getLongitude() + "," + location.getAltitude() + ")",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setLocation();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.msgLocPermissionDenied), Toast.LENGTH_SHORT).show();
        }
    }

    public void setUI() {
        UiSettings mapUI = mMap.getUiSettings();
        mapUI.setZoomControlsEnabled(true);

        String mapNone = getResources().getString(R.string.labelMap1),
                mapNorth = getResources().getString(R.string.labelMap2),
                mapCourse = getResources().getString(R.string.labelMap3);

        if (dados.getString("Orientacao", mapNone).equals(mapNone)) {
            mapUI.setAllGesturesEnabled(true);
            mapUI.setCompassEnabled(true);
        }
        else if (dados.getString("Orientacao", mapNorth).equals(mapNorth)) {
            mapUI.setZoomGesturesEnabled(true);
            mapUI.setRotateGesturesEnabled(false);
            mapUI.setCompassEnabled(false);
        }
        else if (dados.getString("Orientacao", mapCourse).equals(mapCourse)) {

        }
    }

    private void setElements() {
        setUI();
        setLocation();
        setMap();
    }
}
