package com.example.labrador;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class FirebaseHelper {
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    public FirebaseHelper() {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }
    // 알람 정보 Firestore에 저장
    public void saveAlarmToFirestore(String alarmName, int hour, int minute, String selectedDays, String interval) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Firestore에 알람 정보 저장
            CollectionReference alarmsCollection = firestore.collection("alarms");
            DocumentReference alarmDocument = alarmsCollection.document();
            alarmDocument.set(new AlarmInfo(alarmName, hour, minute, selectedDays, interval));
        } else {
            // 사용자가 로그인하지 않은 경우 처리
            // 예를 들어, 로그인 화면으로 리디렉션하거나 오류 메시지 표시
        }
    }
    // 알람 정보 Firestore에서 불러오기
    public void getAlarmsFromFirestore(final OnAlarmsLoadListener listener) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            CollectionReference alarmsCollection = firestore.collection("alarms");
            alarmsCollection.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    listener.onAlarmsLoadSuccess(task.getResult().getDocuments());
                } else {
                    listener.onAlarmsLoadError(task.getException());
                }
            });
        } else {
            // 사용자가 로그인하지 않은 경우 처리
            // 예를 들어, 로그인 화면으로 리디렉션하거나 오류 메시지 표시
        }
    }
    // 사용자 정보 Firestore에 저장
    public void saveUserDataToFirestore(String userId, String userEmail, boolean isPregnant, boolean isVisual, boolean isMale) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Firestore에 사용자 정보 저장
            CollectionReference usersCollection = firestore.collection("users");
            DocumentReference userDocument = usersCollection.document(userId);
            userDocument.set(new User(userEmail, isPregnant, isVisual, isMale));
        } else {
            // 사용자가 로그인하지 않은 경우 처리
            // 예를 들어, 로그인 화면으로 리디렉션하거나 오류 메시지 표시
        }
    }

    // 이메일 중복 확인
    public void checkDuplicateEmail(String email, final OnEmailCheckListener listener) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SignInMethodQueryResult result = task.getResult();
                        if (result != null && result.getSignInMethods() != null) {
                            if (result.getSignInMethods().size() > 0) {
                                // 이미 이 이메일로 가입한 사용자가 있음
                                listener.onEmailExists();
                            } else {
                                // 사용 가능한 이메일 주소
                                listener.onEmailAvailable();
                            }
                        } else {
                            // Firebase 결과가 null이거나, SignInMethods가 null인 경우
                            listener.onEmailCheckError(new Exception("Firebase 결과가 null입니다."));
                        }
                    } else {
                        // 오류 처리
                        listener.onEmailCheckError(task.getException());
                    }
                });
    }

    public interface OnEmailCheckListener {
        void onEmailExists();
        void onEmailAvailable();
        void onEmailCheckError(Exception e);
    }
    public interface OnAlarmsLoadListener {
        void onAlarmsLoadSuccess(List<DocumentSnapshot> alarms);
        void onAlarmsLoadError(Exception e);
    }
}
