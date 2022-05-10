package com.leeddev.voicerecorder;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
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
    ImageView btn_home;
    AdView ad_view;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
TextView tv_file_name;


    ConstraintLayout give_feedback,default_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btn_rate_us =findViewById(R.id.list_rate_us);
        toolbar = findViewById(R.id.toolbar);
        btn_home = findViewById(R.id.home);
        ad_view= findViewById(R.id.ads_view);
        default_name= findViewById(R.id.list_default_fn);
        give_feedback= findViewById(R.id.list_give_fb);
        sharedpreferences = getSharedPreferences("", Context.MODE_PRIVATE);
        sharedpreferences=getSharedPreferences("save",MODE_PRIVATE);
        editor=sharedpreferences.edit();
        tv_file_name = findViewById(R.id.tv_file_name);
        default_name = findViewById(R.id.list_default_fn);
        default_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(SettingActivity.this);
            }
        });

    }

    public void showDialog(Activity activity){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.default_name_dialogbox);

        final EditText enter_name = dialog.findViewById(R.id.enter_name);

        TextView dialogButtonYes = (TextView) dialog.findViewById(R.id.textViewYes);


        dialogButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

          String et =enter_name.getText().toString();
                editor.putString("filename",enter_name.getText().toString());
               tv_file_name.setText(et);
                editor.apply();
                dialog.dismiss();
            }
        });

        TextView dialogButtonNo = (TextView) dialog.findViewById(R.id.textViewNo);
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

        MobileAds.initialize(this, new OnInitializationCompleteListener(){
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        ad_view = findViewById(R.id.ads_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_view.loadAd(adRequest);
        btn_home.setOnClickListener(new View.OnClickListener() {
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