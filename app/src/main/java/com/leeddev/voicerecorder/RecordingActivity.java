package com.leeddev.voicerecorder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

;

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener,
        AudioListAdapter.onItemListClickbabu {
    private ImageView recordBtn;

    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer timer;
    RelativeLayout startRecording, recordingInProgress;
    private RecyclerView audioList;
    private File[] allFiles;
    private AudioListAdapter audioListAdapter;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private File fileToPlay;
    private ImageView playBtn;
    private TextView playerHeader, playerFilename;
    public String totalTime = "";
    private SeekBar playerseekBar;

    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    public String recordingName = "";
    ImageView settings;
    ImageButton save;
    Toolbar toolbar;
    Dialog dialog;
    ImageButton pause_resume;
    ImageButton resume;
    AdView ad_view;
    public  static boolean isStopped = false;

    public static boolean isresumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
ad_view= findViewById(R.id.ads_view);
        startRecording = findViewById(R.id.startRecordingParent);

        save=findViewById(R.id.btn_save);
        recordingInProgress = findViewById(R.id.recordingStartedParentView);
        recordBtn = findViewById(R.id.btn_start);
        pause_resume = (ImageButton) findViewById(R.id.btn_pause_resume);
        timer = findViewById(R.id.timer);
        recordBtn.setOnClickListener(this);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.player_sheet);
        // logic to diplay recordings
        audioList = findViewById(R.id.audio_list_view);
        playerFilename = findViewById(R.id.player_filename);
        playerseekBar = findViewById(R.id.player_seekBar);
        String path = RecordingActivity.this.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        audioListAdapter = new AudioListAdapter(allFiles, this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(RecordingActivity.this));
        audioList.setAdapter(audioListAdapter);
        toolbar = findViewById(R.id.toolbar);
        settings = findViewById(R.id.settingsAction);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordingActivity.this, SettingActivity.class);
                startActivity(intent);

            }
        });

        pause_resume.findViewById(R.id.btn_pause_resume).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (isresumed)
                {
                    resumeRecording();

                    pause_resume.setImageResource(R.drawable.pause1);
                    isresumed = false;


                }
                else {
                    isresumed=true;
                    pauseRecording();
                    isRecording = false;

                pause_resume.setImageResource(R.drawable.play1);
                }
                /////////
//                isresumed = true;
//                pauseRecording();
//                isRecording = false;
//                startRecording.setEnabled(true);
//                stopBtn.setEnabled(false);
//                showSaveRecordingDialogBox();
            }
        });

//        pause_resume.findViewById(R.id.btn_pause_resume).setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View view) {
//////                alertDialog.dismiss();
//        isresumed= true;
//                pauseRecording();
//                timer.stop();
//                isRecording= false;
//                pause_resume.setImageResource(R.drawable.play);
//
//            }
//        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                alertDialog.dismiss();
                stopRecording();

            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener(){
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        ad_view = findViewById(R.id.ads_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_view.loadAd(adRequest);
    }





    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_start:
                if (checkPermissions()) {
                    startRecording();
                    isRecording = true;
                    Toast.makeText(RecordingActivity.this, "Recording Started", Toast.LENGTH_SHORT).show();
                    startRecording.setVisibility(View.GONE);
                    recordingInProgress.setVisibility(View.VISIBLE);
                }
                break;

//            case R.id.btn_stop:
                //pause recording
//                pauseRecording();
//                isRecording = false;
//                startRecording.setEnabled(true);
//                stopBtn.setEnabled(false);
//                showSaveRecordingDialogBox();
//                recordingInProgress.setVisibility(View.GONE);
//                startRecording.setVisibility(View.VISIBLE);
//                break;
        }
    }


    @SuppressLint("NewApi")
    private void startRecording() {

        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        String recordPath = RecordingActivity.this.getExternalFilesDir("/").getAbsolutePath();
        recordFile = "Recording123" + formatter.format(now) + ".3gp";
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
        timer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        Toast.makeText(getApplicationContext(), "Recording Saved", Toast.LENGTH_SHORT).show();
        recordingInProgress.setVisibility(View.GONE);
        startRecording.setVisibility(View.VISIBLE);

        String path = RecordingActivity.this.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        audioListAdapter = new AudioListAdapter(allFiles, this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(RecordingActivity.this));
        audioList.setAdapter(audioListAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pauseRecording() {
        pause_resume.setImageResource(R.drawable.play1);
        mediaRecorder.pause();
        timer.stop();
        Toast.makeText(getApplicationContext(), "Recording Paused", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeRecording() {
        timer.start();
        pause_resume.setImageResource(R.drawable.pause1);
        mediaRecorder.resume();

        Toast.makeText(getApplicationContext(), "Recording Resumed", Toast.LENGTH_SHORT).show();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(RecordingActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(RecordingActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 21);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //code removed to avoid crash for null mediarecorder
        if (isRecording && mediaRecorder!=null) {
            stopRecording();
        }

//        if (isPlaying) {
//            stopAudio();
//        }
    }


    @Override
    public void onClickListener(File file, int position) {
        // startActivity(new Intent(RecordingActivity.this,AudioListActivity.class));

        dialog.show();


        playBtn = dialog.findViewById(R.id.player_play_btn);
        playerFilename = dialog.findViewById(R.id.player_filename);
        playerseekBar = dialog.findViewById(R.id.player_seekBar);
        playerHeader = dialog.findViewById(R.id.player_header_name);
        playerFilename.setText(file.getName());
        Button cancel = dialog.findViewById(R.id._player_sheet_btn_cancel);
        dialog.findViewById(R.id.player_play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    isStopped = true;
                    playBtn.setImageResource(R.drawable.play);
                    pauseAudio();

                } else {
                    if (isStopped)
                    {
                        resumeAudio();
                        playBtn.setImageResource(R.drawable.pause);
                        isStopped = false;
                        return;
                    }
                    isPlaying = true;
                    playAudio(file);
                }
            }

        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    stopAudio();
                }

                dialog.dismiss();
            }
        });

    }

    private void stopAudio() {

        playBtn.setImageResource(R.drawable.play);

        isPlaying = false;

        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void playAudio(File fileToPlay) {


        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setImageResource(R.drawable.pause);
        playerFilename.setText(fileToPlay.getName());


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();

            }
        });

        playerseekBar.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

        isPlaying = true;
    }//playAudio ended

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerseekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);

            }
        };
    }

//    public void showSaveRecordingDialogBox() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
//        View view = layoutInflaterAndroid.inflate(R.layout.save_recording_dialogbox, null);
//        builder.setView(view);
//        builder.setCancelable(false);
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//        TextView time = view.findViewById(R.id.tv_time);
//        time.setText(totalTime);
////        EditText name = view.findViewById(R.id.tv_description);
////        recordingName = name.getText().toString();
//        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//                stopRecording();
//
//            }
//        });
//        view.findViewById(R.id.btn_resume).setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View view) {
//                alertDialog.dismiss();
//                resumeRecording();
//            }
//        });
//        return;
// }

    public void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    public void resumeAudio() {
        mediaPlayer.start();
        isPlaying = true;
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }
}