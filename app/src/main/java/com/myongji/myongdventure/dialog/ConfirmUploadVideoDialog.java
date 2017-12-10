package com.myongji.myongdventure.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.myongji.myongdventure.R;

/**
 * Created by KimVala on 2017-12-10.
 */

public class ConfirmUploadVideoDialog extends Dialog {
    String path;
    VideoView videoView;

    public ConfirmUploadVideoDialog(@NonNull Context context, String path) {
        super(context);
        this.path = path;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_video);

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
            }
        });
    }
}
