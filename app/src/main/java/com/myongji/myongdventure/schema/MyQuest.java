package com.myongji.myongdventure.schema;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.myongji.myongdventure.enums.QuestType;
import com.myongji.myongdventure.enums.Status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kimharim on 2017. 12. 4..
 */

@IgnoreExtraProperties
public class MyQuest {
    public Status status;
    // TODO - 사진, 동영상 등 저장할 변수 필요

    public MyQuest() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", status);

        return result;
    }
}
