package com.myongji.myongdventure;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.myongji.myongdventure.schema.User;

/**
 * Created by kimharim on 2017. 12. 4..
 */

public class DBHelper {
    private static DBHelper instance = new DBHelper();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private User currentUser;

    public DBHelper() {
        Log.i("dbHelper", "dbHelper가 생성되었습니다.");
    }

    // users 테이블에 저장된 현재 사용자의 리스너
    // 로그인 할 때 최초 1회만 불러야 한다.
    public void initUserListener(final String uid) {
        myRef.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("myRef", "Failed to read value.", error.toException());
            }
        });

        final DatabaseReference myConnectionsRef = myRef.child("users").child(uid);

        // 최종 로그인 시간의 timestamp들을 기록
        final DatabaseReference lastOnlineRef = myRef.child("users").child(uid).child("lastOnline");
        // 현재 로그인 중인 사용자들의 상태를 기록
        final DatabaseReference usersOnlineRef = myRef.child("usersOnline").child(uid);

        final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    DatabaseReference con = myConnectionsRef.push();
                    usersOnlineRef.setValue(true);

                    // when this device disconnects, remove it
                    con.onDisconnect().removeValue();
                    usersOnlineRef.onDisconnect().removeValue();

                    // when I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);

                    // add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    con.setValue(Boolean.TRUE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });
    }

    public static DBHelper getInstance() {
        return instance;
    }

    public void writeNewUser(String uid, String name, String email) {
        User user = new User(uid, name, email);

        Log.i("new User", "새로운 유저가 생성되었습니다.");
        myRef.child("users").child(uid).setValue(user);
        initUserListener(uid);
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
