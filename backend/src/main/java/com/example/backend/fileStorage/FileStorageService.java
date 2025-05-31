package com.example.backend.fileStorage;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.UUID;

public class FileStorageService {

    private final Path rootLocation = Paths.get("C:\\Users\\Milena\\Documents\\fakultet\\AKTUELNO\\PIA\\projekat\\pia_project\\projekat\\slike"); // Define the root location where files will be stored

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }


    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file");
            }
            String filename = generateUniqueFileName(file.getOriginalFilename()); // Ensure filename is unique
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
            return filename; // Return the filename or path for storing in the database
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        String fileExtension = "";
        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot > 0) {
            fileExtension = originalFilename.substring(lastDot); // Includes the dot
        }
        String uuid = UUID.randomUUID().toString();
        return uuid + fileExtension;
    }

    public byte[] loadFileAsBytes(String filename) throws IOException {
        Path file = rootLocation.resolve(filename);
        if (Files.exists(file)) {
            return Files.readAllBytes(file);
        } else {
            throw new RuntimeException("File not found: " + filename);
        }
    }
}
