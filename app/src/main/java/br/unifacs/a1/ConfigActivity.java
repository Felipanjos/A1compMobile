package br.unifacs.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Button botao_apresentacao = (Button) findViewById(R.id.button_config_format);
        Button botao_velocidade = (Button) findViewById(R.id.button_config_speed);
        Button botao_orientacao = (Button) findViewById(R.id.button_config_orientation);
        Button botao_tipo = (Button) findViewById(R.id.button_config_type);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_config_format:

                break;
            case R.id.button_config_speed:

                break;
            case R.id.button_config_orientation:

                break;
            case R.id.button_config_type:

                break;
            case R.id.button_config_back:
                finish();
                break;
        }
    }
}