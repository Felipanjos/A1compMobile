package br.unifacs.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HistoricoSelectorActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historicoselector);
        setButtons();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonHistoricoMapa:
                Intent viewMapa = new Intent(this, HistoricoActivity.class);
                startActivity(viewMapa);
                break;
            case R.id.buttonHistoricoTabela:
                Intent viewTabela = new Intent(this, HistoricoTabelaActivity.class);
                startActivity(viewTabela);
                break;
        }
    }

    public void setButtons() {
        Button buttonMapa = (Button) findViewById(R.id.buttonHistoricoMapa),
                buttonTabela = (Button) findViewById(R.id.buttonHistoricoTabela);
        buttonMapa.setOnClickListener(this);
        buttonTabela.setOnClickListener(this);
    }
}