package com.example.backend.models;

import java.time.LocalDateTime;

public class ClassWithStatus {

    private int id;
    private LocalDateTime beginDate;
    private LocalDateTime endDate;
    private String teacherName;
    private String teacherSurname;
    private Subject subject;
    private String status;

    public ClassWithStatus(int id, LocalDateTime beginDate, LocalDateTime endDate, String teacherName, String teacherSurname, Subject subject, String status) {
        this.id = id;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.teacherName = teacherName;
        this.teacherSurname = teacherSurname;
        this.subject = subject;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDateTime beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherSurname() {
        return teacherSurname;
    }

    public void setTeacherSurname(String teacherSurname) {
        this.teacherSurname = teacherSurname;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
