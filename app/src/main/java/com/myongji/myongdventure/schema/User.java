package com.myongji.myongdventure.schema;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kimharim on 2017. 12. 4..
 */

@IgnoreExtraProperties
public class User {
    public String uid;
    public String name;
    public String email;
    public int level;
    public int exp;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.level = 1;
        this.exp = 0;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("email", email);
        result.put("level", level);
        result.put("exp", exp);

        return result;
    }
}
