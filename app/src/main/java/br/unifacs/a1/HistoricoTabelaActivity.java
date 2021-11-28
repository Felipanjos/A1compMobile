package br.unifacs.a1;

import static android.icu.lang.UCharacter.IndicPositionalCategory.LEFT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoricoTabelaActivity extends AppCompatActivity {

    private FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    private DatabaseReference tabela = firebase.getReference("coordenada");
    private Coordenada elemento;
    private long quantidade = 0;
    private LinearLayout log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_tabela);
        log = (LinearLayout) findViewById(R.id.layoutLog);

        tabela.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        elemento = item.getValue(Coordenada.class);
                        TextView texto = new TextView(HistoricoTabelaActivity.this);
                        String msg = elemento.getData() + "\n" +
                                "Latitude: " + elemento.getLatitude() + "\n" +
                                "Longitude: " + elemento.getLongitude() + "\n";
                        texto.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        texto.setText(msg);
                        log.addView(texto);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}