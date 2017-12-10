package com.myongji.myongdventure.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myongji.myongdventure.R;
import com.myongji.myongdventure.schema.User;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();

            // 유저정보 가져오기
            myRef.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    currentUser = dataSnapshot.getValue(User.class);

                    if (currentUser != null) {
                        String level = String.valueOf(currentUser.level);
                        String name = String.valueOf(currentUser.name);
                        String levelName = "Lv. " + level + " " + name;
                        String exp = String.valueOf(currentUser.exp);
                        String nextExp = String.valueOf((currentUser.level + 2) * currentUser.level * 5);
                        String fullExp = exp + " / " + nextExp;

//                        TextView usernameTextView = findViewById(R.id.tv_username);
//                        usernameTextView.setText(levelName);
//                        TextView userexpTextView = findViewById(R.id.tv_userexp);
//                        userexpTextView.setText(fullExp);
//
//                        Uri imageUrl = firebaseUser.getPhotoUrl();
//                        if (imageUrl != null) {
//                            ImageView userImageView = findViewById(R.id.iv_profile);
//                            Picasso.with(getApplicationContext()).load(imageUrl).into(userImageView);
//                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("myRef", "Failed to read value.", error.toException());
                }
            });
        }
    }
}
