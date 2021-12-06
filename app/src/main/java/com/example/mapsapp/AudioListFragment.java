package com.example.mapsapp;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudioListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioListFragment extends Fragment implements AudioListAdapter.onItemListClick {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView audioList;
    private File[] allFiles;
    private AudioListAdapter audioListAdapter;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private  File filetoPlay;

    private ImageButton playBtn;
    private TextView playerHeader,playerFilename;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AudioListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AudioListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AudioListFragment newInstance(String param1, String param2) {
        AudioListFragment fragment = new AudioListFragment();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO: KAYBOLMAYA ÇÖZÜM :https://www.youtube.com/watch?v=DCY4N6fe95s&ab_channel=TVACStudio, PART5

        playBtn = view.findViewById(R.id.player_play_button);
        playerHeader= view.findViewById(R.id.player_header_title);
        playerFilename = view.findViewById(R.id.player_file_name);
        audioList = view.findViewById(R.id.android_list_view);
        String path =getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        audioListAdapter = new AudioListAdapter(allFiles,this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioListAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onClickListener(File file, int position) {
        Log.d("Play LOG","File Playing : " + file.getName());
        if(isPlaying){
            stopAudio();
            isPlaying = false;
            playAudio(filetoPlay);

        }else{
            filetoPlay = file;
            playAudio(filetoPlay);
            isPlaying = true;
        }
    }

    private void stopAudio() {
        //TODO: PLAY BUTTON OYNAR OLARAK DEĞIŞTIR KERDEEEŞ > OLARAK
        playerHeader.setText("STOP");
        isPlaying = false;
        mediaPlayer.stop();
    }

    private void playAudio(File filetoPlay) {

        mediaPlayer = new MediaPlayer();
        //TODO: botomsheetbehvour
        try {
            mediaPlayer.setDataSource(filetoPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO: PLAY BUTTON RENGINI FILAN DEGISTIR || OLARAK DEĞİŞTIR!
        playerFilename.setText(filetoPlay.getName());
        playerHeader.setText("PLAYING...");
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                playerHeader.setText("FINISHED");
            }
        });
    }
}