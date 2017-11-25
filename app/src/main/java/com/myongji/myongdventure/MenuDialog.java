package com.myongji.myongdventure;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by KimVala on 2017-11-25.
 */

public class MenuDialog extends Dialog {
    public MenuDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_menu);
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                cancel();
                break;
            case R.id.btn2:
                cancel();
                break;
            case R.id.btn3:
                cancel();
                break;
            case R.id.btn4:
                cancel();
                break;
            case R.id.btn5:
                cancel();
                break;
            case R.id.btn6:
                cancel();
                break;
        }
    }
}
