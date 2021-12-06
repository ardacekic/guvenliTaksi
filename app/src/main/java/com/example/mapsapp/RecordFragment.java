package com.example.mapsapp;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSON_CODE = 600;
    private NavController navController;
    private ImageView list_btn;
    private ImageButton record_btn;
    private boolean isRecording = false;
    private String recordPermisson = Manifest.permission.RECORD_AUDIO;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer recordTimer;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    private FragmentManager getSupportFragmentManager() {
        return null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        list_btn = view.findViewById(R.id.record_list_ptn);
        list_btn.setOnClickListener(this);
        record_btn = view.findViewById(R.id.record_btn);
        record_btn.setOnClickListener(this);
        recordTimer = view.findViewById(R.id.record_timer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_list_ptn:
                navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                break;
            case R.id.record_btn:
                if(isRecording){
                    //TODO YANMAYAN MIC ILE DEGISTIRME FONCSIYONU GELCEK CUNKU RECORDAN ÇIKILACAK
                    stopRecording();
                    isRecording = false;
                }else{
                    if(checkPermisson()){
                        startRecording();
                        //TODO YANAR DONERLI MIC ILE DEGISTIRME FONCSIYONU GELCEK CUNKU RECORDA BAŞLANDI
                        isRecording = true;
                    }
                }
                break;
        }
    }

    private void startRecording() {
        recordTimer.setBase(SystemClock.elapsedRealtime());
        recordTimer.start();
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
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

    private boolean checkPermisson() {
        if(ActivityCompat.checkSelfPermission(getContext(),recordPermisson) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermisson},PERMISSON_CODE);
            return false;
        }

    }

}