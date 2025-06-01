package com.example.labrador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView userAgeTextView;
    private TextView userGenderTextView;
    private TextView userDiseaseTextView;
    private Button logoutButton;
    private Button withdrawButton;
    private Button backButton;
    private Button infoModifyButton;
    private Button medAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userNameTextView = findViewById(R.id.user_name_text);
        userAgeTextView = findViewById(R.id.user_age_text);
        userGenderTextView = findViewById(R.id.user_gender_text);
        userDiseaseTextView = findViewById(R.id.user_diease_text);

        // Intent로부터 사용자 정보 가져오기
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        int userAge = intent.getIntExtra("userAge", 0);
        String userGender = intent.getStringExtra("userGender");

        // 사용자 정보를 TextView에 설정
        userNameTextView.setText("홍길동");
        userAgeTextView.setText("20");
        userGenderTextView.setText("남자");
        userDiseaseTextView.setText("없음");

        logoutButton = findViewById(R.id.btn_logout);
        withdrawButton = findViewById(R.id.btn_withdraw);
        backButton = findViewById(R.id.btn_back1);
        infoModifyButton = findViewById(R.id.btn_infor_modify);
        medAddButton = findViewById(R.id.btn_med_add);



        // 로그아웃 버튼 클릭 이벤트 처리
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase Authentication에서 현재 사용자 로그아웃
                FirebaseAuth.getInstance().signOut();

                // 로그인 화면으로 이동 또는 다른 필요한 동작 수행
                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                startActivity(intent);

                // 현재 UserProfileActivity 종료
                finish();
            }
        });

        // 정보 수정 버튼 클릭 이벤트 처리
        infoModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), User_InforModify.class);
                startActivity(intent);
            }
        });

        // 약 추가 버튼 클릭 이벤트 처리
        medAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 약 추가 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), AddMedicationActivity.class);
                startActivity(intent);
            }
        });

        // 회원 탈퇴 버튼 클릭 이벤트 처리
        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자에게 확인 입력란을 띄우고 확인을 입력하도록 안내
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                builder.setTitle("회원 탈퇴");
                builder.setMessage("정말로 회원 탈퇴하시겠습니까?");
                final EditText input = new EditText(UserProfileActivity.this);
                builder.setView(input);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userInput = input.getText().toString();
                        if (userInput.equals("확인")) {
                            // 사용자가 "확인"을 입력한 경우에만 탈퇴 수행
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                currentUser.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                    finish(); // 현재 화면 종료
                                                } else {
                                                    // 탈퇴 실패 처리
                                                    Toast.makeText(UserProfileActivity.this, "회원 탈퇴 실패", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // 사용자가 "확인"을 입력하지 않은 경우
                            Toast.makeText(UserProfileActivity.this, "확인 입력란에 '확인'을 입력하세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
            }
        });

    }

}
