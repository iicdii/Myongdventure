package com.myongji.myongdventure.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.myongji.myongdventure.R;

public class SettingActivity extends AppCompatActivity {

    Vibrator vibrator;
    MediaPlayer mediaPlayer;
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        CheckBox checkBox1 = (CheckBox)findViewById(R.id.cb_sound);
        if(setting.getInt("sound", 1) == 0) {
            checkBox1.setChecked(false);
        }
        checkBox1.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CheckBox)view).isChecked()) {
                    editor.putInt("sound", 1);
                    editor.commit();
//                    Toast.makeText(SettingActivity.this, "sound = " + setting.getInt("sound", 1), Toast.LENGTH_SHORT).show();
                } else {
                    editor.putInt("sound", 0);
                    editor.commit();
//                    Toast.makeText(SettingActivity.this, "sound = " + setting.getInt("sound", 1), Toast.LENGTH_SHORT).show();
                }
            }
        });

        CheckBox checkBox2 = (CheckBox)findViewById(R.id.cb_vibrator);
        if(setting.getInt("vibrator", 1) == 0) {
            checkBox2.setChecked(false);
        }
        checkBox2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CheckBox)view).isChecked()) {
                    editor.putInt("vibrator", 1);
                    editor.commit();
//                    Toast.makeText(SettingActivity.this, "vibrator = " + setting.getInt("vibrator", 1), Toast.LENGTH_SHORT).show();
                } else {
                    editor.putInt("vibrator", 0);
                    editor.commit();
//                    Toast.makeText(SettingActivity.this, "vibrator = " + setting.getInt("vibrator", 1), Toast.LENGTH_SHORT).show();
                }
            }
        });

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        Button btn1 = (Button)findViewById(R.id.btn_test1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setting.getInt("vibrator", 1) == 1)
                    vibrator.vibrate(500);
            }
        });

        Button btn2 = (Button)findViewById(R.id.btn_test2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setting.getInt("sound", 1) == 1) {
                    mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.ppap);
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();
                }
            }
        });
    }
}
