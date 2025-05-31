package com.example.backend.models;

public class StudentUserDbo {
    private int id;
    private String username;
    private String first_name;
    private String last_name;
    private String gender;
    private String address;
    private String phone;
    private String email;
    private String profile_picture_path;
    private String school_type;
    private int grade;

    public StudentUserDbo(int id, String username, String first_name, String last_name, String gender, String address, String phone, String email, String profile_picture_path, String school_type, int grade) {
        this.id = id;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.profile_picture_path = profile_picture_path;
        this.school_type = school_type;
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_picture_path() {
        return profile_picture_path;
    }

    public void setProfile_picture_path(String profile_picture_path) {
        this.profile_picture_path = profile_picture_path;
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
