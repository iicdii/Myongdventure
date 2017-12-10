package com.myongji.myongdventure.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myongji.myongdventure.R;

import java.io.File;

/**
 * Created by KimVala on 2017-12-10.
 */

public class ConfirmUploadVideoDialog extends Dialog {
    String path;
    VideoView videoView;
    SharedPreferences setting;

    public ConfirmUploadVideoDialog(@NonNull Context context, String path) {
        super(context);
        this.path = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_video);

        setting = getContext().getSharedPreferences("setting", 0);

        videoView = (VideoView)findViewById(R.id.vv_video);

        MediaController mediaController = new MediaController(getContext());
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.start();

        Button btn = (Button)findViewById(R.id.btn_confirmvideo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 완료 버튼 눌렀을 때 수행될 것
                if(setting.getInt("sound", 1) == 1) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(view.getContext(), R.raw.ppap);
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();
                }

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                Uri file = Uri.fromFile(new File(path));
                StorageReference riversRef = storageRef.child(file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getContext(), "영상 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(getContext(), "영상 업로드 성공", Toast.LENGTH_SHORT).show();
                    }
                });

                cancel();
            }
        });
    }
}
