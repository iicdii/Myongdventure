package com.myongji.myongdventure.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.myongji.myongdventure.R;

/**
 * Created by KimVala on 2017-12-10.
 */

public class ConfirmUploadPictureDialog extends Dialog {
    ImageView imageView;
    Bitmap bitmap;

    public ConfirmUploadPictureDialog(@NonNull Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_picture);

        imageView = (ImageView)findViewById(R.id.iv_picture);
        imageView.setImageBitmap(bitmap);

        Button btn = (Button)findViewById(R.id.btn_confirmpicture);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 완료 버튼 눌렀을 때 수행될 것
            }
        });
    }
}
