package com.myongji.myongdventure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myongji.myongdventure.R;
import com.myongji.myongdventure.schema.Quest;

import java.util.ArrayList;

public class MyQuestActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private TextView countView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quest);

        countView = findViewById(R.id.tv_countView);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = firebaseUser.getUid();

        Query query = myRef.child("userQuests").child(userId);

        // 내 퀘스트 정보 가져오기
        query.addListenerForSingleValueEvent(new ValueEventListener() {
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

        final FirebaseListAdapter mAdapter = new FirebaseListAdapter<Quest>(this, Quest.class, android.R.layout.simple_list_item_1, query) {
            @Override
            protected void populateView(View view, Quest quest, int position) {
                TextView text = view.findViewById(android.R.id.text1);
                text.setText(quest.title);
            }
        };

        ListView listView = findViewById(R.id.lv_myquest);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(view.getContext(), MyQuestDetailActivity.class);
                intent.putExtra("uid", mAdapter.getRef(position).getKey());
                view.getContext().startActivity(intent);
            }
        });
    }
}
