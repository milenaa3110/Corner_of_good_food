package com.example.backend.models;

public class Teacher {

        private String username;

        private String cv_path;

        private String[] teaching_age_group;
        private String referral_source;
        private Subject[] subject;

        public Teacher(String username, String cv_path, String[] teaching_age_group, String referral_source, Subject[] subject) {
            this.username = username;
            this.cv_path = cv_path;
            this.teaching_age_group = teaching_age_group;
            this.referral_source = referral_source;
            this.subject = subject;
        }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCv_path() {
        return cv_path;
    }

    public void setCv_path(String cv_path) {
        this.cv_path = cv_path;
    }

    public String[] getTeaching_age_group() {
        return teaching_age_group;
    }

    public void setTeaching_age_group(String[] teaching_age_group) {
        this.teaching_age_group = teaching_age_group;
    }

    public String getReferral_source() {
        return referral_source;
    }

    public void setReferral_source(String referral_source) {
        this.referral_source = referral_source;
    }

    public Subject[] getSubject() {
        return subject;
    }

    public void setSubject(Subject[] subject) {
        this.subject = subject;
    }
}
