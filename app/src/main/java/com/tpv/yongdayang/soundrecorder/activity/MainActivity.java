package com.tpv.yongdayang.soundrecorder.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.tpv.yongdayang.soundrecorder.R;
import com.tpv.yongdayang.soundrecorder.RecordingService;
import com.tpv.yongdayang.soundrecorder.db.DBHelper;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton mRecordButton = null;
    private Button mPauseButton = null;

    private TextView mRecordingPrompt;
    private TextView mToolBar_Title;
    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;

    private Chronometer mChronometer = null;
    private ArrayList<String> fileNames;
    long timeWhenPaused = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToolBar_Title = (TextView)findViewById(R.id.toolbar_title);
        mToolBar_Title.setText(R.string.recorder); //设置Title
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //获取目录下所有的录音文件并让数据库同步数据
        fileNames = getFileNames(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SoundRecorder");
        if(fileNames != null)
            checkData(fileNames);

        initView();
    }

    private void initView(){
        mRecordButton = (FloatingActionButton)findViewById(R.id.btnRecord);
        mPauseButton = (Button)findViewById(R.id.btnPause);
        mRecordingPrompt = (TextView)findViewById(R.id.recording_status_text);
        mChronometer = (Chronometer)findViewById(R.id.chronometer);

        mPauseButton.setEnabled(false);
        mRecordButton.setBackgroundResource(R.drawable.recordbtn_back);

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseRecord(mPauseRecording);
                mPauseRecording = !mPauseRecording;
            }
        });
    }

    private void onPauseRecord(boolean pause) {
        if (pause) {
            //暂停
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.mipmap.ic_media_play ,0 ,0 ,0);
            mRecordingPrompt.setText((String)getString(R.string.resume_recording_button));
            timeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime(); //保存暂停时的时间
            mChronometer.stop();
        } else {
            //恢复
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.mipmap.ic_media_pause ,0 ,0 ,0);
            mRecordingPrompt.setText((String)getString(R.string.pause_recording_button).toUpperCase());
            mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);  //为计时器设置基准时间
            mChronometer.start();
        }
    }


    private void onRecord(boolean start) {
        Intent intent = new Intent(MainActivity.this, RecordingService.class);

        if (start) {  //开始录音
            mPauseButton.setEnabled(true);
            Toast.makeText(MainActivity.this ,R.string.toast_recording_start,Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                folder.mkdir();
            }

            //start Chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start(); //启动计时器
            //每一秒调用一次
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "..");
                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        mRecordPromptCount = -1;
                    }

                    mRecordPromptCount++;
                }
            });

            //启动Service，在后台进行录音
            startService(intent);
            //录音的过程中不息屏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;

        } else { //停止录音

            mRecordButton.setImageResource(R.mipmap.ic_mic_white_36dp);
            mPauseButton.setEnabled(false);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));

            stopService(intent);
            //屏幕可以关闭
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate( R.menu.menu_main ,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_file:
                Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivity(intent);
                break;
            case R.id.action_settings:
                Intent setIntent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(setIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkData(List<String> fileNames){
        DBHelper helper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DBHelper.TABLE_NAME, new String[]{"_id", "recording_name"}, null, null, null, null, null);
        while(c.moveToNext()){
            String name = c.getString(c.getColumnIndex("recording_name"));
            if(!fileNames.contains(name)){
                int id = c.getInt(c.getColumnIndex("_id"));
                helper.removeItemWithId(id);
            }
        }
        c.close();
    }

    private ArrayList<String> getFileNames(String path){
        ArrayList<String> fileNames = new ArrayList<>();
        File sourcePath = new File(path);
        if (!sourcePath.exists()) {
            sourcePath.mkdir();
        }
        File[] files = sourcePath.listFiles();
        for(int i = 0; i < files.length; i++){
            fileNames.add(files[i].getName());
        }
        return fileNames;
    }
}
