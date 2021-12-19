package com.example.mapsapp;

import static com.example.mapsapp.Constants.URL_GetAllTaksiInfo;
import static com.example.mapsapp.Constants.URL_Request_data;

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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.example.mapsapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,View.OnClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    public Button show_nearestTaxi,call_Taxi_Button,taxi_come_Button;
    protected Double latitude,longitude;
    private TextView filter;
    public boolean clicked = false;
    public String message_intent="Taksi Çağır";
    public String Clicked_Taxi = "";
    public boolean Clicked_to_marker = false;
    public String Taksi_id,lat_user,lon_user,time_user = "";
    ArrayList<TaksiData> Taxies_users = new ArrayList<TaksiData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        call_Taxi_Button = findViewById(R.id.call_Taxi_Button);
        call_Taxi_Button.setText(message_intent);
        call_Taxi_Button.setOnClickListener(this);

        taxi_come_Button = findViewById(R.id.taxi_come_Button);
        taxi_come_Button.setOnClickListener(this);
        startTimer();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMap();
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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Clicked_to_marker = true;
                //TODO: YEŞİL YANIP SÖNEN BİŞİ YAP Kİ ANLAYALIM TAKSİYİ İSTİYOZ >> TAKSİ ÇAĞIR BUTONUNU
                //TODO: TAKSİ SİMGESİNİ Bİ TIK BÜYÜTEBİLİRİZ HER TIKLANANA
                String markerTitle = marker.getTitle();
                Taksi_id = marker.getTag().toString();
                Log.i("MarkerClick", markerTitle);
                Clicked_Taxi = markerTitle;
                return false;
            }
        });
    }
    @Override
    public void onLocationChanged(Location location) {
        //TODO: ADAM ELLEDİYSE DAHA BOZMA ZEVKİNİ ÖYLE KALSIN ZOOM AQ
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
        lat_user = latitude.toString();
        lon_user = longitude.toString();
        time_user = String.valueOf((location.getTime()));
        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f) );
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
                //TODO: ADAMIN VALİD BİTİNİ 0 A ÇEK MİLLET GÖRMESİN
                //TODO: Taksici uygulması kendi bitini 0 gördüğünde ekrana kullanıcın gps noktası gelsin. Kabul ederse accepted biti kullanıcının 1 olsun etmezse 0 olsun
                //TODO: Kullanıcı travel bilgilerini tutacak bir table oluşturmak gerek
            startAnimationLoader(); // animasyon girsin abi bekleniyor filan yazsın cancel lama özelliğini ekleriz sonra
            closeAllMarkers();
            updateTaksiTable();
            updateUserGPSTable();
            startAcceptTimer(); //HEM Kabuletme için hem de yeniden çağırabilmek için kullanılcak olan timerı açacak 2şer aralıklarla totalde 10 snye bakılacak
        }else if(v == taxi_come_Button){
            Log.i("resultintent", "onClick ");
            Intent intent_qrReader = new Intent(this,QrReader.class);
            startActivity(intent_qrReader);
        }
    }

    private void startAnimationLoader() {
    }

    private void closeAllMarkers() {
        mMap.clear();
    }

    private void startAcceptTimer() {
        new CountDownTimer(10000, 2000){
            public void onTick(long millisUntilFinished){
                closeAllMarkers(); // sürekli temizle userlar görmesin elaman çağırılırken
                //Her 2 sn de bir taksici kabuletmiş mi bak
                //taksici kabul ederse TAKSİ YOLDA MESAJIMIZ OLSUN! VE Taksi Çağır butonumuz inaktive olsun.
            }
            public  void onFinish(){
                //Eğer timer sonunda kabul etmemişse markerları yeniden aç

            }
        }.start();
    }

    private void updateUserGPSTable() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Request_data, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String message  = jsonObject.getString("error");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if(message.equals("false")){

                    }else{
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error","psam error");
                Log.i("error2",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("taksi_id",Taksi_id);
                params.put("user_id","1");
                params.put("lat",lat_user);
                params.put("lon",lon_user);
                params.put("time",time_user);
                params.put("status","1");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
    private void updateTaksiTable() {
    }

    private void startTimer() {
        new CountDownTimer(20000, 1000){
            public void onTick(long millisUntilFinished){

            }
            public  void onFinish(){
                updateMap(); startTimer();
            }
        }.start();
    }

    private void updateMap() {
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, URL_GetAllTaksiInfo, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            // display response

                            try {
                                Log.d("Response", (response.getString("message")).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                String message = response.getString("message").toString();
                                Type type = new TypeToken<ArrayList<TaksiData>>(){}.getType();
                                Taxies_users = new Gson().fromJson(message,type);
                                showOnMap();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("taksi_user",Taxies_users.get(1).getUsername());

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error.Response", "response");
                        }
                    }
            );
        RequestHandler.getInstance(this).addToRequestQueue(getRequest);
    }

    private void showOnMap() {
        for (TaksiData t: Taxies_users) {
            LatLng first = new LatLng(Double.valueOf(t.getLat()), Double.valueOf(t.getLon()));
            Marker m = mMap.addMarker(new MarkerOptions().position(first).title(t.getPlate_num()));
            m.setTag(t.getId());
            Log.d("taksi_idea",t.getUsername() + t.getLat() + t.getLon());
        }
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
                filter.setVisibility(View.INVISIBLE);
            }else {

            }
        }
    }
}