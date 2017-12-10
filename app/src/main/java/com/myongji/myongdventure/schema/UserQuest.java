package com.myongji.myongdventure.schema;

import android.net.Uri;

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
public class UserQuest {
    public Status status;
    public int likeCount;
    public String userId;
    public Uri imageUrl;
    public Uri videoUrl;

    public UserQuest(Status status, String userId) {
        this.status = status;
        this.likeCount = 0;
        this.userId = userId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", status);

        return result;
    }
}
