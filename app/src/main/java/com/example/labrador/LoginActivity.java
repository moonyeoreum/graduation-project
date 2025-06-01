package com.example.labrador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText entId, entPw;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance(); // FirebaseAuth 초기화
        userReference = FirebaseDatabase.getInstance().getReference("users"); // Firebase 데이터베이스 참조

        // Firebase Firestore 초기화
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // XML에서 정의한 UI 요소들과 연결
        entId = findViewById(R.id.ent_email);
        entPw = findViewById(R.id.ent_pw1);

        Button loginButton = findViewById(R.id.btn_login);
        loginButton.setOnClickListener(view -> loginUser());

        Button newMemberButton = findViewById(R.id.btn_register);
        newMemberButton.setOnClickListener(view -> goToRegisterActivity());
    }

    private void loginUser() {
        String email = entId.getText().toString();
        String password = entPw.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 로그인에 성공한 경우
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d("LoginActivity", "로그인 성공: " + user.getEmail());

                            // 사용자의 UID 가져오기
                            String userId = user.getUid();

                            // Firebase 데이터베이스에서 음성 On/Off 설정 가져오기
                            userReference.child(userId).child("isTTSOn")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Boolean isTTSOn = dataSnapshot.getValue(Boolean.class);
                                            if (isTTSOn != null) {
                                                // isTTSOn 값에 따라 TTS를 활성화 또는 비활성화
                                                if (isTTSOn) {
                                                    // TTS 활성화
                                                } else {
                                                    // TTS 비활성화
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // 에러 처리
                                        }
                                    });

                            // Firebase Firestore에서 사용자 데이터 가져오기
                            FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
                            DocumentReference userRef = dbFirestore.collection("users").document(user.getUid());

                            userRef.get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            // 사용자 데이터가 존재하는 경우
                                            String username = documentSnapshot.getString("username");
                                            boolean isPregnant = documentSnapshot.getBoolean("isPregnant");
                                            boolean isVisual = documentSnapshot.getBoolean("isVisual");
                                            boolean isMale = documentSnapshot.getBoolean("isMale");

                                            // 여기에서 사용자 데이터를 사용하거나 표시합니다.
                                        } else {
                                            Log.d("LoginActivity", "사용자 데이터가 존재하지 않습니다.");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("LoginActivity", "Firestore에서 사용자 데이터 가져오기 실패: " + e.getMessage());
                                    });

                            // 로그인 성공 후 메인 엑티비티로 이동
                            Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                            startActivity(intent);
                        }
                    } else {
                        // 로그인에 실패한 경우
                        Exception e = task.getException();
                        if (e != null) {
                            Log.e("LoginActivity", "Firebase Authentication 오류: " + e.getMessage());
                        }
                    }
                });
    }

    private void goToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
