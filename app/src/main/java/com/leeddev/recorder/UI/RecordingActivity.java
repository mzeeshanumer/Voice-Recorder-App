package com.leeddev.recorder.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.leeddev.recorder.R;
import com.leeddev.recorder.RecylerViewUtils.AudioListAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
public class RecordingActivity extends AppCompatActivity implements View.OnClickListener, AudioListAdapter.onItemListClickbabu {
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
    private SeekBar playerseekBar;
    public static boolean isStopped = false;
    public static boolean isresumed = false;
    private InterstitialAd mInterstitialAd;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    public String recordingName = "";
    private Long lastPause;
    ImageView settings;
    ImageButton save;
    Toolbar toolbar;
    Dialog dialog;
    long timeWhenStopped = 0;
    ImageButton pause_resume;
    AdView ad_view;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        ad_view = findViewById(R.id.ads_view);
        startRecording = findViewById(R.id.startRecordingParent);
        save = findViewById(R.id.btn_save);
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
        audioListAdapter = new AudioListAdapter(this,allFiles, this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(RecordingActivity.this));
        audioList.setAdapter(audioListAdapter);
        toolbar = findViewById(R.id.toolbar);
        settings = findViewById(R.id.settingsAction);
        sharedpreferences = getSharedPreferences("", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        showAds();
//SETTING BUTTON
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordingActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
//PAUSE AND RESUME RECORDING BUTTON
        pause_resume.findViewById(R.id.btn_pause_resume).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (isresumed) {
                    resumeRecording();
                    pause_resume.setImageResource(R.drawable.icon_pause_recording);
                    isresumed = false;
                }
                else {
                    isresumed = true;
                    pauseRecording();
                    isRecording = false;
                    pause_resume.setImageResource(R.drawable.icon_resume_recording);
                }
            }
        });
//STOP RECORDING BUTTON /ALSO SAVE
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
//SHOW InterstitialAd When Clicked Stop Button
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(RecordingActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            Log.d("TAG", "The ad was dismissed.");
//                            startActivity(new Intent(MainActivity.this,RecordingActivity.class));
                            mInterstitialAd = null;
                            showAds();
                        }
                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when fullscreen content failed to show.
                            Log.d("TAG", "The ad failed to show.");
                        }
                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            mInterstitialAd = null;
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    Log.d("TAG", "THE Intertiated was not Ready yet");
                }

            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        ad_view = findViewById(R.id.ads_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_view.loadAd(adRequest);
    }
//    public void removeItem(int position) {
//        removeItem(position);
//        audioListAdapter.notifyItemRemoved(position);
//    }

    private void showAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-9104652884839341/4730510907", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
//Get Permission Before Start Recording
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
        }
    }

//Start Recording and Get Path, Time, Date
    @SuppressLint("NewApi")
    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());

        timeWhenStopped = 0;
//        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        pause_resume.setImageResource(R.drawable.icon_pause_recording);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        String recordPath = RecordingActivity.this.getExternalFilesDir("/").getAbsolutePath();
        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        String filename = sharedPreferences.getString("filename", "rename123");
        recordFile = filename + formatter.format(now) + ".3gp";
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
//Stop Recording and Set Audio File in Path
    private void stopRecording() {


        timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
        timer.stop();
        isRecording = false;
        isresumed = false;
        timeWhenStopped = 0;
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        Toast.makeText(getApplicationContext(), "Recording Saved", Toast.LENGTH_SHORT).show();
        recordingInProgress.setVisibility(View.GONE);
        startRecording.setVisibility(View.VISIBLE);
        String path = RecordingActivity.this.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        audioListAdapter = new AudioListAdapter(this,allFiles, this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(RecordingActivity.this));
        audioList.setAdapter(audioListAdapter);
    }
//Pause Recording Function
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pauseRecording() {
        pause_resume.setImageResource(R.drawable.icon_resume_recording);
        mediaRecorder.pause();
        timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
        timer.stop();
//        lastPause= SystemClock.elapsedRealtime();
//        timer.stop();
//        timer.getBase();
        Toast.makeText(getApplicationContext(), "Recording Paused", Toast.LENGTH_SHORT).show();
    }
 //Pause Recording Function
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeRecording() {
        timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        timer.start();
//        timer.setBase(timer.getBase()+(SystemClock.elapsedRealtime()-lastPause));
//      timer.setBase(SystemClock.elapsedRealtime()-lastPause);
//        timer.start();
        pause_resume.setImageResource(R.drawable.icon_pause_recording);
        mediaRecorder.resume();
        Toast.makeText(getApplicationContext(), "Recording Resumed", Toast.LENGTH_SHORT).show();
    }
//Permissions
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
        //code removed to avoid crash for null mediaRecorder
        if (isRecording && mediaRecorder != null) {
            stopRecording();
        }
    }
//Player sheet
    @Override
    public void onClickListener(File file, int position) {
        // startActivity(new Intent(RecordingActivity.this,AudioListActivity.class));
        dialog.show();
        playBtn = dialog.findViewById(R.id.player_play_btn);
        playerFilename = dialog.findViewById(R.id.player_filename);
        playerseekBar = dialog.findViewById(R.id.player_seekBar);
        playerHeader = dialog.findViewById(R.id.player_header_name);
        playerFilename.setText(file.getName());
        dialog.setCanceledOnTouchOutside(false);
        Button cancel = dialog.findViewById(R.id._player_sheet_btn_cancel);
//player Play Button
        dialog.findViewById(R.id.player_play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    isStopped = true;
                    playBtn.setImageResource(R.drawable.icon_play_player);
                    pauseAudio();
                }
 //player Pause Button
                else {
                    if (isStopped) {
                        resumeAudio();
                        playBtn.setImageResource(R.drawable.icon_pause_player);
                        isStopped = false;
                        return;
                    }
                    isPlaying = true;
                    playAudio(file);
                }
            }
        });
 //player Cancel Button
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
  //Player sheet Stop Method
    private void stopAudio() {
        playBtn.setImageResource(R.drawable.icon_play_player);
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }
//Player sheet Play Method
    private void playAudio(File fileToPlay) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setImageResource(R.drawable.icon_pause_player);
        playerFilename.setText(fileToPlay.getName());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
            }
        });
//Seek Bar
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
//Player sheet Pause Method
    public void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }
//Player sheet Resume Method
    public void resumeAudio() {
        mediaPlayer.start();
        isPlaying = true;
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
    }
//Back Press Dialog Box exit/cancel
    @Override
    public void onBackPressed() {
        // calling the function
        customExitDialog();
    }
    private void customExitDialog() {
        final Dialog dialog = new Dialog(RecordingActivity.this);
        dialog.setContentView(R.layout.exit_dialogbox);
        dialog.setCanceledOnTouchOutside(false);
        // getting reference of TextView
        TextView dialogButtonYes = (TextView) dialog.findViewById(R.id.textViewYes);
        TextView dialogButtonNo = (TextView) dialog.findViewById(R.id.textViewNo);
// Click listener for No
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
// Click listener for Yes
        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finishAffinity();
            }
        });
 // show the exit dialog
        dialog.show();
    }
}