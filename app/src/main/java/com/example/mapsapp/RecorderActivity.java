package com.example.mapsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecorderActivity extends AppCompatActivity  implements OnMapReadyCallback,LocationListener,View.OnClickListener{
    private GoogleMap mMap;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Double latitude,longitude;
    private String node_name,leaf_name,plate_name;
    //private ImageButton record_btn;
    private boolean isRecording = false;
    private String recordPermisson = Manifest.permission.RECORD_AUDIO;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer recordTimer;
    private TextView node,leaf,plate;
    private Button list;
    boolean nowRecording = false;
    private Button record_btn;
    LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        node = findViewById(R.id.node_name);
        leaf = findViewById(R.id.leaf_name);
        plate = findViewById(R.id.plate_name);
        list  = findViewById(R.id.go_records);

        Intent intent = getIntent();
        node_name  = intent.getStringExtra(QrReader.EXTRA_INFO_NODE);
        leaf_name  = intent.getStringExtra(QrReader.EXTRA_INFO_LEAF);

        plate_name = intent.getStringExtra(QrReader.EXTRA_INFO_PLATE);

        record_btn = findViewById(R.id.record_btn);
        record_btn.setOnClickListener(this);
        list = findViewById(R.id.go_records);
        list.setOnClickListener(this);
        recordTimer = findViewById(R.id.record_timer);
        lottie = findViewById(R.id.lottie_recording);

        node.setText(node_name);
        leaf.setText(leaf_name);
        plate.setText(plate_name);
        checkPermisson();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        //TODO: ADAM ELLEDİYSE DAHA BOZMA ZEVKİNİ ÖYLE KALSIN ZOOM AQ2
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f) );
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_btn:
                if(isRecording){
                    //TODO YANMAYAN MIC ILE DEGISTIRME FONCSIYONU GELCEK CUNKU RECORDAN ÇIKILACAK
                    stopRecording();
                    lottie.cancelAnimation();
                    isRecording = false;
                }else{
                    checkPermisson();
                    //TODO IF Lİ YAP

                    lottie.playAnimation();
                    startRecording();
                    //TODO YANAR DONERLI MIC ILE DEGISTIRME FONCSIYONU GELCEK CUNKU RECORDA BAŞLANDI
                    isRecording = true;

                }
                break;
            case R.id.go_records:
                if(isRecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isRecording = false;
                            stopRecording();
                            //TODO GO TO FILE MANAGER
                            Intent intent = new Intent(getApplicationContext(),FileManager.class);
                            startActivity(intent);

                        }
                    });
                    alertDialog.setNegativeButton("Hayır",null);
                    alertDialog.setTitle("Kayıt Halen Sürmekte");
                    alertDialog.setMessage("Kayıtı Durdurmak İster Misiniz?");
                    alertDialog.create().show();
                }else{

                }
                break;
        }
    }
    private void startRecording() {
        recordTimer.setBase(SystemClock.elapsedRealtime());
        recordTimer.start();
        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        Log.d("record_Path",recordPath);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        recordFile = "Record_" + formatter.format(now) + ".3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }
    private void stopRecording() {
        recordTimer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void checkPermisson() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 600);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 600) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording)
            stopRecording();
    }
}