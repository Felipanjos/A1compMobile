package br.unifacs.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NavegacaoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navegacao);

        Button buttonNavegacaoVoltar = (Button) findViewById(R.id.buttonNavegacaoVoltar);
        buttonNavegacaoVoltar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonNavegacaoVoltar:
                finish();
                break;
        }
    }
}