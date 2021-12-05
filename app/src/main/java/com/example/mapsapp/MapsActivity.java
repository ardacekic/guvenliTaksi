package com.example.mapsapp;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.example.mapsapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,View.OnClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    public Button show_nearestTaxi,call_Taxi_Button,taxi_come_Button;
    protected Double latitude,longitude;
    public boolean clicked = false;
    public String message_intent="Taksi Çağır";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        show_nearestTaxi = findViewById(R.id.nearest_Taxi_Button);
        show_nearestTaxi.setOnClickListener(this);

        call_Taxi_Button = findViewById(R.id.call_Taxi_Button);
        call_Taxi_Button.setText(message_intent);
        call_Taxi_Button.setOnClickListener(this);

        taxi_come_Button = findViewById(R.id.taxi_come_Button);
        taxi_come_Button.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14.0f) );
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
    public void onLocationChanged(Location location) {
        //TODO: ADAM ELLEDİYSE DAHA BOZMA ZEVKİNİ ÖYLE KALSIN ZOOM AQ
        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f) );
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onClick(View v) {
        if(v == show_nearestTaxi){
            if (!clicked){
                Log.i("click","clicked");
                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(latitude, longitude))
                        .strokeWidth(5)
                        //.zIndex(1000)
                        .radius(50) //TODO: BUNU PARAMETRIC YAPICAZ
                        .strokeColor(Color.BLUE));
                //TODO: İSTASYONLARI SORGULAMA EKLENECEK
                clicked = true;
            }else{
                mMap.clear();
                clicked = false;
            }
        }else if (v == call_Taxi_Button){

        }else if(v == taxi_come_Button){
            Log.i("resultintent", "onClick ");
            Intent intent_qrReader = new Intent(this,QrReader.class);
            startActivityForResult(intent_qrReader,1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TAG", "onRestart: ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Log.i("resultCode", "onActivityResult: ");
                String name_node = data.getStringExtra(QrReader.EXTRA_INFO_NODE);
                String name_leaf = data.getStringExtra(QrReader.EXTRA_INFO_LEAF);
                String plate     = data.getStringExtra(QrReader.EXTRA_INFO_PLATE);
                call_Taxi_Button.setText(name_leaf);
                show_nearestTaxi.setText(name_node);
                taxi_come_Button.setText(plate);
                //TODO: FRONT-END HAZIRLANINCA BURAYI RESULT OLARAK DÖNDÜRÜCEZ DEBUG İÇİN YETERLİ
            }else {

            }
        }
    }
}