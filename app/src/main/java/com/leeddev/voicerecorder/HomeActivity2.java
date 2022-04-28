package com.leeddev.voicerecorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity2 extends AppCompatActivity {
    ImageView settings;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        toolbar= findViewById(R.id.toolbar);
        settings = findViewById(R.id.settingsAction);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity2.this, SettingActivity.class);
                startActivity(intent);
            }
        });


    }

    public void recordingIntent(View view) {
        Intent intent = new Intent(HomeActivity2.this, RecordingActivity.class);
        startActivity(intent);
    }



}