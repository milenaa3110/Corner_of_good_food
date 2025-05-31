package com.example.backend.db;

import com.example.backend.fileStorage.FileStorageService;
import com.example.backend.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Repo {

    public boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z]{3,})(?=.*\\d)(?=.*[@#$%^&+=]).{6,10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean existsByUsername(String username) {
        try (Connection conn = DB.source().getConnection(); PreparedStatement stm = conn.prepareStatement("SELECT * FROM user WHERE username=?")) {
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByEmail(String email) {
        try (Connection conn = DB.source().getConnection(); PreparedStatement stm = conn.prepareStatement("SELECT * FROM user WHERE email=?")) {
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResponseEntity<String> registerUser(User user) {
        try (Connection conn = DB.source().getConnection(); PreparedStatement stm = conn.prepareStatement("INSERT INTO user (username, password_hash, security_question, security_answer, first_name, last_name, gender, address, phone, email, type, profile_picture_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            String password = user.getPassword();

            //provera greski
            if (!isValidPassword(user.getPassword()))
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Password is not in a valid format");
            if (existsByUsername(user.getUsername()))
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Username already exists");
            if (existsByEmail(user.getEmail()))
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email already exists");

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);
            stm.setString(1, user.getUsername());
            stm.setString(2, hashedPassword);
            stm.setString(3, user.getSecurity_question());
            stm.setString(4, user.getSecurity_answer());
            stm.setString(5, user.getFirst_name());
            stm.setString(6, user.getLast_name());
            stm.setString(7, String.valueOf(user.getGender()));
            stm.setString(8, user.getAddress());
            stm.setString(9, user.getPhone());
            stm.setString(10, user.getEmail());
            stm.setString(11, user.getType());
            stm.setString(12, "default.jpg");
            stm.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.toString());
        }
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("OK");
    }

    public ResponseEntity<String> validatePdf(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (!("application/pdf".equals(contentType))) {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("File must be in PDF format");
        }

        if (file.getSize() > 3000000) {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("File is too large. Maximum size is 3MB.");
        }
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("OK");
    }

    public String validateProfilePicture(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            return "File must be in JPG or PNG format";
        }

        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();

            if (width < 100 || height < 100) {
                return "dimensions are too small. Minimum size is 100x100px.";
            }

            if (width > 300 || height > 300) {
                return "Image dimensions are too large. Maximum size is 300x300px.";
            }
        } else {
            return "Invalid image file.";
        }
        return "OK";
    }

    public ResponseEntity<String> savePicture(String username, MultipartFile file) {
        try (Connection conn = DB.source().getConnection(); PreparedStatement stm = conn.prepareStatement("UPDATE `projekat_pia2023`.`user`\n" + "SET\n" + "profile_picture_path = ?\n" + "WHERE username = ?;")) {

            if (file == null) {
                return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body("OK");
            } else {
                String status = validateProfilePicture(file);
                if (status != "OK") {
                    return ResponseEntity
                            .status(HttpStatus.ACCEPTED)
                            .body(status);
                }
                FileStorageService fileStorageService = new FileStorageService();
                String path = fileStorageService.store(file);
                stm.setString(1, path);
            }
            stm.setString(2, username);
            stm.executeUpdate();
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("OK");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    int findIdByUsername(String username) {
        try (Connection conn = DB.source().getConnection(); PreparedStatement stm = conn.prepareStatement("SELECT * from user where username=?")) {
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.toString());
        }
        return -1;
    }

    public ResponseEntity<String> registerStudent(Student student) {
        try (Connection conn = DB.source().getConnection(); PreparedStatement stm = conn.prepareStatement("INSERT INTO `projekat_pia2023`.`student`\n" + "(`user_id`,\n" + "`school_type`,\n" + "`grade`)\n" + "VALUES\n" + "(?,\n" + "?,\n" + "?);\n")) {
            int id = findIdByUsername(student.getUsername());
            if (id == -1) return ResponseEntity.status(HttpStatus.ACCEPTED).body("Person with this id doesn't exist");
            stm.setInt(1, id);
            stm.setString(2, student.getSchool_type());
            stm.setInt(3, student.getGrade());
            stm.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.toString());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(e.toString());
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("OK");
    }

    public String getType(String username) {
        try (Connection conn = DB.source().getConnection(); PreparedStatement stm = conn.prepareStatement("SELECT type from user where username=?")) {
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type");
                return type;
            } else return "Wrong username";
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.toString());
        }
        return null;

    }

    public ResponseEntity<String> updateStudent(StudentUserDbo studentUserDbo) {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement checkEmailStm = conn.prepareStatement("SELECT * from user where username=?");
             PreparedStatement stm = conn.prepareStatement("UPDATE user SET first_name = ?, last_name = ?, address = ?, phone = ?, email = ? WHERE username = ?;");
             PreparedStatement stm1 = conn.prepareStatement("UPDATE student SET school_type = ?, grade = ? WHERE user_id = ?;");
        ) {
            checkEmailStm.setString(1, studentUserDbo.getUsername());
            ResultSet rs1 = checkEmailStm.executeQuery();
            if (rs1.next()) {
                String emailOld = rs1.getString("email");
                String email = studentUserDbo.getEmail();
                if (!Objects.equals(email, emailOld) && existsByEmail(email)) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("Email already exists");
                }
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("User doesn't exist");
            }


            stm.setString(1, studentUserDbo.getFirst_name());
            stm.setString(2, studentUserDbo.getLast_name());
            stm.setString(3, studentUserDbo.getAddress());
            stm.setString(4, studentUserDbo.getPhone());
            stm.setString(5, studentUserDbo.getEmail());
            stm.setString(6, studentUserDbo.getUsername());
            stm.executeUpdate();
            int id = findIdByUsername(studentUserDbo.getUsername());
            stm1.setString(1, studentUserDbo.getSchool_type());
            stm1.setInt(2, studentUserDbo.getGrade());
            stm1.setInt(3, id);
            stm1.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Successfully updated!");
    }

    public ResponseEntity<String> savePdf(String username, MultipartFile file) {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement("UPDATE teacher SET cv_path = ? WHERE user_id = ?;");
             PreparedStatement stm1 = conn.prepareStatement("UPDATE request SET cv_path = ? WHERE teacher_id = ?;")
        ) {
            if (file == null) {
                return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body("You didn't upload a file");
            } else {
                ResponseEntity<String> status = validatePdf(file);

                if (status.getBody() != "OK") {
                    return ResponseEntity
                            .status(HttpStatus.ACCEPTED)
                            .body(status.getBody());
                }
                FileStorageService fileStorageService = new FileStorageService();
                String path = fileStorageService.store(file);
                stm.setString(1, path);
                stm1.setString(1, path);
            }
            int id = findIdByUsername(username);
            stm.setInt(2, id);
            stm1.setInt(2, id);
            stm.executeUpdate();
            stm1.executeUpdate();
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("OK");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<String> registerTeacher(Teacher teacher) {

        try (
                Connection conn = DB.source().getConnection();
                PreparedStatement stm = conn.prepareStatement("Insert into teacher (user_id, cv_path, referral_source) values (?, ?, ?)");
                PreparedStatement stm1 = conn.prepareStatement("Insert into teacheragegroup (user_id, age_group_id) values (?, ?)");
                PreparedStatement stm2 = conn.prepareStatement("Select age_group_id from `teachingagegroup` where age_group_name=?");
                PreparedStatement stm3 = conn.prepareStatement("Insert into teaches (teacher_id, subject_id) values (?, ?)");
                PreparedStatement stm4 = conn.prepareStatement("SELECT id from subjects where name=?");
                PreparedStatement stm5 = conn.prepareStatement("INSERT into request (teacher_id, teacher_name, cv_path, status) values (?, ?, ?, ?)");
        ) {

            int id = findIdByUsername(teacher.getUsername());
            if (id == -1) return ResponseEntity.status(HttpStatus.ACCEPTED).body("Person with this id doesn't exist");
            stm.setInt(1, id);
            stm.setString(2, "default");
            stm.setString(3, teacher.getReferral_source());
            stm.executeUpdate();
            for (String ageGroup : teacher.getTeaching_age_group()) {
                stm1.setInt(1, id);
                stm2.setString(1, ageGroup);
                ResultSet rs = stm2.executeQuery();
                if (rs.next()) {
                    int ageGroupId = rs.getInt("age_group_id");
                    stm1.setInt(2, ageGroupId);
                }
                stm1.executeUpdate();
            }
            for (Subject subject : teacher.getSubject()) {
                stm3.setInt(1, id);
                stm4.setString(1, subject.getName());
                ResultSet rs = stm4.executeQuery();
                if (rs.next()) {
                    int subjectId = rs.getInt("id");
                    stm3.setInt(2, subjectId);
                }
                stm3.executeUpdate();
            }
            stm5.setInt(1, id);
            stm5.setString(2, teacher.getUsername());
            stm5.setString(3, "default");
            stm5.setString(4, "pending");
            stm5.executeUpdate();


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println(e.toString());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(e.toString());
        }
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("OK");
    }

    public ResponseEntity<?> loginStudent(User user) {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * from user where username=?");
             PreparedStatement stm1 = conn.prepareStatement("SELECT * from student where user_id=?");
        ) {
            stm.setString(1, user.getUsername());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String password = rs.getString("password_hash");
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                if (passwordEncoder.matches(user.getPassword(), password)) {
                    stm1.setInt(1, rs.getInt("id"));
                    ResultSet rs1 = stm1.executeQuery();
                    if (rs1.next()) {
                        StudentUserDbo studentUserDbo = new StudentUserDbo(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("gender"),
                                rs.getString("address"),
                                rs.getString("phone"),
                                rs.getString("email"),
                                rs.getString("profile_picture_path"),
                                rs1.getString("school_type"),
                                rs1.getInt("grade"));
                        return ResponseEntity
                                .status(HttpStatus.ACCEPTED)
                                .body(studentUserDbo);
                    } else {
                        return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body("Student doesn't exist");
                    }
                }
                //Can you make this message in json format?


                else {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Wrong password");
                    return ResponseEntity
                            .status(HttpStatus.ACCEPTED)
                            .body(response);
                }
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Wrong password");
                return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body(response);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getSubjects() {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * from subjects");
        ) {
            ResultSet rs = stm.executeQuery();
            ArrayList<Subject> subjects = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Subject subject = new Subject(id, name);
                subjects.add(subject);
            }
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(subjects);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
    }

    //get all subjects of one teacher
    public ResponseEntity<?> getSubjectOfTeacher(int id) {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * from subjects where id in (select subject_id from teaches where teacher_id=?)");
        ) {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            ArrayList<Subject> subjects = new ArrayList<>();
            while (rs.next()) {
                int subjectId = rs.getInt("id");
                String name = rs.getString("name");
                Subject subject = new Subject(subjectId, name);
                subjects.add(subject);
            }
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(subjects);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
    }   //get all age groups of one teacher

    public ResponseEntity<?> getAllAgeGroupsOfTeacher(int id) {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * from teachingagegroup where age_group_id in (select age_group_id from teacheragegroup where user_id=?)");
        ) {
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            ArrayList<String> ageGroups = new ArrayList<>();
            while (rs.next()) {
                int ageGroupId = rs.getInt("age_group_id");
                String name = rs.getString("age_group_name");
                ageGroups.add(name);
            }
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(ageGroups);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
    }

    public ResponseEntity<?> loginTeacher(User user) {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * from request where teacher_name=?");
             PreparedStatement stm1 = conn.prepareStatement("SELECT * from user where username=?");
             PreparedStatement stm2 = conn.prepareStatement("SELECT * from teacher where user_id=?");
        ) {
            stm.setString(1, user.getUsername());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if (Objects.equals(status, "pending")) {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Zahtev je u obradi");
                    return ResponseEntity
                            .status(HttpStatus.ACCEPTED)
                            .body(response);
                } else if (Objects.equals(status, "rejected")) {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Zahtev je odbijen");
                    return ResponseEntity
                            .status(HttpStatus.ACCEPTED)
                            .body(response);
                }
                stm1.setString(1, user.getUsername());
                ResultSet rs1 = stm1.executeQuery();
                if (rs1.next()) {
                    String password = rs1.getString("password_hash");
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    if (passwordEncoder.matches(user.getPassword(), password)) {
                        stm2.setInt(1, rs1.getInt("id"));
                        ResultSet rs2 = stm2.executeQuery();
                        if (rs2.next()) {
                            int id = rs1.getInt("id");
                            TeacherUserDbo teacherUserDbo = new TeacherUserDbo(
                                    id,
                                    rs1.getString("username"),
                                    rs1.getString("first_name"),
                                    rs1.getString("last_name"),
                                    rs1.getString("address"),
                                    rs1.getString("phone"),
                                    rs1.getString("email"),
                                    rs1.getString("type"),
                                    rs1.getString("profile_picture_path"),
                                    getSubjectOfTeacher(id).getBody() instanceof String ? null : (List<Subject>) getSubjectOfTeacher(id).getBody(),
                                    getAllAgeGroupsOfTeacher(id).getBody() instanceof String ? null : (List<String>) getAllAgeGroupsOfTeacher(id).getBody()
                            );
                            return ResponseEntity
                                    .status(HttpStatus.ACCEPTED)
                                    .body(teacherUserDbo);
                        } else {
                            Map<String, String> response = new HashMap<>();
                            response.put("message", "Učitelj ne postoji");
                            return ResponseEntity
                                    .status(HttpStatus.BAD_REQUEST)
                                    .body(response);
                        }
                    } else {
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Pogrešna lozinka");
                        return ResponseEntity
                                .status(HttpStatus.ACCEPTED)
                                .body(response);
                    }
                } else {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Korisnik ne postoji");
                    return ResponseEntity
                            .status(HttpStatus.ACCEPTED)
                            .body(response);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
        return null;
    }

    public ResponseEntity<?> loginAdmin(User user) {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * from administrators where username=?");
        ) {
            stm.setString(1, user.getUsername());
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String password = rs.getString("password");
                if (!Objects.equals(password, user.getPassword())) {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Pogrešna lozinka");
                    return ResponseEntity
                            .status(HttpStatus.ACCEPTED)
                            .body(response);
                } else {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "OK");
                    return ResponseEntity
                            .status(HttpStatus.ACCEPTED)
                            .body(response);
                }
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Korisnik ne postoji");
                return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body(response);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ResponseEntity<?> getRequests() {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * from request where status=?");
        ) {
            stm.setString(1, "pending");
            ResultSet rs = stm.executeQuery();
            ArrayList<Requests> requests = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("request_id");
                String teacherName = rs.getString("teacher_name");
                String cvPath = rs.getString("cv_path");
                String status = rs.getString("status");
                Requests request = new Requests(id, teacherName, cvPath, status);
                requests.add(request);
            }
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(requests);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
    }

    public ResponseEntity<String> updateRequest(int requestId, String updateStatus) {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement("UPDATE request SET status = ? WHERE request_id = ?;");
        ) {
            stm.setString(1, updateStatus);
            stm.setInt(2, requestId);
            stm.executeUpdate();
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("OK");
        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
    }

    private String isValidTime(LocalDateTime dateTime, int hours) {
        int minute = dateTime.get(ChronoField.MINUTE_OF_HOUR);
        int hour = dateTime.get(ChronoField.HOUR_OF_DAY);
        DayOfWeek day = dateTime.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) return "Učitelj ne radi tokom vikenda";
        else if (minute != 0 && minute != 30) return "Mora da se zakaže na pun sat ili na pola sata";
        else if (hour < 10 || hour > 18 - hours) return "Radno vreme učitelja je od 10 do 18"; //Cas traje 1h
        else return "OK";
    }

    public boolean isTeacherFreeAtTime(int teacherId, LocalDateTime dateTime, int durationHours) {
        LocalDateTime proposedEndTime = dateTime.plusHours(durationHours);

        String sql = "SELECT COUNT(*) FROM scheduledlessons WHERE teacher_id = ? " +
                "AND ((lesson_date_time < ? AND lesson_date_time + INTERVAL (1 + is_double_lesson) HOUR > ?) " +
                "OR (lesson_date_time >= ? AND lesson_date_time < ?))";

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, teacherId);
            stm.setTimestamp(2, Timestamp.valueOf(proposedEndTime));
            stm.setTimestamp(3, Timestamp.valueOf(dateTime));
            stm.setTimestamp(4, Timestamp.valueOf(dateTime));
            stm.setTimestamp(5, Timestamp.valueOf(proposedEndTime));

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;  // No overlapping classes
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean isTeacherFullyBookedForDay(int teacherId, LocalDate day) {
        LocalDateTime startOfWorkDay = day.atTime(10, 0);
        LocalDateTime endOfWorkDay = day.atTime(18, 0);

        List<LocalDateTime> classStartTimes = new ArrayList<>();
        Map<LocalDateTime, Integer> classDurations = new HashMap<>();

        String sql = "SELECT lesson_date_time, is_double_lesson FROM scheduledlessons " +
                "WHERE teacher_id = ? AND lesson_date_time BETWEEN ? AND ?";

        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement(sql)) {

            stm.setInt(1, teacherId);
            stm.setTimestamp(2, Timestamp.valueOf(startOfWorkDay));
            stm.setTimestamp(3, Timestamp.valueOf(endOfWorkDay));

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime startTime = rs.getTimestamp("lesson_date_time").toLocalDateTime();
                    int duration = rs.getInt("is_double_lesson") == 1 ? 120 : 60;
                    classStartTimes.add(startTime);
                    classDurations.put(startTime, duration);
                }
            }
        } catch (SQLException e) {
            // Handle exceptions
        }

        Collections.sort(classStartTimes);

        LocalDateTime lastEndTime = startOfWorkDay;
        for (LocalDateTime startTime : classStartTimes) {
            int duration = classDurations.get(startTime);
            LocalDateTime endTime = startTime.plusMinutes(duration);

            long gapMinutes = Duration.between(lastEndTime, startTime).toMinutes();
            if (gapMinutes > 30) {
                return false; // Found a gap larger than 30 minutes
            }

            lastEndTime = endTime.isAfter(lastEndTime) ? endTime : lastEndTime;
        }

        long endOfDayGap = Duration.between(lastEndTime, endOfWorkDay).toMinutes();
        return endOfDayGap <= 30; // Teacher is fully booked if the gap is 30 minutes or less
    }

    public boolean isTeacherFullyBookedForWeek(int teacherId, LocalDate weekStart) {
        LocalDate currentDay = weekStart;

        // Iterate through the week, Monday to Friday
        for (int i = 0; i < 5; i++) { // Assuming weekStart is a Monday
            if (!isTeacherFullyBookedForDay(teacherId, currentDay)) {
                return false; // Found a day where the teacher is not fully booked
            }
            currentDay = currentDay.plusDays(1);
        }

        return true; // Teacher is fully booked for all weekdays
    }

    public ResponseEntity<String> addBookingProposal(LessonBookingDTO lessonBookingDTO) {
        try {
            Connection conn = DB.source().getConnection();
            PreparedStatement stm = conn.prepareStatement("INSERT INTO `projekat_pia2023`.`scheduledlessons`(`student_id`,`teacher_id`,`subject_id`,`lesson_date_time`,`description`,`is_double_lesson`,`status`) VALUES (?,?,?,?,?,?,?);");
        String response = isValidTime(lessonBookingDTO.getDateTime(), lessonBookingDTO.isDoubleLesson() ? 2 : 1);
        if (response != "OK") {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(response);
        }
        if (isTeacherFullyBookedForWeek(lessonBookingDTO.getTeacherId(), lessonBookingDTO.getDateTime().toLocalDate())) {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Učitelj nije slobodan nijedan dan sledeće nedelje");
        }

        if (isTeacherFullyBookedForDay(lessonBookingDTO.getTeacherId(), lessonBookingDTO.getDateTime().toLocalDate())) {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Učitelj nije slobodan tokom celog dana");
        }

        if (!isTeacherFreeAtTime(lessonBookingDTO.getTeacherId(), lessonBookingDTO.getDateTime(), lessonBookingDTO.isDoubleLesson() ? 2 : 1)) {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Učitelj je zauzet u tom terminu");
        }
        stm.setInt(1, lessonBookingDTO.getStudentId());
        stm.setInt(2, lessonBookingDTO.getTeacherId());
        stm.setInt(3, lessonBookingDTO.getSubjectId());
        stm.setTimestamp(4, Timestamp.valueOf(lessonBookingDTO.getDateTime()));
        stm.setString(5, lessonBookingDTO.getDescription());
        stm.setBoolean(6, lessonBookingDTO.isDoubleLesson());
        stm.setString(7, "scheduled");
        stm.executeUpdate();
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("OK");
        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getTeachersWithSubject() {

        //get all teachers that teach a subject
        try {
            Connection conn = DB.source().getConnection();
            PreparedStatement stm = conn.prepareStatement("SELECT DISTINCT Subjects.id, Subjects.name FROM Subjects JOIN Teaches ON Subjects.id = Teaches.subject_id;");
            PreparedStatement stm1 = conn.prepareStatement("Select * FROM teaches where subject_id=?");
            PreparedStatement stm2 = conn.prepareStatement("Select * FROM teacher join user on teacher.user_id = user.id where user_id=?");
            ArrayList<SubjectWithTeacher> subjectsWithTeachers = new ArrayList<>();
            ResultSet rs = stm.executeQuery();
            while (rs.next()){
                ArrayList<TeacherUserDbo> teachers = new ArrayList<>();
                stm1.setInt(1, rs.getInt("id"));
                ResultSet rs1 = stm1.executeQuery();
                while (rs1.next()) {
                    stm2.setInt(1, rs1.getInt("teacher_id"));
                    ResultSet rs2 = stm2.executeQuery();
                    if (rs2.next()) {
                        int id = rs2.getInt("id");
                        TeacherUserDbo teacherUserDbo = new TeacherUserDbo(
                                id,
                                rs2.getString("username"),
                                rs2.getString("first_name"),
                                rs2.getString("last_name"),
                                rs2.getString("address"),
                                rs2.getString("phone"),
                                rs2.getString("email"),
                                rs2.getString("type"),
                                rs2.getString("profile_picture_path"),
                                getSubjectOfTeacher(id).getBody() instanceof String ? null : (List<Subject>) getSubjectOfTeacher(id).getBody(),
                                getAllAgeGroupsOfTeacher(id).getBody() instanceof String ? null : (List<String>) getAllAgeGroupsOfTeacher(id).getBody()
                        );
                        teachers.add(teacherUserDbo);
                    }
                }
                SubjectWithTeacher subjectWithTeacher = new SubjectWithTeacher(rs.getString("name"), teachers);
                subjectsWithTeachers.add(subjectWithTeacher);
            }
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(subjectsWithTeachers);

        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getClassWithStatus(String status) {
        try (Connection conn = DB.source().getConnection();
             PreparedStatement stm = conn.prepareStatement("SELECT * from scheduledlessons join user on scheduledlessons.teacher_id=user.id join subjects on scheduledlessons.subject_id = subjects.id where status=? order by lesson_date_time DESC");
        ) {
            stm.setString(1, status);
            ResultSet rs = stm.executeQuery();
            ArrayList<ClassWithStatus> classes = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String teacherName = rs.getString("first_name");
                String teacherSurname = rs.getString("last_name");
                LocalDateTime beginTime = rs.getTimestamp("lesson_date_time").toLocalDateTime();
                boolean isDoubleLesson = rs.getBoolean("is_double_lesson");
                String status1 = rs.getString("status");
                LocalDateTime endTime;
                if (isDoubleLesson) {
                    endTime = beginTime.plusHours(2);
                } else {
                    endTime = beginTime.plusHours(1);
                }
                ClassWithStatus classWithStatus = new ClassWithStatus(id, beginTime, endTime, teacherName, teacherSurname, new Subject(rs.getInt("subject_id"), rs.getString("name")), status1);
                classes.add(classWithStatus);
            }
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(classes);
        } catch (SQLException e) {
            System.out.println(e.toString());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error message: " + e.getMessage());
        }
    }
}
