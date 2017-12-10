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
    public String title;
    public Uri imageUrl;
    public Uri videoUrl;

    public UserQuest() {

    }

    public UserQuest(Status status, String title) {
        this.status = status;
        this.title = title;
        this.likeCount = 0;
    }

    public String getStatusText() {
        String statusText = "";

        switch (this.status) {
            case ONGOING:
                statusText = "진행중";
                break;
            case DONE:
                statusText = "완료";
                break;
            default:
                break;
        }

        return statusText;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", status);

        return result;
    }
}
