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

        Button botaoconfig = (Button) findViewById(R.id.button_settings);
        Button botaosair = (Button) findViewById(R.id.button_sair);
        botaosair.setOnClickListener(this);
        botaoconfig.setOnClickListener(this);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_settings:
                Intent view_config = new Intent(this, ConfigActivity.class);
                startActivity(view_config);
                finish();
            case R.id.button_sair:
                finish();
        }
    }
}