package com.myongji.myongdventure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myongji.myongdventure.DBHelper;
import com.myongji.myongdventure.R;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DBHelper dbHelper = DBHelper.getInstance();
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 유저가 로그인 되있는지 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // 구글 로그인 성공, 파이어베이스로 로그인 시도
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // 구글 로그인 실패, 적절한 UI 업데이트 코드작성
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("firebaseAuthWithGoogle:", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // 로그인 성공, update UI with the signed-in user's information
                    Log.d("signIn", "signInWithCredential:success");
                    final FirebaseUser user = mAuth.getCurrentUser();
                    final String userUid = user.getUid();

                    // Users 테이블에 user가 있는지 확인
                    myRef.child("users/" + userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // user가 없으면 신규 생성
                            // 있으면 dbHelper가 현재 사용자 정보를 계속 갖고있게 함
                            if (dataSnapshot.getValue() == null) {
                                dbHelper.writeNewUser(userUid, user.getDisplayName(), user.getEmail());
                            } else {
                                dbHelper.initUserListener(userUid);
                            }

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("user", databaseError.getMessage());
                        }
                    });
                } else {
                    // 로그인 실패 시 유저에게 메세지를 보여준다.
                    Log.w("signIn:failure", "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }

                // ...
                }
            });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e("onConnectionFailed", result.toString());
    }
}
