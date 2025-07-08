package Controller;

import Services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    EmailService emailService;

    @PostMapping("/file/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fixedFileName = "Resume.pdf";
        Path uploadPath = Paths.get("Resume").toAbsolutePath();

        try {
            // Create the uploads directory if it doesn't exist
            Files.createDirectories(uploadPath);

            // Save file as "Resume.pdf" regardless of original name
            Path targetFile = uploadPath.resolve(fixedFileName);

            // Override existing file if present
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return ResponseEntity.ok("File uploaded and renamed to Resume.pdf successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed due to server error.");
        }
    }


    @PostMapping("/message/save")
    public ResponseEntity<String> saveMessage(@RequestParam("message") String message) {
        try {
            Path path = Paths.get("CustomMessage/CustomMessage.txt");
            Files.createDirectories(path.getParent());
            Files.writeString(path, message);
            return ResponseEntity.ok("Message saved.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error saving message.");
        }
    }

    @PostMapping("/draft")
    public ResponseEntity<String> draftEmail(@RequestParam String toEmail) {
        try {
            String draftId = emailService.createDraft(toEmail);
            return ResponseEntity.ok("Draft created with ID: " + draftId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create draft.");
        }
    }

}
