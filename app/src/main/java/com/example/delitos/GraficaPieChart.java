package com.example.delitos;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GraficaPieChart extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayList<Delitos> listDelitos = new ArrayList<>();
    private PieChart pieChart;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_pie_chart);

        pieChart = (PieChart)findViewById(R.id.pieChart);
        spinner = (Spinner)findViewById(R.id.sp_datosP);
        spinner.setOnItemSelectedListener(this);

        String [] tipos = {"Modalidad", "Sexo", "Edad"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        spinner.setAdapter(adapter);

        try {
            readDelitos();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private void pieSexo() {
        ArrayList<PieEntry> grafica = new ArrayList<>();
        // Recorre toda la lsita
        for (int i = 0; i < getSexo().size(); i++) {
            // Contador de homicidios
            int contador = 0;
            for (int j = 0; j < listDelitos.size(); j++ ) {
                // Si coincide la busqueda aumenta el contador
                if (getSexo().get(i).equals(listDelitos.get(j).getSexo())) {
                    contador += listDelitos.get(j).getD_totales();
                }
            }
            // Añade los datos a la lista
            grafica.add(new PieEntry(contador, getSexo().get(i)));
        }
        pieDis(grafica);

    }

    private void pieEdad() {
        ArrayList<PieEntry> grafica = new ArrayList<>();
        // Recorre toda la lsita
        for (int i = 0; i < getEdad().size(); i++) {
            // Contador de homicidios
            int contador = 0;
            for (int j = 0; j < listDelitos.size(); j++ ) {
                // Si coincide la busqueda aumenta el contador
                if (getEdad().get(i).equals(listDelitos.get(j).getRango_edad())) {
                    contador += listDelitos.get(j).getD_totales();
                }
            }
            // Añade los datos a la lista
            grafica.add(new PieEntry(contador, getEdad().get(i)));
        }
        pieDis(grafica);

    }

    private void pieModalidad() {
        ArrayList<PieEntry> grafica = new ArrayList<>();
        // Recorre toda la lsita
        for (int i = 0; i < getModalidad().size(); i++) {
            // Contador de homicidios
            int contador = 0;
            for (int j = 0; j < listDelitos.size(); j++ ) {
                // Si coincide la busqueda aumenta el contador
                if (getModalidad().get(i).equals(listDelitos.get(j).getModalidad())) {
                    contador += listDelitos.get(j).getD_totales();
                }
            }
            // Añade los datos a la lista
            grafica.add(new PieEntry(contador, getModalidad().get(i)));
        }
        pieDis(grafica);

    }

    private void pieDis (ArrayList<PieEntry> grafica) {
        // Se crea el set de datos con la lista que se lleno
        PieDataSet pieDataSet = new PieDataSet(grafica, "");

        // Color de la grafica de Pie
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        // Color de letras de grafica
        pieDataSet.setValueTextColor(Color.WHITE);
        // Tamaño de letras de grafica
        pieDataSet.setValueTextSize(16f);


        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        // Se deshabilita la descripcion del grafico
        pieChart.getDescription().setEnabled(false);
        // Texto que aparece al centro del Pie
        pieChart.setCenterText("Homicidios");
        // Tamaño del centro
        pieChart.setHoleRadius(30);
        // Tamaño de la sombra del centro
        pieChart.setTransparentCircleRadius(35);
        // eliminar texto en barra de pastel
        pieChart.setDrawSliceText(false);
        // crear animacion
        pieChart.animate();

        // Leyenda de lo que estara escrito
        Legend legend = pieChart.getLegend();
        // leyendas de colores en circulo
        legend.setForm(Legend.LegendForm.LINE);
        // leyendas en el lado horizontal en el centro
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        // leyendas en forma vertical
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position).toString().equals("Modalidad")) {
            pieModalidad();
        } else if (parent.getItemAtPosition(position).toString().equals("Sexo")) {
            pieSexo();
        } else if (parent.getItemAtPosition(position).toString().equals("Edad")) {
            pieEdad();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}