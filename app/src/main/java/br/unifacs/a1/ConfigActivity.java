package br.unifacs.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Button buttonConfigVoltar = (Button) findViewById(R.id.buttonConfigVoltar);
        Button buttonConfigNavegacao = (Button) findViewById(R.id.buttonConfigNavegacao);
        buttonConfigVoltar.setOnClickListener(this);
        buttonConfigNavegacao.setOnClickListener(this);


        String grau1 = getResources().getString(R.string.labelGrau1),
                grau2 = getResources().getString(R.string.labelGrau2),
                grau3 = getResources().getString(R.string.labelGrau3);

        Spinner dropdown = findViewById(R.id.dropdown_speed2);

        String[] items = new String[]{grau1, grau2, grau3};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buttonConfigNavegacao:
                Intent view_mapa = new Intent(this, NavegacaoActivity.class);
                startActivity(view_mapa);
                finish();
                break;
            case R.id.buttonConfigVoltar:
                finish();
                break;
        }
    }
}