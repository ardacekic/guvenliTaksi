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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity implements View.OnClickListener {
    private EditText username_EditText;
    private EditText password_EditText;
    private EditText email_EditText;
    private Button registerButton;
    boolean response = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username_EditText = findViewById(R.id.username_edittext);
        password_EditText = findViewById(R.id.password_edittext);
        email_EditText    = findViewById(R.id.email_edittext);
        registerButton    = findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
    }
    private void registerUser() {
        final String username = username_EditText.getText().toString().trim();
        final String password = password_EditText.getText().toString().trim();
        final String email    = email_EditText.getText().toString().trim();
        Log.i("registeruser",Constants.URL_REGISTER);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    Log.i("json",response.toString());
                    String message  = jsonObject.getString("error");
                    if(message.equals("false")){
                        startActivity(new Intent(getApplicationContext(),Pop.class));
                        //backtoMain();
                    }else{
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                params.put("email",email);
                params.put("password",password);
                Log.i("params",params.get("email"));
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if(v == registerButton){
            registerUser();
            Log.i("click","clicked");
        }
    }

    private void backtoMain() {
        Intent back_to_main = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(back_to_main);
    }

}