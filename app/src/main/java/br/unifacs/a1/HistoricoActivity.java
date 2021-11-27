package br.unifacs.a1;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.unifacs.a1.databinding.ActivityHistoricoBinding;

public class HistoricoActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference tabela;
    private double latInicio, longInicio,
                    latFim, longFim;
    private Coordenada inicio, fim, elemento;
    private Marker start, end;
    private long quantidade = 0;
    private TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        br.unifacs.a1.databinding.ActivityHistoricoBinding binding = ActivityHistoricoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapHistorico);
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        tabela = FirebaseDatabase.getInstance().getReference("coordenada");
        setInfo();
        drawRoute();
    }

    private void setInfo() {
        tabela.child("0").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    inicio = snapshot.getValue(Coordenada.class);
                    latInicio = inicio.getLatitude();
                    longInicio = inicio.getLongitude();

                    start = mMap.addMarker(new MarkerOptions()
                            .title("Posição inicial")
                            .position(new LatLng(latInicio, longInicio)));

                    CameraPosition cameraPosition =
                            new CameraPosition.Builder().target(new LatLng(latInicio, longInicio))
                                    .zoom(15.f)
                                    .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    setMessage("start");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tabela.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quantidade = snapshot.getChildrenCount() - 1;
                tabela.child(String.valueOf(quantidade)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        fim = snapshot.getValue(Coordenada.class);
                        latFim = fim.getLatitude();
                        longFim = fim.getLongitude();

                        end = mMap.addMarker(new MarkerOptions()
                                .title("Posição final")
                                .position(new LatLng(latFim, longFim)));
                        setMessage("end");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void drawRoute() {
        tabela = FirebaseDatabase.getInstance().getReference("coordenada");
        tabela.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<LatLng> lista = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        elemento = item.getValue(Coordenada.class);
                        texto = (TextView) findViewById(R.id.txtStart);
                        lista.add(new LatLng(elemento.getLatitude(), elemento.getLongitude()));
                    }
                    PolylineOptions poliOption = new PolylineOptions()
                            .addAll(lista)
                            .color(0xffffff00)
                            .width(10);
                    mMap.addPolyline(poliOption);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setMessage(String opcao) {
        TextView start = (TextView) findViewById(R.id.txtStart),
                      end = (TextView) findViewById(R.id.txtEnd);
        String msg = " ";

        switch (opcao) {
            case "start":
                msg = getResources().getString(R.string.labelHistoryStart) + "\n" +
                        "     " + latInicio + "\n" +
                        "     " + longInicio + "\n" +
                        "     " + inicio.getData();
                start.setText(msg);
                break;
            case "end":
                msg += getResources().getString(R.string.labelHistoryEnd) + "\n" +
                        "     " + latFim + "\n" +
                        "     " + longFim + "\n" +
                        "     " + fim.getData();
                end.setText(msg);
                break;
            default:
                msg = getResources().getString(R.string.labelLocUnavailable);
        }
    }
}