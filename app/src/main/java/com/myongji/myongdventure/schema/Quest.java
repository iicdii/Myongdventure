package com.myongji.myongdventure.schema;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.myongji.myongdventure.enums.Building;
import com.myongji.myongdventure.enums.QuestType;
import com.myongji.myongdventure.enums.Status;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kimharim on 2017. 12. 4..
 */

@IgnoreExtraProperties
public class Quest {
    public String uid;
    public String title;
    public String content;
    public QuestType type;
    public Building building;
    public int exp;

    public Quest() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("content", content);
        result.put("type", type);
        result.put("building", building);
        result.put("exp", exp);

        return result;
    }
}
