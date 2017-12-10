package com.myongji.myongdventure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CreditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        Button btn = (Button)findViewById(R.id.btn_mju);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WepViewActivity.class);
                intent.putExtra("page", "http://www.mju.ac.kr");
                view.getContext().startActivity(intent);
            }
        });
    }
}
