package com.myongji.myongdventure.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myongji.myongdventure.R;
import com.myongji.myongdventure.enums.Status;
import com.myongji.myongdventure.schema.Quest;
import com.myongji.myongdventure.schema.UserQuest;

public class QuestDetailActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_detail);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        Log.i("uid", uid);

        // 퀘스트 정보 가져오기
        myRef.child("quests").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Quest quest = dataSnapshot.getValue(Quest.class);

                if (quest == null) return;

                TextView titleView = findViewById(R.id.tv_title);
                titleView.setText(quest.title);
                TextView contentView = findViewById(R.id.tv_content);
                contentView.setText(quest.content);
                TextView expView = findViewById(R.id.tv_exp);

                String exp = String.valueOf(quest.exp);
                String expText = "경험치: " + exp;
                expView.setText(expText);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("myRef", "Failed to read value.", error.toException());
            }
        });
    }

    public void handleAccept(View view) {
        final Activity mActivity = this;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = firebaseUser.getUid();

        myRef.child("userQuests").child(userId).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserQuest userQuest = dataSnapshot.getValue(UserQuest.class);

                // 데이터가 있으면 이미 받은 퀘스트이므로 리턴한다.
                if (userQuest != null) return;

                TextView titleView = findViewById(R.id.tv_title);
                String title = String.valueOf(titleView.getText());

                UserQuest newQuest = new UserQuest(Status.ONGOING, title);

                myRef.child("userQuests").child(userId).child(uid).setValue(newQuest);

                Intent intent = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("myRef", "Failed to read value.", error.toException());
            }
        });
    }
}
