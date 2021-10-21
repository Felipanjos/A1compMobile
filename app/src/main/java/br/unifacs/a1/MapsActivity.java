package br.unifacs.a1;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import br.unifacs.a1.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_LAST_LOCATION = 1;
    private static final int REQUEST_LOCATION_UPDATES = 2;

    private SensorManager mSensorManager;
    private SensorEventListener sensorEventListener;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private Float azimut, degrees;

    private GoogleMap mMap;
    private Marker currentPos, lastPos, starterPos;

    private SharedPreferences dados;

    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dados = getSharedPreferences("Config", Context.MODE_PRIVATE);
        client = LocationServices.getFusedLocationProviderClient(this);

        br.unifacs.a1.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //sensor manager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorEventListener= new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    mGravity = event.values;
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    mGeomagnetic = event.values;
                if (mGravity != null && mGeomagnetic != null) {
                    float R[] = new float[9];
                    float I[] = new float[9];
                    boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                    if (success) {
                        float orientation[] = new float[3];
                        SensorManager.getOrientation(R, orientation);
                        azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                        degrees = (float) Math.toDegrees(azimut);
                        /**
                         * TRY THIS TO UPDATE YOUR CAMERA
                         * degrees you can use as bearing
                         * CameraPosition cameraPosition = new CameraPosition( myLatLng, 15, 0, degrees);
                         * map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),200, null);
                         **/
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                //TODO: checar possibildade de desenharo o raio aqui
            }
        };

        // recebe última localização
        updateStatusBar(null);
        locationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        markers();
        //Configura o mapa e seus elementos, baseado na tela de configurações
        setMap();
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
        // TODO: Adiciona um marcador em Salvador (case 1)
        if (requestCode == REQUEST_LAST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                lastLocation();
        } else
            permissionDenied();
        if (requestCode == REQUEST_LOCATION_UPDATES) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // O usuário acabou de dar a permissão
                locationUpdates();
            } else
                permissionDenied();
        }
    }

    //Fused location
    private void locationUpdates() {
        // Se a app já possui a permissão, ativa a calamada de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // A permissão foi dada
//            flagUpdate = true;
            client.getLastLocation().addOnSuccessListener(this, location -> {
                currentPos.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                currentPos.setVisible(true);
            });
            // Configura solicitações de localização
            LocationRequest request = LocationRequest.create();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(25 * 1000);
            request.setFastestInterval(100);
            // Programa o evento a ser chamado em intervalo regulares de tempo
            LocationCallback callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    updateStatusBar(location);

                    // TODO: marker da posição atual (case 3)
                    //marker da última posição atual
                    lastPos.setPosition(currentPos.getPosition());
                    lastPos.setVisible(true);

                    //marker da posição atual
                    currentPos.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    currentPos.setVisible(true);

                    currentPos.setRotation(degrees);

                    //mover câmera
                    CameraPosition cameraPosition =
                            new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .bearing(degrees)
                                    .zoom(17.0f)
                                    .tilt(0)
                                    .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),200, null);
                }
            };
            client.requestLocationUpdates(request, callback, null);
        } else {
            // Solicite a permissão
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_UPDATES);
            lastLocation();
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
            String latDDM, longDDM,
                    latDMS, longDMS;

            if (dados.getBoolean("Km/h", true)) {
                location.setSpeed(location.getSpeed() * (float) 3.6);
                speed = getResources().getString(R.string.labelLocSpeedKm);
            }
            if (dados.getBoolean("Mph", true)) {
                location.setSpeed(location.getSpeed() * (float) 2.23694);
                speed = getResources().getString(R.string.labelLocSpeedMph);
            }

            //coordinates config
            if (dados.getString("Formato", DD).equals(DD)) {
                texto += lat + location.getLatitude() + "\n"
                        + longi + location.getLongitude() + "\n";
            }
//            if (dados.getString("Formato", DD).equals(DDM)) {
//                latDDM = converteCoordenadas(location.getLatitude(), "DDM");
//                longDDM = converteCoordenadas(location.getLongitude(), "DDM");
//
//                texto += lat + latDDM + "\n"
//                        + longi + longDDM + "\n";
//            }
//            if (dados.getString("Formato", DD).equals(DMS)) {
//                latDMS = converteCoordenadas(location.getLatitude(), "DMS");
//                longDMS = converteCoordenadas(location.getLongitude(), "DMS");
//
//                texto += lat + latDMS + "\n"
//                        + longi + longDMS + "\n";
//            }

            texto += speed + location.getSpeed();
        } else
            texto += getResources().getString(R.string.labelUpdateUnavailable);
        textView.setText(texto);
    }

    private void lastLocation() {
        // Se a app já possui a permissão, ativa a camada de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // A permissão foi dada
            client.getLastLocation().addOnSuccessListener(this, location -> {
                //TODO: adicionar marker da última posição (case 2)
                    lastPos.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                    lastPos.setVisible(true);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPos.getPosition(), 15.0f));
            });
        } else {
            // Solicite a permissão
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LAST_LOCATION);
        }
    }

    public void mapConfig() {
        //TODO: exibir formato satelite, vetorial e info de trafego
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

    private void enableLocation() {
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    public void mapOrientations() {
        //TODO: adicionar orientacoes
        UiSettings mapUI = mMap.getUiSettings();
        mapUI.setZoomControlsEnabled(true);

        String mapNone = getResources().getString(R.string.labelMap1),
                mapNorth = getResources().getString(R.string.labelMap2),
                mapCourse = getResources().getString(R.string.labelMap3);
        // none
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
    }

    private void markers() {
        //TODO: marcadores para cases 1, 2 e 3
        //case 1
        starterPos = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-12.9704, -38.5124))
                .title(getResources().getString(R.string.labelMarker))
                .visible(false));
        //case 2
        lastPos = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-12.977620, -38.513260))
                .title(getResources().getString(R.string.labelLastPosition))
                .visible(false));
        //case 3
        currentPos = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-12.937620, -38.413260))
                .title(getResources().getString(R.string.labelLocMarker))
                .visible(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carro)));
    }

    private void permissionDenied() {
        //O usuário não deu a permissão solicitada
        Toast.makeText(this, getResources().getString(R.string.msgLocPermissionDenied), Toast.LENGTH_SHORT).show();
        starterPos.setVisible(true);
        currentPos.setVisible(false);
        lastPos.setVisible(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(starterPos.getPosition(), 15.0f));
    }

    private void setMap() {
        mapOrientations();
        enableLocation();
        mapConfig();
    }

//    private String converteCoordenadas(double cord, String tipo) {
//
//        String texto = String.valueOf(cord);
//        int separador = texto.indexOf(".");
//        String valor;
//
//        if (cord >= 0)
//            valor = "E";
//        else
//            valor = "S";
//
//        String textoGrau = texto.substring(0, separador),
//                textoMinuto = "0." + texto.substring(separador);
//
//        int grau = Integer.parseInt(textoGrau);
//        double minuto = Double.parseDouble(textoMinuto);
//        minuto *= 60;
//        Toast.makeText(this, "estou aqui 1", Toast.LENGTH_SHORT).show();
//
//        if (tipo.equalsIgnoreCase("DDM")) {
//            Toast.makeText(this, "estou aqui 2", Toast.LENGTH_SHORT).show();
//            return grau + "º " + minuto + "' " + "\" " + valor;
//        }
//
//        if (tipo.equalsIgnoreCase("DMS")) {
//            Toast.makeText(this, "estou aqui 3", Toast.LENGTH_SHORT).show();
//            textoMinuto = String.valueOf(minuto);
//            textoMinuto = textoMinuto.substring(0, separador);
//                String textoSegundo = "0." + textoMinuto.substring(separador);
//
//            double segundo = Double.parseDouble(textoSegundo);
//            segundo *= 60;
//
//            return grau + "º " + textoMinuto + "' " + segundo + "\" " + valor;
//        }
//        else {
//            Toast.makeText(this, "estou aqui 4", Toast.LENGTH_SHORT).show();
//            return "null";
//        }
//    }
}