
package br.unifacs.a1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences dados;
    SharedPreferences.Editor editorDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dados = getSharedPreferences("Config", Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        setSwitch();
        setButton();
        setRadio();
        setSpinner();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buttonConfigNavegacao:
                Intent view_mapa = new Intent(this, NavegacaoActivity.class);
                startActivity(view_mapa);
                finish();
                break;
            case R.id.buttonConfigSalvar:
                saveSpinner();
                saveSwitch();
                saveRadio();
                Toast.makeText(this, getResources().getString(R.string.msgSalvar), Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonConfigVoltar:
                finish();
                break;
        }
    }

    private void setButton() {
        Button buttonConfigVoltar = (Button) findViewById(R.id.buttonConfigVoltar),
               buttonConfigNavegacao = (Button) findViewById(R.id.buttonConfigNavegacao),
               buttonConfigSalvar = (Button) findViewById(R.id.buttonConfigSalvar);
        buttonConfigVoltar.setOnClickListener(this);
        buttonConfigNavegacao.setOnClickListener(this);
        buttonConfigSalvar.setOnClickListener(this);
    }

    private void setSpinner() {

        Spinner spinnerFormato = (Spinner) findViewById(R.id.spinnerFormat),
                spinnerOrientacao = (Spinner) findViewById(R.id.spinnerMap);
        // Labels do spinner
        String formato1 = getResources().getString(R.string.labelGrau1),
                formato2 = getResources().getString(R.string.labelGrau2),
                formato3 = getResources().getString(R.string.labelGrau3),
                orientacao1 = getResources().getString(R.string.labelMap1),
                orientacao2 = getResources().getString(R.string.labelMap2),
                orientacao3 = getResources().getString(R.string.labelMap3);

        // Preenchimento do spinner
        String[] formatos = new String[]{formato1, formato2, formato3},
                orientacoes = new String[]{orientacao1, orientacao2, orientacao3};

        spinnerFormato.setAdapter(new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, formatos));
        spinnerOrientacao.setAdapter(new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, orientacoes));

        spinnerFormato.setSelection(posicaoOpcao(spinnerFormato,
                dados.getString("Formato", formato1)));

        spinnerOrientacao.setSelection(posicaoOpcao(spinnerOrientacao,
                dados.getString("Orientacao", orientacao1)));
    }

    private void saveSpinner() {
        Spinner spinnerFormato = (Spinner) findViewById(R.id.spinnerFormat),
                spinnerOrientacao = (Spinner) findViewById(R.id.spinnerMap);
        // Persistência
        editorDados = dados.edit();
        String opcaoFormato = spinnerFormato.getSelectedItem().toString(),
               opcaoOrientacao = spinnerOrientacao.getSelectedItem().toString();

        if (editorDados != null) {
            editorDados.putString("Formato", opcaoFormato);
            editorDados.putString("Orientacao", opcaoOrientacao);
            editorDados.commit();
        }
    }

    private int posicaoOpcao(Spinner spinner, String texto) {

        for (int i = 0; i < spinner.getCount(); i++) {
            if(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(texto)) {
                return i;
            }
        }
        return 0;
    }

    private void setSwitch() {
        Switch infoTrafego = (Switch) findViewById(R.id.infoTrafego);
        infoTrafego.setChecked((dados.getBoolean("Mostrar trafego", false)));
    }

    private void saveSwitch() {

        editorDados = dados.edit();
        Switch infoTrafego = (Switch) findViewById(R.id.infoTrafego);

        if (infoTrafego.isChecked()) {
            editorDados.putBoolean("Mostrar trafego", true);
            editorDados.commit();
        }
        else if (!infoTrafego.isChecked()) {
            editorDados.putBoolean("Mostrar trafego", false);
            editorDados.commit();
        }
    }

    private void setRadio() {
        RadioButton radioKm = (RadioButton) findViewById(R.id.radioKm),
                    radioMph = (RadioButton) findViewById(R.id.radioMph),
                    radioVetorial = (RadioButton) findViewById(R.id.radioVetorial),
                    radioSatelite = (RadioButton) findViewById(R.id.radioSatelite);

        if (!radioKm.isChecked() && !radioMph.isChecked())
            radioKm.setChecked(true);
        if (!radioVetorial.isChecked() && !radioSatelite.isChecked())
            radioVetorial.setChecked(true);

        radioKm.setChecked(dados.getBoolean("Km", true));
        radioMph.setChecked(dados.getBoolean("Mph", false));
        radioVetorial.setChecked(dados.getBoolean("Vetorial", true));
        radioSatelite.setChecked(dados.getBoolean("Satelite", false));
    }

    private void saveRadio() {

        RadioButton radioKm = (RadioButton) findViewById(R.id.radioKm),
                radioMph = (RadioButton) findViewById(R.id.radioMph),
                radioVetorial = (RadioButton) findViewById(R.id.radioVetorial),
                radioSatelite = (RadioButton) findViewById(R.id.radioSatelite);

        editorDados = dados.edit();
        editorDados.putBoolean("Km", radioKm.isChecked());
        editorDados.putBoolean("Mph", radioMph.isChecked());
        editorDados.putBoolean("Vetorial", radioVetorial.isChecked());
        editorDados.putBoolean("Satelite", radioSatelite.isChecked());
        editorDados.commit();
    }
}