package com.example.delitos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public List<Delitos> listDelitos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void barras(View view) {
        Intent i = new Intent(this, GraficaBarChart.class);
        startActivity(i);
    }

    public void pie(View view) {
        Intent i = new Intent(this, GraficaPieChart.class);
        startActivity(i);
    }



}