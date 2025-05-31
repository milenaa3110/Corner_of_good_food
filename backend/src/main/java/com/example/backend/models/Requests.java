package com.example.backend.models;

public class Requests {
    //id, teacherId, teacherName, cvPath, status
    private Integer request_id;

    private String teacher_name;
    private String cv_path;
    private String status;

    public Requests(Integer request_id, String teacher_name, String cv_path, String status) {
        this.request_id = request_id;
        this.teacher_name = teacher_name;
        this.cv_path = cv_path;
        this.status = status;
    }

    public Integer getRequest_id() {
        return request_id;
    }

    public void setRequest_id(Integer request_id) {
        this.request_id = request_id;
    }


    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getCv_path() {
        return cv_path;
    }

    public void setCv_path(String cv_path) {
        this.cv_path = cv_path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
