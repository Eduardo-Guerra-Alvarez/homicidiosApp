package com.example.delitos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GraficaBarChart extends AppCompatActivity {
    private BarChart barChart;
    private Spinner spinnerM;
    private Spinner spinnerS;
    private Spinner spinnerE;
    private Spinner spinnerTG;
    private Button btn_tipo;
    private ArrayList<Delitos> listDelitos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_bar_chart);

        barChart = (BarChart)findViewById(R.id.barChart);
        spinnerM = (Spinner)findViewById(R.id.sp_modalidad);
        spinnerS = (Spinner)findViewById(R.id.sp_sexo);
        spinnerE = (Spinner)findViewById(R.id.sp_edad);
        spinnerTG = (Spinner)findViewById(R.id.sp_grafica);
        btn_tipo = (Button)findViewById(R.id.btn_tipo);
        barChart.setVisibility(View.INVISIBLE);
        spinnerM.setVisibility(View.INVISIBLE);
        spinnerS.setVisibility(View.INVISIBLE);
        spinnerE.setVisibility(View.INVISIBLE);
        btn_tipo.setVisibility(View.INVISIBLE);

        try {
            readDelitos();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> modalidad = getModalidad();
        ArrayList<String> edad = getEdad();
        ArrayList<String> sexo = getSexo();
        modalidad.add(0,"Todas");
        edad.add(0,"Todas");
        sexo.add(0,"Todas");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modalidad);
        spinnerM.setAdapter(adapter);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexo);
        spinnerS.setAdapter(adapter);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, edad);
        spinnerE.setAdapter(adapter);

        String[] tip_graf = {"Entidad", "Modalidad", "Sexo", "Edad"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tip_graf);
        spinnerTG.setAdapter(adapter);

    }

    public void desactivarElementos () {
        spinnerM.setVisibility(View.INVISIBLE);
        spinnerS.setVisibility(View.INVISIBLE);
        spinnerE.setVisibility(View.INVISIBLE);
        btn_tipo.setVisibility(View.INVISIBLE);
    }

    public void enviarGrafica (View view) {
        Toast.makeText(this, "Cargando...", Toast.LENGTH_LONG).show();
        barChart.setVisibility(View.VISIBLE);
        if (spinnerTG.getSelectedItem().toString().equals("Entidad")) {
            spinnerM.setVisibility(View.VISIBLE);
            spinnerS.setVisibility(View.VISIBLE);
            spinnerE.setVisibility(View.VISIBLE);
            btn_tipo.setVisibility(View.VISIBLE);
        } else if (spinnerTG.getSelectedItem().toString().equals("Modalidad")) {
            desactivarElementos();
            barModalidad();
        } else if (spinnerTG.getSelectedItem().toString().equals("Sexo")) {
            desactivarElementos();
            barSexo();
        } else if (spinnerTG.getSelectedItem().toString().equals("Edad")) {
            desactivarElementos();
            barEdad();
        }
    }

    public void enviarDatos(View view) {
        barChart.setVisibility(View.VISIBLE);
        barEnt();
    }

    private ArrayList<String> getEntidad() {
        Set<String> hs = new HashSet<>();
        for (int i = 0; i < listDelitos.size(); i++) {
            hs.add(listDelitos.get(i).getEntidad());
        }
        ArrayList<String> estados = new ArrayList<>();
        estados.addAll(hs);
        Collections.sort(estados);

        return estados;
    }

    private ArrayList<String> getSexo() {
        Set<String> hs = new HashSet<>();
        for (int i = 0; i < listDelitos.size(); i++) {
            hs.add(String.valueOf(listDelitos.get(i).getSexo()));
        }
        ArrayList<String> sexo = new ArrayList<>();
        sexo.addAll(hs);
        Collections.sort(sexo);

        return sexo;
    }

    private ArrayList<String> getEdad() {
        Set<String> hs = new HashSet<>();
        for (int i = 0; i < listDelitos.size(); i++) {
            hs.add(String.valueOf(listDelitos.get(i).getRango_edad()));
        }
        ArrayList<String> edad = new ArrayList<>();
        edad.addAll(hs);
        Collections.sort(edad);

        return edad;
    }

    private ArrayList<String> getModalidad() {
        Set<String> hs = new HashSet<>();
        for (int i = 0; i < listDelitos.size(); i++) {
            hs.add(String.valueOf(listDelitos.get(i).getModalidad()));
        }
        ArrayList<String> modalidad = new ArrayList<>();
        modalidad.addAll(hs);
        Collections.sort(modalidad);

        return modalidad;
    }

    private void barModalidad () {
        ArrayList<BarEntry> grafica = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < getModalidad().size(); i++) {
            int contador = 0;
            for (int j = 0; j < listDelitos.size(); j++) {
                if (getModalidad().get(i).equals(listDelitos.get(j).getModalidad())) {
                    contador += listDelitos.get(j).getD_totales();
                }
            }
            total += contador;
            grafica.add(new BarEntry(i, contador));
        }
        BarDataSet barDataSet = new BarDataSet(grafica, "Total Homicidios: " + total);
        graficaDis(barDataSet, getModalidad());
    }

    private void barSexo () {
        ArrayList<BarEntry> grafica = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < getSexo().size(); i++) {
            int contador = 0;
            for (int j = 0; j < listDelitos.size(); j++) {
                if (getSexo().get(i).equals(listDelitos.get(j).getSexo())) {
                    contador += listDelitos.get(j).getD_totales();
                }
            }
            total += contador;
            grafica.add(new BarEntry(i, contador));
        }
        BarDataSet barDataSet = new BarDataSet(grafica, "Total Homicidios: " + total);
        graficaDis(barDataSet, getSexo());
    }

    private void barEdad () {
        ArrayList<BarEntry> grafica = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < getEdad().size(); i++) {
            int contador = 0;
            for (int j = 0; j < listDelitos.size(); j++) {
                if (getEdad().get(i).equals(listDelitos.get(j).getRango_edad())) {
                    contador += listDelitos.get(j).getD_totales();
                }
            }
            total += contador;
            grafica.add(new BarEntry(i, contador));
        }
        BarDataSet barDataSet = new BarDataSet(grafica, "Total Homicidios: " + total);
        graficaDis(barDataSet, getEdad());
    }

    private void barEnt() {
        ArrayList<BarEntry> grafica = new ArrayList<>();
        int total = 0;

        if (spinnerM.getSelectedItem().toString().equals("Todas") && spinnerS.getSelectedItem().toString().equals("Todas")
                && spinnerE.getSelectedItem().toString().equals("Todas")) {
            for (int i = 0; i < getEntidad().size(); i++) {
                int contador = 0;
                for (int j = 0; j < listDelitos.size(); j++) {
                    if (getEntidad().get(i).equals(String.valueOf(listDelitos.get(j).getEntidad()))) {
                        contador += listDelitos.get(j).getD_totales();
                    }
                }
                total += contador;
                grafica.add(new BarEntry(i, contador));
            }
        } else if (!spinnerM.getSelectedItem().toString().equals("Todas") && spinnerS.getSelectedItem().toString().equals("Todas")
                && spinnerE.getSelectedItem().toString().equals("Todas")){
            for (int i = 0; i < getEntidad().size(); i++) {
                int contador = 0;
                for (int j = 0; j < listDelitos.size(); j++) {
                    if (getEntidad().get(i).equals(String.valueOf(listDelitos.get(j).getEntidad()))
                            && spinnerM.getSelectedItem().toString().equals(listDelitos.get(j).getModalidad())) {
                        contador += listDelitos.get(j).getD_totales();
                    }
                }
                total += contador;
                grafica.add(new BarEntry(i, contador));
            }
        } else if (spinnerM.getSelectedItem().toString().equals("Todas") && !spinnerS.getSelectedItem().toString().equals("Todas")
                && spinnerE.getSelectedItem().toString().equals("Todas")){
            for (int i = 0; i < getEntidad().size(); i++) {
                int contador = 0;
                for (int j = 0; j < listDelitos.size(); j++) {
                    if (getEntidad().get(i).equals(String.valueOf(listDelitos.get(j).getEntidad()))
                            && spinnerS.getSelectedItem().toString().equals(listDelitos.get(j).getSexo())) {
                        contador += listDelitos.get(j).getD_totales();
                    }
                }
                total += contador;
                grafica.add(new BarEntry(i, contador));
            }
        } else if (spinnerM.getSelectedItem().toString().equals("Todas") && spinnerS.getSelectedItem().toString().equals("Todas")
                && !spinnerE.getSelectedItem().toString().equals("Todas")){
            for (int i = 0; i < getEntidad().size(); i++) {
                int contador = 0;
                for (int j = 0; j < listDelitos.size(); j++) {
                    if (getEntidad().get(i).equals(String.valueOf(listDelitos.get(j).getEntidad()))
                            && spinnerE.getSelectedItem().toString().equals(listDelitos.get(j).getRango_edad())) {
                        contador += listDelitos.get(j).getD_totales();
                    }
                }
                total += contador;
                grafica.add(new BarEntry(i, contador));
            }
        } else if (!spinnerM.getSelectedItem().toString().equals("Todas") && !spinnerS.getSelectedItem().toString().equals("Todas")
                && spinnerE.getSelectedItem().toString().equals("Todas")){
            for (int i = 0; i < getEntidad().size(); i++) {
                int contador = 0;
                for (int j = 0; j < listDelitos.size(); j++) {
                    if (getEntidad().get(i).equals(String.valueOf(listDelitos.get(j).getEntidad()))
                            && spinnerM.getSelectedItem().toString().equals(listDelitos.get(j).getModalidad())
                            && spinnerS.getSelectedItem().toString().equals(listDelitos.get(j).getSexo())) {
                        contador += listDelitos.get(j).getD_totales();
                    }
                }
                total += contador;
                grafica.add(new BarEntry(i, contador));
            }
        } else if (spinnerM.getSelectedItem().toString().equals("Todas") && !spinnerS.getSelectedItem().toString().equals("Todas")
                && !spinnerE.getSelectedItem().toString().equals("Todas")){
            for (int i = 0; i < getEntidad().size(); i++) {
                int contador = 0;
                for (int j = 0; j < listDelitos.size(); j++) {
                    if (getEntidad().get(i).equals(String.valueOf(listDelitos.get(j).getEntidad()))
                            && spinnerS.getSelectedItem().toString().equals(listDelitos.get(j).getSexo())
                            && spinnerE.getSelectedItem().toString().equals(listDelitos.get(j).getRango_edad())) {
                        contador += listDelitos.get(j).getD_totales();
                    }
                }
                total += contador;
                grafica.add(new BarEntry(i, contador));
            }
        } else if (!spinnerM.getSelectedItem().toString().equals("Todas") && spinnerS.getSelectedItem().toString().equals("Todas")
                && !spinnerE.getSelectedItem().toString().equals("Todas")){
            for (int i = 0; i < getEntidad().size(); i++) {
                int contador = 0;
                for (int j = 0; j < listDelitos.size(); j++) {
                    if (getEntidad().get(i).equals(String.valueOf(listDelitos.get(j).getEntidad()))
                            && spinnerM.getSelectedItem().toString().equals(listDelitos.get(j).getModalidad())
                            && spinnerE.getSelectedItem().toString().equals(listDelitos.get(j).getRango_edad())) {
                        contador += listDelitos.get(j).getD_totales();
                    }
                }
                total += contador;
                grafica.add(new BarEntry(i, contador));
            }
        } else if (!spinnerM.getSelectedItem().toString().equals("Todas") && !spinnerS.getSelectedItem().toString().equals("Todas")
                && !spinnerE.getSelectedItem().toString().equals("Todas")){
            for (int i = 0; i < getEntidad().size(); i++) {
                int contador = 0;
                for (int j = 0; j < listDelitos.size(); j++) {
                    if (getEntidad().get(i).equals(String.valueOf(listDelitos.get(j).getEntidad()))
                            && spinnerM.getSelectedItem().toString().equals(listDelitos.get(j).getModalidad())
                            && spinnerS.getSelectedItem().toString().equals(listDelitos.get(j).getSexo())
                            && spinnerE.getSelectedItem().toString().equals(listDelitos.get(j).getRango_edad())) {
                        contador += listDelitos.get(j).getD_totales();
                    }
                }
                total += contador;
                grafica.add(new BarEntry(i, contador));
            }
        }
        BarDataSet barDataSet = new BarDataSet(grafica, "Total Homicidios: " + total);
        graficaDis(barDataSet, getEntidad());
    }

    public void graficaDis (BarDataSet barDataSet, ArrayList<String> nombre) {

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(20f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.invalidate();
        barChart.getDescription().setEnabled(false);
        barChart.animateY(2000);
        barChart.getLegend().setEnabled(false);
        XAxis xaxis = barChart.getXAxis();
        xaxis.setGranularityEnabled(true);
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setValueFormatter(new IndexAxisValueFormatter(nombre));

        YAxis yaxis = barChart.getAxisLeft();
        yaxis.setAxisMinimum(20f);
        yaxis.setAxisMinimum(0);
        yaxis = barChart.getAxisRight();
        yaxis.setEnabled(false);
    }

    public void readDelitos() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.homicidios2020);
        BufferedReader reader =  new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";
        try {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                // Separar por comas
                String [] tokens = line.split(",");

                // leer el fichero
                Delitos delitos = new Delitos();
                delitos.setAño(Integer.parseInt(tokens[0]));
                delitos.setClave_ent(Integer.parseInt(tokens[1]));
                delitos.setEntidad(tokens[2]);
                delitos.setBja(tokens[3]);
                delitos.setT_delito(tokens[4]);
                delitos.setSt_delito(tokens[5]);
                delitos.setModalidad(tokens[6]);
                delitos.setSexo(tokens[7]);
                delitos.setRango_edad(tokens[8]);
                delitos.setEnero(Integer.parseInt(tokens[9]));
                delitos.setFebrero(Integer.parseInt(tokens[10]));
                delitos.setMarzo(Integer.parseInt(tokens[11]));
                delitos.setAbril(Integer.parseInt(tokens[12]));
                delitos.setMayo(Integer.parseInt(tokens[13]));
                if (tokens[14].length() > 0) {
                    delitos.setJunio(Integer.parseInt(tokens[14]));

                } else {

                }
                if (tokens[15].length() > 0) {
                    delitos.setJulio(Integer.parseInt(tokens[15]));
                } else {
                    delitos.setJulio(0);
                }
                if (tokens[16].length() > 0) {
                    delitos.setAgosto(Integer.parseInt(tokens[16]));
                } else {
                    delitos.setAgosto(0);
                }
                if (tokens[17].length() > 0) {
                    delitos.setSeptiembre(Integer.parseInt(tokens[17]));
                } else {
                    delitos.setSeptiembre(0);
                }
                if (tokens[18].length() > 0) {
                    delitos.setOctubre(Integer.parseInt(tokens[18]));
                } else {
                    delitos.setOctubre(0);
                }
                if (tokens[19].length() > 0) {
                    delitos.setNoviembre(Integer.parseInt(tokens[19]));
                } else {
                    delitos.setNoviembre(0);
                }
                if (tokens[20].length() > 0) {
                    delitos.setDiciembre(Integer.parseInt(tokens[20]));
                } else {
                    delitos.setDiciembre(0);
                }
                if (tokens[21].length() > 0) {
                    delitos.setD_totales(Integer.parseInt(tokens[21]));
                } else {
                    delitos.setD_totales(0);
                }


                listDelitos.add(delitos);
                Log.d("Activity Bar Chart", "This: " + delitos );
            }
        } catch (IOException e) {
            Log.wtf("My activity", "Error reading data file on line " + line);
            e.printStackTrace();
        }
    }
}