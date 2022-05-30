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
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.leeddev.recorder.R;
import com.leeddev.recorder.RecylerViewUtils.AudioListAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AudioListAdapter.onItemListClickbabu {
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private Chronometer timer;
    RelativeLayout startRecording, recordingInProgress;
    public static RecyclerView audioList;
    private File[] allFiles;
    public static AudioListAdapter audioListAdapter;
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
    String recordFile;
    ImageView settings;
    ImageButton saveRecording;
    Toolbar toolbar;
    Dialog playerDialog;
    ImageView recordBtn;
    public static long timeWhenStopped = 0;
    ImageButton pause_resume;
    AdView ad_view;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ad_view = findViewById(R.id.ads_view);
        startRecording = findViewById(R.id.startRecordingParent);
        saveRecording = findViewById(R.id.btn_save);
        recordingInProgress = findViewById(R.id.recordingStartedParentView);
        recordBtn = findViewById(R.id.btn_start);
        pause_resume = findViewById(R.id.btn_pause_resume);
        timer = findViewById(R.id.timer);
        toolbar = findViewById(R.id.toolbar);
        settings = findViewById(R.id.settingsAction);
        ad_view = findViewById(R.id.ads_view);
        recordBtn.setOnClickListener(this);
        playerDialog = new Dialog(this);
        playerDialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        playerDialog.setContentView(R.layout.player_sheet);
        audioList = findViewById(R.id.audio_list_view);
        playerFilename = findViewById(R.id.player_filename);
        playerseekBar = findViewById(R.id.player_seekBar);
        // logic to diplay recordings
        initializeComponents();
        setListeners();
    } //Closed OnCreate Here

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setListeners() {
        //SETTING BUTTON
        settings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });
//PAUSE AND RESUME RECORDING BUTTON
        pause_resume.findViewById(R.id.btn_pause_resume).setOnClickListener(view -> {
            if (isresumed) {
                pauseRecording();
                pause_resume.setImageResource(R.drawable.icon_resume_recording);
                isresumed = false;
            } else {
                isresumed = true;
                resumeRecording();
                pause_resume.setImageResource(R.drawable.icon_pause_recording);
            }
        });
//STOP RECORDING BUTTON /ALSO SAVE
        saveRecording.setOnClickListener(view -> {
            stopRecording();
//SHOW InterstitialAd When Clicked Stop Button
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
//                            startActivity(new Intent(MainActivity.this,RecordingActivity.class));
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
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
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeComponents() {

        audioListAdapter = new AudioListAdapter(this, getFiles(), this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        audioList.setAdapter(audioListAdapter);
        sharedpreferences = getSharedPreferences("", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
//BANNER ADS
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_view.loadAd(adRequest);
        showAds();
        MobileAds.initialize(this, initializationStatus -> {
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private File[] getFiles() {
        String path = MainActivity.this.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();
        Arrays.sort(allFiles, Comparator.comparingLong(File::lastModified).reversed());
        return allFiles;
    }

    //Interstitial Ad
    private void showAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, this.getResources().getString(R.string.admob_app_interstitial), adRequest,
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
                    isresumed = true;
                    startRecording();
                    isRecording = true;
                    Toast.makeText(MainActivity.this, "Recording Started", Toast.LENGTH_SHORT).show();
                    startRecording.setVisibility(View.GONE);
                    recordingInProgress.setVisibility(View.VISIBLE);

                }
                break;
        }
    }

    //Start Recording and Get Path, Time, Date
    @SuppressLint("NewApi")
    public void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timeWhenStopped = 0;
        timer.start();
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        this.startService(serviceIntent);
        pause_resume.setImageResource(R.drawable.icon_pause_recording);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        String recordPath = MainActivity.this.getExternalFilesDir("/").getAbsolutePath();
        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        String filename = sharedPreferences.getString("filename", "rename123");
        recordFile = filename + formatter.format(now) + ".mp3";
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void stopRecording() {
        timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
        timer.stop();
        isRecording = false;
        isresumed = false;
        timeWhenStopped = 0;
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        this.stopService(serviceIntent);
        Toast.makeText(getApplicationContext(), "Recording Saved", Toast.LENGTH_SHORT).show();
        recordingInProgress.setVisibility(View.GONE);
        startRecording.setVisibility(View.VISIBLE);
        audioListAdapter = new AudioListAdapter(this, getFiles(), this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        audioList.setAdapter(audioListAdapter);

    }

    //Pause Recording Function
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pauseRecording() {
        pause_resume.setImageResource(R.drawable.icon_resume_recording);
        mediaRecorder.pause();
        timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
        timer.stop();
        Toast.makeText(getApplicationContext(), "Recording Paused", Toast.LENGTH_SHORT).show();
    }

    //Pause Recording Function
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeRecording() {
        timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        timer.start();
        pause_resume.setImageResource(R.drawable.icon_pause_recording);
        mediaRecorder.resume();
        Toast.makeText(getApplicationContext(), "Recording Resumed", Toast.LENGTH_SHORT).show();
    }

    //Permissions
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 21);
            return false;
        }
    }

    //Player sheet
    @Override
    public void onClickListener(File file, int position) {
        playerDialog.show();
        playBtn = playerDialog.findViewById(R.id.player_play_btn);
        playerFilename = playerDialog.findViewById(R.id.player_filename);
        playerseekBar = playerDialog.findViewById(R.id.player_seekBar);
        playerHeader = playerDialog.findViewById(R.id.player_header_name);
        playerFilename.setText(file.getName());
        playerDialog.setCanceledOnTouchOutside(false);
        Button btnCancel = playerDialog.findViewById(R.id._player_sheet_btn_cancel);
//player Play Button
        playerDialog.findViewById(R.id.player_play_btn).setOnClickListener(view -> {
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
        });
        //player Cancel Button
        btnCancel.setOnClickListener(view -> {
            if (isPlaying) {
                stopAudio();
            }
            playerDialog.dismiss();
            playerseekBar.setProgress(0);
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
        mediaPlayer.setOnCompletionListener(mediaPlayer -> stopAudio());
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
                seekbarHandler.postDelayed(this, 50);
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
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.exit_dialogbox);
        dialog.setCanceledOnTouchOutside(false);
        // getting reference of TextView
        TextView dialogButtonExit = dialog.findViewById(R.id.textViewExit);
        TextView dialogButtonCancel = dialog.findViewById(R.id.textViewCancel);
// Click listener for No
        dialogButtonCancel.setOnClickListener(v -> dialog.dismiss());
// Click listener for Yes
        dialogButtonExit.setOnClickListener(v -> finishAffinity());
        // show the exit dialog
        dialog.show();
    }
}