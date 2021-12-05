package com.example.mapsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class Pop extends AppCompatActivity implements View.OnClickListener{
    private Button ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.647),(int)(height*.4));
        ok = findViewById(R.id.button);
        ok.setOnClickListener(this);
        //TODO:CLOSE ON TOUCH OLDUĞUNDA DA MAIN EKRANA GİTMESİNİ SAĞLA!

    }

    @Override
    public void onClick(View v) {
        if(v == ok){
            Intent back_to_main = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(back_to_main);
        }
    }
}