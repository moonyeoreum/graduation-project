package com.example.labrador;

public class PasswordValidator {
    public static boolean isPasswordValid(String password) {
        // 비밀번호의 길이가 충분한지 확인 (예: 8자 이상)
        if (password.length() < 8) {
            return false;
        }

        // 비밀번호에 대문자, 소문자, 숫자가 모두 포함되었는지 확인
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasUppercase && hasLowercase && hasDigit;
    }
}