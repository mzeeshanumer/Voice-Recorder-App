package com.leeddev.voicerecorder;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class SettingActivity extends AppCompatActivity {
    ImageView btn_toggle1, btn_toggle2;
    SwitchCompat switchCompat2,switchCompat1;
    ConstraintLayout btn_rate_us;
    Toolbar toolbar;
    ImageView home;
    AdView ad_view;

    ConstraintLayout give_feedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btn_rate_us =findViewById(R.id.list_rate_us);
        toolbar = findViewById(R.id.toolbar);
        home = findViewById(R.id.home);
        ad_view= findViewById(R.id.ads_view);
        give_feedback= findViewById(R.id.list_give_fb);
        MobileAds.initialize(this, new OnInitializationCompleteListener(){
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        ad_view = findViewById(R.id.ads_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_view.loadAd(adRequest);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, RecordingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
        btn_rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri= Uri.parse("market://details?id="+ getPackageName());
                    Intent intent =new Intent(Intent.ACTION_VIEW,uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    Uri uri = Uri.parse("http://play.google.com/store/apps/details?id="+ getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
        give_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri= Uri.parse("market://details?id="+ getPackageName());
                    Intent intent =new Intent(Intent.ACTION_VIEW,uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    Uri uri = Uri.parse("http://play.google.com/store/apps/details?id="+ getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
        switchCompat2=findViewById(R.id.switch_toggle2);
        SharedPreferences sharedPreferences=getSharedPreferences("save",MODE_PRIVATE);
        switchCompat2.setChecked(sharedPreferences.getBoolean("value",false));

        switchCompat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchCompat2.isChecked())
                {
                    // When switch checked
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    switchCompat2.setChecked(false);
                   Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    // When switch unchecked
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    switchCompat2.setChecked(false);
                }
            }
        });

        switchCompat1=findViewById(R.id.switch_toggle1);
        SharedPreferences sharedPreferences1=getSharedPreferences("save",MODE_PRIVATE);
        switchCompat1.setChecked(sharedPreferences1.getBoolean("value",false));

        switchCompat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchCompat1.isChecked())
                {
                    // When switch checked
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    switchCompat1.setChecked(false);
                    Toast.makeText(getApplicationContext(),
                            "Coming Soon",
                            Toast.LENGTH_SHORT).show();

                }
                else
                {
                    // When switch unchecked
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    switchCompat1.setChecked(false);
                }
            }
        });
    }
}