package com.myongji.myongdventure;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myongji.myongdventure.activity.QuestDetailActivity;
import com.myongji.myongdventure.schema.Quest;

import java.util.ArrayList;

/**
 * Created by KimVala on 2017-12-03.
 */

public class QuestDialog extends Dialog {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    public QuestDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_quest);

        final ArrayList<String> arrayList = new ArrayList<>();

        // 5공학관 퀘스트 목록 불러오기
        // 나중에 건물별로 불러올때 1공학관이면 FIVE를 ONE으로 고치면 됨
        myRef.child("quests").orderByChild("building").equalTo("FIVE").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("load Quest", "5공학관 퀘스트를 불러옵니다.");
                for (DataSnapshot questSnapshot: dataSnapshot.getChildren()) {
                    Quest quest = questSnapshot.getValue(Quest.class);

                    if (quest != null)
                        arrayList.add(quest.title);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("loadQuest:onCancelled", databaseError.toException());
            }
        });


        arrayList.add("앙기모띠");
        arrayList.add("앙하림띠");

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);

        ListView listView = (ListView)findViewById(R.id.lv_quest);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), QuestDetailActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }
}
