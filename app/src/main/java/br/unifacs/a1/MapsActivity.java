package br.unifacs.a1;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import br.unifacs.a1.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_LAST_LOCATION = 1;
    private static final int REQUEST_LOCATION_UPDATES = 2;

    boolean gps_enabled = false;
    boolean flagUpdate = false;

    private GoogleMap mMap;
    private Marker currentPos, lastPos;
    private final LatLng ATUAL = new LatLng(-12.937620, -38.413260);
    private final LatLng ANTERIOR = new LatLng(-12.977620, -38.513260);

    private SharedPreferences dados;

    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dados = getSharedPreferences("Config", Context.MODE_PRIVATE);

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        trataGPS(lm);
        br.unifacs.a1.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // recebe última localização
        atualizaLocationUpdatesView(null);
        atualizaLastLocationView(null);
        startLocationUpdates();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        currentPos = mMap.addMarker(new MarkerOptions()
                .position(ATUAL)
                .title(getResources().getString(R.string.labelLocMarker))
                .visible(false));

        lastPos = mMap.addMarker(new MarkerOptions()
                .position(ANTERIOR)
                .title(getResources().getString(R.string.labelLastPosition))
                .visible(false));

        // TODO: Adiciona um marcador em Salvador (case 1)
        if (!gps_enabled)
            mMap.addMarker(new MarkerOptions().position(new LatLng(-12.9704, -38.5124)).title(getResources().getString(R.string.labelMarker)));

        //Configura o mapa e seus elementos, baseado na tela de configurações
        setElements();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LAST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lastLocation();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.msgLocPermissionDenied), Toast.LENGTH_SHORT).show();
            finish();
        }
        if (requestCode == REQUEST_LOCATION_UPDATES) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // O usuário acabou de dar a permissão
                startLocationUpdates();
            }
            else {
                // O usuário não deu a permissão solicitada
                Toast.makeText(this, getResources().getString(R.string.msgLocPermissionDenied), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Fused location
    private void startLocationUpdates() {
        // Se a app já possui a permissão, ativa a calamada de localização
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            flagUpdate = true;
            // A permissão foi dada
            // Cria o cliente FusedLocation
            client = LocationServices.getFusedLocationProviderClient(this);

            // Configura solicitações de localização
            LocationRequest request = LocationRequest.create();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(5 * 5000);
            request.setFastestInterval(1000);
            // Programa o evento a ser chamado em intervalo regulares de tempo
            LocationCallback callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    atualizaLocationUpdatesView(location);
                    // TODO: marker da posição atual (case 3)
                    if (gps_enabled){
                        currentPos.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                        currentPos.setVisible(true);
                    }
                }
            };
            client.requestLocationUpdates(request, callback,null);
        } else {
            // Solicite a permissão
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_UPDATES);
        }
    }

    private void atualizaLocationUpdatesView(Location location) {
        TextView textView = findViewById(R.id.txtStatusBar);
        String texto = getResources().getString(R.string.labelLoc) + "\n",
                lat = getResources().getString(R.string.labelLocLat),
                longi = getResources().getString(R.string.labelLocLong),
                speed = getResources().getString(R.string.labelLocSpeed),
                bearing = getResources().getString(R.string.labelLocCourse),
                acc = getResources().getString(R.string.labelLocAccuracy),
                DDM = getResources().getString(R.string.labelGrau2),
                DMS = getResources().getString(R.string.labelMap3);

        if (location != null) {
            //TODO: status bar config
            if (dados.getBoolean("Km/h", true)) {
                location.setSpeed(location.getSpeed() * (float) 3.6);
                speed = getResources().getString(R.string.labelLocSpeedKm);
            }
            if (dados.getBoolean("Mph", true)) {
                location.setSpeed(location.getSpeed() * (float) 2.23694);
                speed = getResources().getString(R.string.labelLocSpeedMph);
            }

//            if (dados.getString("Formato", DDM).equals(DDM)) {
//                String latDDM = converteCoordenadas(location.getLatitude(), "DDM");
//                String longDDM = converteCoordenadas(location.getLongitude(), "DDM");
//                if (!latDDM.equals("null")) {
//                    Toast.makeText(this, "DDM selecionado", Toast.LENGTH_SHORT).show();
//                }
//                if (longDDM.equals("null")) {
//                    Toast.makeText(this, "DDM2 selecionado", Toast.LENGTH_SHORT).show();
//                }
//            }
//            if (dados.getString("Formato", DMS).equals(DMS)) {
//                String latDMS = converteCoordenadas(location.getLatitude(), "DMS");
//                String longDMS = converteCoordenadas(location.getLongitude(), "DMS");
//                if (latDMS.equals("null")) {
//                    Toast.makeText(this, "DMS2 selecionado", Toast.LENGTH_SHORT).show();
//                }
//                if (longDMS.equals("null")) {
//                    Toast.makeText(this, "DMS2 selecionado", Toast.LENGTH_SHORT).show();
//                }
//            }

            texto += lat + location.getLatitude() + "\n"
                    + longi + location.getLongitude() + "\n"
                    + speed + location.getSpeed() + "\n"
                    + bearing + location.getBearing() + "\n"
                    + acc + location.getAccuracy();
        }
        else {
            texto += getResources().getString(R.string.labelUpdateUnavailable);
        }
        textView.setText(texto);
    }

    private void atualizaLastLocationView(Location location) {
        TextView textViewLast = findViewById(R.id.txtLastStatusBar);
        String texto = getResources().getString(R.string.labelLastLoc) + "\n";
        if (location != null) {
            texto += getResources().getString(R.string.labelLocLat) + location.getLatitude() + "\n"
                    + getResources().getString(R.string.labelLocLong) + location.getLongitude() + "\n"
                    + getResources().getString(R.string.labelLocSpeed) + location.getSpeed() + "\n"
                    + getResources().getString(R.string.labelLocCourse) + location.getBearing();
        }
        else {
            texto += getResources().getString(R.string.labelLocUnavailable);
        }
        textViewLast.setText(texto);
    }

    private void lastLocation() {
        // Se a app já possui a permissão, ativa a camada de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // A permissão foi dada
            client = LocationServices.getFusedLocationProviderClient(this);
            client.getLastLocation().addOnSuccessListener(this, location -> {
                atualizaLastLocationView(location);
                //TODO: adicionar marker da última posição (case 2)
                if (!flagUpdate) {
                    lastPos.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    lastPos.setVisible(true);
                }
            });
        } else {
            // Solicite a permissão
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LAST_LOCATION);
        }
    }

    private void trataGPS(LocationManager lm) {
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {
            Toast.makeText(this, getResources().getString(R.string.msgNoGPS), Toast.LENGTH_SHORT).show();
        }
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
            //TODO: adicionar orientacao de mapa Course up
        }
    }

    private void setElements() {
        setUI();
        setLocation();
        setMap();
    }

    private String  converteCoordenadas(double cord, String tipo) {

        String texto = String.valueOf(cord);
        int separador = texto.indexOf(".");
        String valor;

        if (cord >= 0)
            valor = "E";
        else
            valor = "S";

        String textoGrau = texto.substring(0, separador),
                textoMinuto = "0." + texto.substring(separador);

        int grau = Integer.parseInt(textoGrau);
        double minuto = Double.parseDouble(textoMinuto);
        minuto *= 60;
        Toast.makeText(this, "estou aqui 1", Toast.LENGTH_SHORT).show();

        if (tipo.equalsIgnoreCase("DDM")) {
            Toast.makeText(this, "estou aqui 2", Toast.LENGTH_SHORT).show();
            return grau + "º " + minuto + "' " + "\" " + valor;
        }

        if (tipo.equalsIgnoreCase("DMS")) {
            Toast.makeText(this, "estou aqui 3", Toast.LENGTH_SHORT).show();
            textoMinuto = String.valueOf(minuto);
            textoMinuto = textoMinuto.substring(0, separador);
                String textoSegundo = "0." + textoMinuto.substring(separador);

            double segundo = Double.parseDouble(textoSegundo);
            segundo *= 60;

            return grau + "º " + textoMinuto + "' " + segundo + "\" " + valor;
        }
        else {
            Toast.makeText(this, "estou aqui 4", Toast.LENGTH_SHORT).show();
            return "null";
        }
    }
}