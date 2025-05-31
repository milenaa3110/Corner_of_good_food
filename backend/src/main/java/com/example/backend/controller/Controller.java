package com.example.backend.controller;

import com.example.backend.db.Repo;
import com.example.backend.fileStorage.FileStorageService;
import com.example.backend.models.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/requests")
@CrossOrigin(origins = "http://localhost:4200")
public class Controller {

    private final FileStorageService storageService = new FileStorageService();
    @GetMapping("getType")
    public ResponseEntity<String> getType(@RequestParam String username) {
        String type = new Repo().getType(username);
        return ResponseEntity.ok(type);
    }

    @PostMapping("/registerUser")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        return new Repo().registerUser(user);
    }

    @PostMapping("/registerStudent")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) {
        return new Repo().registerStudent(student);
    }

    @PostMapping("/loginStudent")
    public ResponseEntity<?> loginStudent(@RequestBody User user) {
        return new Repo().loginStudent(user);
    }

    @PostMapping(value = "/savePicture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> savePicture(@RequestParam("username") String username, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        return new Repo().savePicture(username, file);
    }

    @PostMapping("/updateStudent")
    public ResponseEntity<String> updateStudent(@RequestBody StudentUserDbo studentUserDbo) {
        return new Repo().updateStudent(studentUserDbo);
    }

    //write method for saving pdf
    @PostMapping(value = "/saveCv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveCv(@RequestParam("username") String username, @RequestParam(value = "cvFile", required = false) MultipartFile file) throws IOException {
        return new Repo().savePdf(username, file);
    }

    @PostMapping("/registerTeacher")
    public ResponseEntity<String> registerTeacher(@RequestBody Teacher teacher) {
        return new Repo().registerTeacher(teacher);
    }

    @PostMapping("/loginTeacher")
    public ResponseEntity<?> loginTeacher(@RequestBody User user) {
        return new Repo().loginTeacher(user);
    }

    @GetMapping("/getSubjects")
    public ResponseEntity<?> getSubjects() {
        return new Repo().getSubjects();
    }
    //login admin
    @PostMapping("/loginAdmin")
    public ResponseEntity<?> loginAdmin(@RequestBody User user) {
        return new Repo().loginAdmin(user);
    }

    @GetMapping(value = "/pdfs/{filename:.+}", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] getPdfFile(@PathVariable String filename) throws IOException {
        return storageService.loadFileAsBytes(filename);
    }
    @GetMapping("/getRequests")
    public ResponseEntity<?> getRequests() {
        return new Repo().getRequests();
    }

    @PostMapping("/updateRequest")
    public ResponseEntity<String> updateRequest(@RequestParam int request_id, @RequestParam String status) {
        return new Repo().updateRequest(request_id, status);
    }

    //get all teachers sorted by subject
    @GetMapping("/getTeachersWithSubject")
    public ResponseEntity<?> getTeachersWithSubject() {
        return new Repo().getTeachersWithSubject();
    }

    //addBookingProposal
    @PostMapping("/addClassProposal")
    public ResponseEntity<String> addBookingProposal(@RequestBody LessonBookingDTO lessonBookingDTO) {
        return new Repo().addBookingProposal(lessonBookingDTO);
    }

    @GetMapping(value = "/photos/{filename:.+}", produces = MediaType.IMAGE_JPEG_VALUE) // Adjust MediaType based on your needs
    public byte[] getFile(@PathVariable String filename) throws IOException {
        return storageService.loadFileAsBytes(filename);
    }

    @GetMapping("/getClassWithStatus")
    public ResponseEntity<?> getClassWithStatus(@RequestParam String status) {
        return new Repo().getClassWithStatus(status);
    }
}
