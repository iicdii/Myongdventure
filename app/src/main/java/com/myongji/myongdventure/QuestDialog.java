package com.myongji.myongdventure;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by KimVala on 2017-12-03.
 */

public class QuestDialog extends Dialog {
    public QuestDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_quest);

        ArrayList<String> arrayList = new ArrayList<>();
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
