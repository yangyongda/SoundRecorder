package com.tpv.yongdayang.soundrecorder.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tpv.yongdayang.soundrecorder.R;
import com.tpv.yongdayang.soundrecorder.bean.RecordingItem;
import com.tpv.yongdayang.soundrecorder.db.DBHelper;
import com.tpv.yongdayang.soundrecorder.fragment.PlaybackFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by YongDa.Yang on 2017/6/5.
 */

public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.RecordingsViewHolder> {

    private Context mContext;
    private ArrayList<RecordingItem> mFileList;
    public  FileViewerAdapter(Context mContext, ArrayList<RecordingItem> fileList){
        this.mContext = mContext;
        mFileList = fileList;
    }

    /*
    * 创建界面
    * */
    @Override
    public FileViewerAdapter.RecordingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new RecordingsViewHolder(itemView);
    }
    /*
    * 绑定数据
    * */
    @Override
    public void onBindViewHolder(final FileViewerAdapter.RecordingsViewHolder holder, int position) {
        RecordingItem item = mFileList.get(position);

        long itemDuration = Math.round(item.getLength()/1000.0)*1000;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);

        holder.vName.setText(item.getName());
        holder.vLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.vDateAdded.setText(
                DateUtils.formatDateTime(
                        mContext,
                        item.getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        );

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlaybackFragment playbackFragment = new PlaybackFragment().newInstance(mFileList.get(holder.getAdapterPosition()));

                    FragmentTransaction transaction = ((FragmentActivity) mContext)
                            .getSupportFragmentManager()
                            .beginTransaction();

                    playbackFragment.show(transaction, "dialog_playback");

                } catch (Exception e) {

                }
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ArrayList<String> entrys = new ArrayList<String>();
                entrys.add(mContext.getString(R.string.dialog_file_share));
                entrys.add(mContext.getString(R.string.dialog_file_rename));
                entrys.add(mContext.getString(R.string.dialog_file_delete));

                final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);


                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {        //分享
                            shareFileDialog(holder.getAdapterPosition());
                        }else if (item == 1) { //重命名
                            renameFileDialog(holder.getAdapterPosition());
                        }else if (item == 2) { //删除
                            deleteFileDialog(holder.getAdapterPosition());
                        }
                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton(mContext.getString(R.string.dialog_action_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    public class RecordingsViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vLength;
        protected TextView vDateAdded;
        protected View cardView;

        public RecordingsViewHolder(View itemView) {
            super(itemView);
            vName = (TextView) itemView.findViewById(R.id.file_name_text);
            vLength = (TextView) itemView.findViewById(R.id.file_length_text);
            vDateAdded = (TextView) itemView.findViewById(R.id.file_date_added_text);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }


    public void shareFileDialog(int position) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mFileList.get(position).getFilePath())));
        shareIntent.setType("audio/mp4");
        mContext.startActivity(Intent.createChooser(shareIntent, mContext.getText(R.string.send_to)));
    }

    public void renameFileDialog (final int position) {

        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = (EditText) view.findViewById(R.id.new_name);

        renameFileBuilder.setTitle(mContext.getString(R.string.dialog_file_rename));
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton(mContext.getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String value = input.getText().toString().trim() + ".mp4";
                            rename(position, value);

                        } catch (Exception e) {

                        }

                        dialog.cancel();
                    }
                });
        renameFileBuilder.setNegativeButton(mContext.getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        renameFileBuilder.setView(view);
        AlertDialog alert = renameFileBuilder.create();
        alert.show();
    }

    public void rename(int position, String name) {

        String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath += "/SoundRecorder/" + name;
        File f = new File(mFilePath);

        if (f.exists() && !f.isDirectory()) {
            Toast.makeText(mContext,
                    String.format(mContext.getString(R.string.toast_file_exists), name),
                    Toast.LENGTH_SHORT).show();

        } else {
            File oldFilePath = new File(mFileList.get(position).getFilePath());
            oldFilePath.renameTo(f); //重命名
            new DBHelper(mContext).updateItem(mFileList.get(position).getId(), name, mFilePath); //更新数据库
            mFileList.get(position).setName(name); //更新List中RecordingItem的name
            mFileList.get(position).setFilePath(mFilePath); //更新List中RecordingItem的FilePath
            notifyItemChanged(position);
        }
    }

    //删除文件Dialog
    public void deleteFileDialog (final int position) {
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(mContext);
        confirmDelete.setTitle(mContext.getString(R.string.dialog_file_delete));
        confirmDelete.setMessage(mContext.getString(R.string.dialog_text_delete));
        confirmDelete.setCancelable(true);
        confirmDelete.setPositiveButton(mContext.getString(R.string.dialog_action_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            //删除文件
                            remove(position);

                        } catch (Exception e) {

                        }

                        dialog.cancel();
                    }
                });
        confirmDelete.setNegativeButton(mContext.getString(R.string.dialog_action_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = confirmDelete.create();
        alert.show();
    }

    public void remove(int position) {

        File file = new File(mFileList.get(position).getFilePath());
        file.delete(); //删除文件

        Toast.makeText(
                mContext,
                String.format(
                        mContext.getString(R.string.toast_file_delete),
                        mFileList.get(position).getName()
                ),
                Toast.LENGTH_SHORT
        ).show();

        new DBHelper(mContext).removeItemWithId(mFileList.get(position).getId()); //更新数据库
        mFileList.remove(position);  //移除列表中的RecordingItem
        notifyItemRemoved(position);
    }
}


