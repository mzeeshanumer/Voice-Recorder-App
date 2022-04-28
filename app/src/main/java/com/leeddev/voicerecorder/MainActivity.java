package com.leeddev.voicerecorder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;

public class MainActivity extends AppCompatActivity {
    Button start;
    Animation fadeInAnimation;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start=findViewById(R.id.getStartedAction);


        new Handler().postDelayed(new Runnable() {
            // Using handler with postDelayed called runnable run method
            @Override
            public void run() {
                start.setVisibility(View.VISIBLE);

//                final Animation animation = new TranslateAnimation(0,0,100,0);
//// set Animation for 5 sec
//                animation.setDuration(1000);
////for button stops in the new position.
//                animation.setFillAfter(true);
                fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                start.startAnimation(fadeInAnimation);
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent (MainActivity.this, RecordingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }

        }, 2*1000);


    }
}
