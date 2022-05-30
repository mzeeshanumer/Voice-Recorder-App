//SPLASH SCREEN CODE
package com.leeddev.recorder.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.leeddev.recorder.R;

public class SplashScreenActivity extends AppCompatActivity {
    Button start;
    Animation fadeInAnimation;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        start = findViewById(R.id.getStartedAction);
        showAds();
// Using handler with postDelayed called runnable run method
        new Handler().postDelayed(() -> {
            start.setVisibility(View.VISIBLE);
            fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            start.startAnimation(fadeInAnimation);
//BUTTON CLICK Interstitial Ad
            start.setOnClickListener(view -> {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(SplashScreenActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            Log.d("TAG", "The ad was dismissed.");
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            mInterstitialAd = null;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            // Called when fullscreen content failed to show.
                            Log.d("TAG", "The ad failed to show.");
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    Log.d("TAG", "THE Interstitial was not Ready yet");
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }
            });
        }, 2 * 1000);
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
}

