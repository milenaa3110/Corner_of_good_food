package com.example.backend.models;

import java.util.List;

public class SubjectWithTeacher {
    String subject_name;
    List<TeacherUserDbo> teachers;

    public SubjectWithTeacher(String subject_name, List<TeacherUserDbo> teachers) {
        this.subject_name = subject_name;
        this.teachers = teachers;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public List<TeacherUserDbo> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherUserDbo> teachers) {
        this.teachers = teachers;
    }
}
