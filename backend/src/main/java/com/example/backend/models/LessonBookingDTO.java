package com.example.backend.models;

import java.time.LocalDateTime;

public class LessonBookingDTO {

    private int studentId;
    private int teacherId;
    private int subjectId;
    private LocalDateTime dateTime;
    private String description;
    private boolean doubleLesson;

    public LessonBookingDTO(int studentId, int teacherId, int subjectId, LocalDateTime dateTime, String description, boolean doubleLesson) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.dateTime = dateTime;
        this.description = description;
        this.doubleLesson = doubleLesson;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDoubleLesson() {
        return doubleLesson;
    }

    public void setDoubleLesson(boolean doubleLesson) {
        this.doubleLesson = doubleLesson;
    }
}
