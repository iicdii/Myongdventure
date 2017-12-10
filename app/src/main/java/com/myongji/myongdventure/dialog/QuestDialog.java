package com.myongji.myongdventure.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myongji.myongdventure.R;
import com.myongji.myongdventure.activity.QuestActivity;

/**
 * Created by KimVala on 2017-12-03.
 */

public class QuestDialog extends Dialog {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private String building;
    private Activity mActivity;
    private TextView countView;

    public QuestDialog(Activity activity, String building) {
        super(activity);
        mActivity = activity;
        this.building = building;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_quest);

        TextView boardView = findViewById(R.id.tv_boardname);
        String title = building + " 퀘스트 게시판";
        boardView.setText(title);

        String buildingType = "";
        switch (building) {
            case "1공학관":
                buildingType = "ONE";
                break;
            case "5공학관":
                buildingType = "FIVE";
                break;
            case "함박관":
                buildingType = "HAM";
                break;
            default:
                break;
        }

        countView = findViewById(R.id.tv_countView);

        // 해당 건물 퀘스트 정보 가져오기
        myRef.child("quests").orderByChild("building").equalTo(buildingType).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String count = String.valueOf(dataSnapshot.getChildrenCount());
                String countText = "총 " + count + "개의 퀘스트가 있습니다.";
                countView.setText(countText);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("myRef", "Failed to read value.", error.toException());
            }
        });

        Button btn = findViewById(R.id.btn_questList);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, QuestActivity.class);
                intent.putExtra("building", building);
                intent.putExtra("countText", countView.getText());
                mActivity.startActivity(intent);
            }
        });
    }
}
