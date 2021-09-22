package br.unifacs.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NavegacaoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navegacao);
        setButtons();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonNavegacaoInicio:
                Intent viewInicio = new Intent(this, MainActivity.class);
                startActivity(viewInicio);
                break;
            case R.id.buttonNavegacaoConfig:
                Intent viewConfiguracoes = new Intent(this, ConfigActivity.class);
                startActivity(viewConfiguracoes);
                break;
        }
    }

    private void setButtons() {
        Button buttonNavegacaoVoltar = (Button) findViewById(R.id.buttonNavegacaoInicio),
               buttonNavegacaoConfig = (Button) findViewById(R.id.buttonNavegacaoConfig);
        buttonNavegacaoVoltar.setOnClickListener(this);
        buttonNavegacaoConfig.setOnClickListener(this);
    }
}