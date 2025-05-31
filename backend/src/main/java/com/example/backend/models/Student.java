package com.example.backend.models;


public class Student {

    private int user_id;
    private String school_type;

    private String username;
    private int grade;

    public Student(int user_id, String  username, String school_type, int grade) {
        this.user_id = user_id;
        this.school_type = school_type;
        this.grade = grade;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getSchool_type() {
        return school_type;
    }

    public void setSchool_type(String school_type) {
        this.school_type = school_type;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }



}
