package com.example.mapsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener {
    private Button login_button,signin_button;
    private EditText username_text,password_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

        signin_button = findViewById(R.id.signin_button);
        signin_button.setOnClickListener(this);

        username_text = findViewById(R.id.username_text);
        password_text = findViewById(R.id.password_text);
    }

    private void openRegisterScreen() {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }
    private void openUserMainScreen() {
        Intent intent_userscreen = new Intent(this,MapsActivity.class);
        startActivity(intent_userscreen);
    }
    private void loginUser() {
        final String username = username_text.getText().toString().trim();
        final String password = password_text.getText().toString().trim();
        Log.i("loginuser",Constants.URL_REGISTER);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String message  = jsonObject.getString("error");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if(message.equals("false")){
                        openUserMainScreen();
                    }else{
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
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
                params.put("username",username);
                params.put("password",password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
    @Override
    public void onClick(View v) {
        if(v == login_button){;
            Log.i("click","clicked");
            //TODO LOGIN HANDSHAKE WILL BE HERE
            loginUser();
        }else if(v == signin_button){
            openRegisterScreen();
        }
    }
}
