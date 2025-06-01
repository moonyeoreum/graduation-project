package com.example.labrador;

public class User {
    private String email;
    private boolean isPregnant;
    private boolean isVisual;
    private boolean isMale;

    public User(String email, boolean isPregnant, boolean isVisual, boolean isMale) {
        this.email = email;
        this.isPregnant = isPregnant;
        this.isVisual = isVisual;
        this.isMale = isMale;
    }

    // Getter 및 Setter 메서드를 추가할 수 있습니다.

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPregnant() {
        return isPregnant;
    }

    public void setPregnant(boolean pregnant) {
        isPregnant = pregnant;
    }

    public boolean isVisual() {
        return isVisual;
    }

    public void setVisual(boolean visual) {
        isVisual = visual;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", isPregnant=" + isPregnant +
                ", isVisual=" + isVisual +
                ", isMale=" + isMale +
                '}';
    }
}
