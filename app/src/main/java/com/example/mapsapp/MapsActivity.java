package com.example.mapsapp;

import static com.example.mapsapp.Constants.URL_GetAllTaksiInfo;
import static com.example.mapsapp.Constants.URL_Request_data;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.example.mapsapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,View.OnClickListener,TaskLoadedCallback {

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
    public boolean Clicked_to_marker,cevapDondu = false;
    public String Taksi_id,lat_user,lon_user,time_user,time_whichSend,Taksi_Status,user_id = "";
    public String taksi_lat="";
    public String taksi_lon="";
    double reS;
    public boolean cevap_evet = false;
    AlertDialog dialog;
    Polyline currentpolyline;
    private MarkerOptions place1, place2;
    String lat_taksi,lon_taksi;
    ArrayList<TaksiData> Taxies_users = new ArrayList<TaksiData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent =getIntent();
        user_id = intent.getStringExtra("user_id");
        call_Taxi_Button = findViewById(R.id.call_Taxi_Button);
        call_Taxi_Button.setText(message_intent);
        call_Taxi_Button.setOnClickListener(this);

        taxi_come_Button = findViewById(R.id.taxi_come_Button);
        taxi_come_Button.setOnClickListener(this);
        taxi_come_Button.setText(user_id);
        if(!cevap_evet)
            startTimer();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //updateMap();

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
            getShortest();
            startAnimationLoader(); // animasyon girsin abi bekleniyor filan yazsın cancel lama özelliğini ekleriz sonra
            closeAllMarkers();
            updateUserGPSTable();
            startAcceptTimer(); //HEM Kabuletme için hem de yeniden çağırabilmek için kullanılcak olan timerı açacak 2şer aralıklarla totalde 10 snye bakılacak
        }else if(v == taxi_come_Button){
            Log.i("resultintent", "onClick ");
            //Intent intent_qrReader = new Intent(this,QrReader.class);

            //startActivity(intent_qrReader);
            place1 = new MarkerOptions().position(new LatLng(39.8004, 32.8106)).title("Location 1");
            place2 = new MarkerOptions().position(new LatLng(39.9004, 32.8106)).title("Location 2");
            String url = (getUrl(place1.getPosition(), place2.getPosition(), "driving"));
            new FetchURL(MapsActivity.this).execute(url, "driving");

        }
    }

    private void getShortest() {

    }

    private void startAnimationLoader() {
        loadingDialog();
    }

    private void closeAllMarkers() {
        //mMap.clear();
    }

    private void startAcceptTimer() {
                //TODO: ALERT VİEW ÇIKAR
        new CountDownTimer(10000, 1000){
            public void onTick(long millisUntilFinished){
                //TODO SORGU AT STATUS BİLGİNİ BAK
                if(!cevapDondu){
                    doesTaksiAccepted();
                }
            }
            public  void onFinish(){
                //Eğer timer sonunda kabul etmemişse markerları yeniden aç
                if(!cevapDondu){
                    //startAcceptTimer();
                    cevapVerilmediPop();
                }

            }
        }.start();
    }

    private void cevapVerilmediPop() {
        dialog.dismiss();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Cevap Verilmedi!")
                .setMessage("Yeni Taksi Ara")
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void popClicled() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Yolculuk Başlıyor")
                .setMessage("Taksin Yolda...")
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //startTimer();
                        getTaksiGpsStatus();
                    }
                })
                .show();
    }
    private void popClicledNeg() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Reddedildin")
                .setMessage("Yeniden Ara")
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void loadingDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.costum_dialog, null));
        builder.setCancelable(true);
        dialog=builder.create();
        dialog.show();

    }
    private void dismissDialog(){
        dialog.dismiss();
    }


    private void doesTaksiAccepted() {
        Log.i("resAL2", time_whichSend);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_Does_Taksi_Accepted, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String message  = jsonObject.getString("error");
                    Log.i("resAL23333", message);
                    Taksi_Status= jsonObject.getJSONObject("message").getString("status");
                    Log.i("resAL233334", Taksi_Status);
                    if(message.equals("false")){
                        Taksi_Status= jsonObject.getJSONObject("message").getString("status");
                        reS = Double.valueOf(Taksi_Status);
                        if (Taksi_Status.equals("2")){
                            cevapDondu=true;
                            dismissDialog();
                            Log.d("arrar",Taksi_Status );
                            cevap_evet=true;
                            popClicled();
                        }else if(Taksi_Status.equals("3")){
                            cevapDondu=true;
                            dismissDialog();
                            Log.d("arrar222",Taksi_Status );
                            cevap_evet=false;
                            popClicledNeg();
                        }else{
                            //bekle aq
                        }
                        Log.i("res", Taksi_Status);
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
                params.put("time",time_whichSend);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateUserGPSTable() {
        time_whichSend = time_user;
        Log.i("resAL", time_whichSend);
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
                params.put("time",time_whichSend);
                params.put("status","1");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void startTimer() {
        new CountDownTimer(20000, 1000){
            public void onTick(long millisUntilFinished){
                if(cevap_evet)
                    onFinish();
                updateMap();
                getShortest();
            }
            public  void onFinish(){
                if(!cevap_evet)
                startTimer();
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
        //TODO: CHANGE THESE OF GEOLOCATİON
        mMap.clear();
        for (TaksiData t: Taxies_users) {
            LatLng first = new LatLng(Double.valueOf(t.getLat()), Double.valueOf(t.getLon()));
            Marker m = mMap.addMarker(new MarkerOptions().position(first).title(t.getPlate_num()));
            m.setTag(t.getId());
            Log.d("taksi_idea",t.getUsername() + t.getLat() + t.getLon());
        }
    }
    private void getTaksiGpsStatus(){
        //get distance between taksi and user in this function with timer!
        mMap.clear();
        getComingTaksiData();
        cevap_evet=true;
        place1 = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(latitude, 32.90)).title("Location 2");
        String url = (getUrl(place1.getPosition(), place2.getPosition(), "driving"));
        new FetchURL(MapsActivity.this).execute(url, "driving");

    }

    private void getComingTaksiData() {
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL_GetComingTaksiInfo, null,
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyD8JD6sSJPMmQRBCV_AgQTc77vN_080vk4";
        Log.d("directionapi",url);
        return url;
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

    @Override
    public void onTaskDone(Object... values) {
        if (currentpolyline != null)
            currentpolyline.remove();
        currentpolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}