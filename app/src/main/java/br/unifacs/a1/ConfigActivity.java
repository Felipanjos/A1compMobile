
package br.unifacs.a1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences dados;
    SharedPreferences.Editor editorDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dados = getSharedPreferences("Config", Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
            setElements();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonConfigNavegacao:
                startMap();
                break;
            case R.id.buttonConfigSalvar:
                saveElements();
                break;
            case R.id.buttonConfigInicio:
                startMain();
                break;
        }
    }

    public void setButton(Map<String, Button> button) {
        button.get("buttonConfigVoltar").setOnClickListener(this);
        button.get("buttonConfigSalvar").setOnClickListener(this);
        button.get("buttonConfigNavegacao").setOnClickListener(this);
    }

    public void setSpinner(Map<String, Spinner> spinner) {

        Spinner spinnerFormato = spinner.get("spinnerFormato"),
                spinnerOrientacao = spinner.get("spinnerOrientacao");
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

        spinnerFormato.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, formatos));
        spinnerFormato.setSelection(findSpinner(spinnerFormato, dados.getString("Formato", formato1)));
        spinnerOrientacao.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, orientacoes));
        spinnerOrientacao.setSelection(findSpinner(spinnerOrientacao, dados.getString("Orientacao", orientacao1)));
    }

    public void saveSpinner(Map <String, Spinner> spinner) {

        editorDados = dados.edit();

        if (editorDados != null) {
            editorDados.putString("Formato", spinner.get("spinnerFormato").getSelectedItem().toString());
            editorDados.putString("Orientacao", spinner.get("spinnerOrientacao").getSelectedItem().toString());
            editorDados.commit();
        }
    }

    public int findSpinner(Spinner spinner, String texto) {

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(texto)) {
                return i;
            }
        }
        return 0;
    }

    public void setSwitch(SwitchCompat interruptor) {
        interruptor.setChecked((dados.getBoolean("Mostrar trafego", false)));
    }

    public void saveSwitch(SwitchCompat interruptor) {

        editorDados = dados.edit();
            editorDados.putBoolean("Mostrar trafego", interruptor.isChecked());
        editorDados.commit();
    }

    public void setRadio(Map<String, RadioButton> radio) {

        if (!radio.get("radioKm").isChecked() && !radio.get("radioMph").isChecked())
            radio.get("radioKm").setChecked(true);
        if (!radio.get("radioVetorial").isChecked() && !radio.get("radioSatelite").isChecked())
            radio.get("radioVetorial").setChecked(true);

        radio.get("radioKm").setChecked(dados.getBoolean("Km", true));
        radio.get("radioMph").setChecked(dados.getBoolean("Mph", false));
        radio.get("radioVetorial").setChecked(dados.getBoolean("Vetorial", true));
        radio.get("radioSatelite").setChecked(dados.getBoolean("Satelite", false));
    }

    public void saveRadio(Map<String, RadioButton> radio) {

        editorDados = dados.edit();
            editorDados.putBoolean("Km", radio.get("radioKm").isChecked());
            editorDados.putBoolean("Mph", radio.get("radioMph").isChecked());
            editorDados.putBoolean("Vetorial", radio.get("radioVetorial").isChecked());
            editorDados.putBoolean("Satelite", radio.get("radioSatelite").isChecked());
        editorDados.commit();
    }

    public void operacao(String opcao) {

        Map<String, Button> buttonMap = new HashMap<>();
            buttonMap.put("buttonConfigVoltar", (Button) findViewById(R.id.buttonConfigInicio));
            buttonMap.put("buttonConfigNavegacao", (Button) findViewById(R.id.buttonConfigNavegacao));
            buttonMap.put("buttonConfigSalvar", (Button) findViewById(R.id.buttonConfigSalvar));

        Map<String, RadioButton> radioButtonMap = new HashMap<>();
            radioButtonMap.put("radioKm", (RadioButton) findViewById(R.id.radioKm));
            radioButtonMap.put("radioMph", (RadioButton) findViewById(R.id.radioMph));
            radioButtonMap.put("radioVetorial", (RadioButton) findViewById(R.id.radioVetorial));
            radioButtonMap.put("radioSatelite", (RadioButton) findViewById(R.id.radioSatelite));

        Map<String, Spinner> spinnerMap = new HashMap<>();
            spinnerMap.put("spinnerFormato", (Spinner) findViewById(R.id.spinnerFormat));
            spinnerMap.put("spinnerOrientacao", (Spinner) findViewById(R.id.spinnerMap));

        SwitchCompat infoTrafego = (SwitchCompat) findViewById(R.id.infoTrafego);

        switch (opcao) {
            case "setButton":
                setButton(buttonMap);
                break;
            case "setSpinner":
                setSpinner(spinnerMap);
                break;
            case "setRadio":
                setRadio(radioButtonMap);
                break;
            case "setSwitch":
                setSwitch(infoTrafego);
                break;
            case "saveSpinner":
                saveSpinner(spinnerMap);
                break;
            case "saveRadio":
                saveRadio(radioButtonMap);
                break;
            case "saveSwitch":
                saveSwitch(infoTrafego);
                break;
        }
    }

    private void setElements() {
        operacao("setSwitch");
        operacao("setSpinner");
        operacao("setRadio");
        operacao("setButton");
    }

    private void saveElements() {
        operacao("saveSpinner");
        operacao("saveSwitch");
        operacao("saveRadio");
        Toast.makeText(this, getResources().getString(R.string.msgSalvar), Toast.LENGTH_SHORT).show();
    }

    private void startMap() {
        Intent viewMapa = new Intent(this, MapsActivity.class);
        startActivity(viewMapa);
    }

    private void startMain() {
        Intent viewInicio = new Intent(this, MainActivity.class);
        startActivity(viewInicio);
    }
}