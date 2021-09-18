package br.unifacs.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtons();
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buttonNavegacao:
                Intent viewNavegacao = new Intent(this, NavegacaoActivity.class);
                startActivity(viewNavegacao);
                break;
            case R.id.buttonConfig:
                Intent viewConfig = new Intent(this, ConfigActivity.class);
                startActivity(viewConfig);
                break;
            case R.id.buttonGnss:
                Intent viewGnss = new Intent(this, GNSSActivity.class);
                startActivity(viewGnss);
                break;
            case R.id.buttonHistorico:
                Intent viewHistorico = new Intent(this, HistoricoActivity.class);
                startActivity(viewHistorico);
                break;
            case R.id.buttonCredit:
                Intent viewCredit = new Intent(this, CreditActivity.class);
                startActivity(viewCredit);
                break;
            case R.id.buttonFechar:
                finish();
                break;
        }
    }

    private void setButtons() {
        Button buttonConfig = (Button) findViewById(R.id.buttonConfig),
                buttonSair = (Button) findViewById(R.id.buttonFechar),
                buttonNavegacao = (Button) findViewById(R.id.buttonNavegacao),
                buttonGnss = (Button) findViewById(R.id.buttonGnss),
                buttonCredit = (Button) findViewById(R.id.buttonCredit),
                buttonHistorico = (Button) findViewById(R.id.buttonHistorico);
        buttonSair.setOnClickListener(this);
        buttonNavegacao.setOnClickListener(this);
        buttonGnss.setOnClickListener(this);
        buttonCredit.setOnClickListener(this);
        buttonHistorico.setOnClickListener(this);
        buttonConfig.setOnClickListener(this);
    }
}