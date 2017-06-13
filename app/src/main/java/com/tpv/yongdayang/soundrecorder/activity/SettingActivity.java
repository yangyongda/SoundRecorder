package com.tpv.yongdayang.soundrecorder.activity;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tpv.yongdayang.soundrecorder.R;
import com.tpv.yongdayang.soundrecorder.util.MySharedPreferences;

public class SettingActivity extends AppCompatActivity {

    private SwitchCompat sw;
    private LinearLayout ll;
    private TextView mToolBar_Title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToolBar_Title = (TextView)findViewById(R.id.toolbar_title);
        mToolBar_Title.setText(R.string.setting); //设置Title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //显示返回箭头
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initView();
    }

    public void initView(){
        sw = (SwitchCompat)findViewById(R.id.high_sw);
        ll = (LinearLayout)findViewById(R.id.about);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MySharedPreferences.setPrefHighQuality(getApplicationContext(), isChecked);
            }
        });

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.v("Click_Layout","Click");
                new AlertDialog.Builder(SettingActivity.this).setView(R.layout.dialog).create().show();
            }
        });
    }
}
