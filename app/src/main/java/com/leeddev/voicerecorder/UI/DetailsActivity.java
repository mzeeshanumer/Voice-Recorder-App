package com.leeddev.voicerecorder.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.leeddev.voicerecorder.R;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTheme(R.style.TransparentCompat);
        getSupportActionBar().hide();
    }
}