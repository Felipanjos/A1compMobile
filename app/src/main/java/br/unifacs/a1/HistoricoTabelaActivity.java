package br.unifacs.a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoricoTabelaActivity extends AppCompatActivity {

    private FirebaseDatabase firebase = FirebaseDatabase.getInstance();
    private DatabaseReference tabela = firebase.getReference("coordenada");
    private Coordenada inicio;
    private double latInicio, longInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_tabela);
        TextView texto = (TextView) findViewById(R.id.valor);

//        tabela.child("0").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    inicio = snapshot.getValue(Coordenada.class);
//                    latInicio = inicio.getLatitude();
////                    longInicio = inicio.getLongitude();
//                    texto.setText(String.valueOf(latInicio));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        tabela.child("0").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                inicio =  task.getResult().getValue(Coordenada.class);
//                texto.setText(inicio.getData());
//            }
//        });
    }
}