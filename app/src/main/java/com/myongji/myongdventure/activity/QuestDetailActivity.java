package com.myongji.myongdventure.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myongji.myongdventure.R;
import com.myongji.myongdventure.schema.Quest;

import org.w3c.dom.Text;

public class QuestDetailActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_detail);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        Log.i("uid", uid);

        // 퀘스트 정보 가져오기
        myRef.child("quests").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Quest quest = dataSnapshot.getValue(Quest.class);

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
}
