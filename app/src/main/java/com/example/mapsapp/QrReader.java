package com.example.mapsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

public class QrReader extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private TextView  textBox;
    public static final String EXTRA_INFO_NODE= "com.example.mapsapp.EXTRA_INFO_NODE";
    public static final String EXTRA_INFO_LEAF= "com.example.mapsapp.EXTRA_INFO_LEAF";
    public static final String EXTRA_INFO_PLATE= "com.example.mapsapp.EXTRA_INFO_PLATE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_qr_reader);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        textBox = findViewById(R.id.text);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            String name_node = jsonObject.getString("name_node");
                            String name_leaf = jsonObject.getString("name_leaf");
                            String plate = jsonObject.getString("plate");

                            Intent intent = new Intent(getApplicationContext(),RecorderActivity.class);
                            intent.putExtra(EXTRA_INFO_NODE,name_node);
                            intent.putExtra(EXTRA_INFO_LEAF,name_leaf);
                            intent.putExtra(EXTRA_INFO_PLATE,plate);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


}