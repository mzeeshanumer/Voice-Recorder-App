package com.leeddev.voicerecorder.UI;

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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.leeddev.voicerecorder.R;
public class SettingActivity extends AppCompatActivity {
    Switch switchCompat2,switchCompat1;
    ConstraintLayout btn_rate_us;
    Toolbar toolbar;
    ImageView btn_home;
    AdView ad_view;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    TextView tv_file_name;
    ConstraintLayout give_feedback,default_name,privacy_policy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btn_rate_us =findViewById(R.id.list_rate_us);
        privacy_policy= findViewById(R.id.list_privacy_policy);
        toolbar = findViewById(R.id.toolbar);
        btn_home = findViewById(R.id.btn_home);
        ad_view= findViewById(R.id.ads_view);
        default_name= findViewById(R.id.list_defaultFilename);
        give_feedback= findViewById(R.id.list_give_fb);
        sharedpreferences = getSharedPreferences("", Context.MODE_PRIVATE);
        sharedpreferences=getSharedPreferences("save",MODE_PRIVATE);
        editor=sharedpreferences.edit();
        tv_file_name = findViewById(R.id.tv_file_name);
        default_name = findViewById(R.id.list_defaultFilename);
        switchCompat1=findViewById(R.id.switch_toggle1);
        SharedPreferences sharedPreferences1=getSharedPreferences("save",MODE_PRIVATE);
        switchCompat1.setChecked(sharedPreferences1.getBoolean("value",false));
//HOME SCREEN
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, RecordingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
//FIRST SWITCH BUTTONS
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
//SECOND SWITCH BUTTONS
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
                else {// When switch unchecked
                    SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                    editor.putBoolean("value",false);
                    editor.apply();
                    switchCompat2.setChecked(false);
                }
            }
        });
//DEFAULT FILE NAME
        default_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(SettingActivity.this);
            }
        });
//RATE US
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
//FEEDBACK
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
        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SettingActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
    }
//DEFAULT FILE NAME DIALOG BOX
    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.default_name_dialogbox);
        final EditText enter_name = dialog.findViewById(R.id.enter_name);
//DEFAULT FILE NAME SAVE BUTTON
        TextView dialogButtonSave = (TextView) dialog.findViewById(R.id.tv_save);
        dialogButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          String edit_name =enter_name.getText().toString();
          editor.putString("filename",enter_name.getText().toString());
          tv_file_name.setText(edit_name);
          Toast.makeText(getApplicationContext(),"File Name Changed",Toast.LENGTH_SHORT).show();
          editor.apply();
          dialog.dismiss();
            }
        });
//DEFAULT FILE NAME CANCEL BUTTON
        TextView dialogButtonCancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
//BANNER ADS
        MobileAds.initialize(this, new OnInitializationCompleteListener(){
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        ad_view.loadAd(adRequest);
    }
}