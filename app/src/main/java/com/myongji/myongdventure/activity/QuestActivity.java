package com.myongji.myongdventure.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.myongji.myongdventure.R;
import com.myongji.myongdventure.schema.Quest;

public class QuestActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        Intent intent = getIntent();
        String building = intent.getStringExtra("building");
        String countText = intent.getStringExtra("countText");
        Log.i("building", building);

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

        Query query = myRef.child("quests").orderByChild("building").equalTo(buildingType);

        final FirebaseListAdapter mAdapter = new FirebaseListAdapter<Quest>(this, Quest.class, android.R.layout.simple_list_item_1, query) {
            @Override
            protected void populateView(View view, Quest quest, int position) {
                TextView text = view.findViewById(android.R.id.text1);
                text.setText(quest.title);
            }
        };

        ListView listView = findViewById(R.id.lv_quest);
        listView.setAdapter(mAdapter);

        TextView countView = findViewById(R.id.tv_countView);
        countView.setText(countText);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(view.getContext(), QuestDetailActivity.class);
                intent.putExtra("uid", mAdapter.getRef(position).getKey());
                view.getContext().startActivity(intent);
            }
        });
    }
}
