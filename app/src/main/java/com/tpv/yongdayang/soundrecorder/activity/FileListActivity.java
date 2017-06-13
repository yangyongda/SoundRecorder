package com.tpv.yongdayang.soundrecorder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tpv.yongdayang.soundrecorder.R;
import com.tpv.yongdayang.soundrecorder.adapter.FileViewerAdapter;
import com.tpv.yongdayang.soundrecorder.bean.RecordingItem;
import com.tpv.yongdayang.soundrecorder.db.DBHelper;

import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity {

    private TextView mToolBar_Title;
    private RecyclerView mRecyclerView;
    private ArrayList<RecordingItem> recordingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        //设置ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar_Title = (TextView)findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        mToolBar_Title.setText(R.string.action_file); //设置Title
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //显示返回箭头
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initData();
        initView();
    }

    public void initView(){
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView) ;

        mRecyclerView.setHasFixedSize(true); //固定大小
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);  //设置布局
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置动画

        mRecyclerView.setAdapter(new FileViewerAdapter(FileListActivity.this,recordingItems));
    }

    public void initData(){
        recordingItems = new DBHelper(FileListActivity.this).getAllItem();
        //Log.v("ItemCount", recordingItems.size()+"");
    }
}
