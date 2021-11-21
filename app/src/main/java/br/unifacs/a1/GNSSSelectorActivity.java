package br.unifacs.a1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GNSSSelectorActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnssselector);
        setButtons();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonGNSSRadar:
                Intent viewRadar = new Intent(this, GNSSRadarActivity.class);
                startActivity(viewRadar);
                break;
            case R.id.buttonGNSSDetails:
                Intent viewDetails = new Intent(this, GNSSDetailsActivity.class);
                startActivity(viewDetails);
                break;
        }
    }

    public void setButtons() {
        Button buttonRadar = (Button) findViewById(R.id.buttonGNSSRadar),
                buttonDetails = (Button) findViewById(R.id.buttonGNSSDetails);
        buttonRadar.setOnClickListener(this);
        buttonDetails.setOnClickListener(this);
    }
}
