
package org.example.sellingcourese.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.example.sellingcourese.Model.Course;
import org.example.sellingcourese.repository.CartDetailRepository;
import org.example.sellingcourese.repository.CourseRepository;
import org.example.sellingcourese.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class CourseService {
    private static final Logger log = LoggerFactory.getLogger(CourseService.class);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String FOLDER_ID = "1SarwgB_52SplTKxlmCvL-ilBwMOKR5Ta"; // Correct Folder ID
    private static final String CREDENTIALS_FILE_PATH = "D:\\Project\\Nam4_hk1\\SellingCourese\\res.json";

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private OrderDetailRepository orderItemRepository;
    // Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    // Helper method to get Google credentials path
//    private String getPathToGoogleCredentials() {
//        try {
//            String currentDirectory = System.getProperty("user.dir");
//            Path filePath = Paths.get(currentDirectory, CREDENTIALS_FILE_PATH);
//            return filePath.toString();
//        } catch (Exception e) {
//            log.error("Failed to resolve Google credentials path: {}", e.getMessage(), e);
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error resolving credentials path", e);
//        }
//    }
    public String getPathToGoogleCredentials() {
        try {
            Resource resource = new ClassPathResource("res.json");

            // Kiểm tra xem file có tồn tại không
            if (!resource.exists()) {
                throw new FileNotFoundException("File res.json not found in classpath");
            }

            // Nếu chạy trong JAR, sử dụng InputStream
            if (resource.isFile()) {
                return resource.getFile().getAbsolutePath();
            } else {
                // Tạo một file tạm thời và sao chép nội dung
                File tempFile = File.createTempFile("res", ".json");
                try (InputStream inputStream = resource.getInputStream()) {
                    Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                return tempFile.getAbsolutePath();
            }
        } catch (IOException e) {
            log.error("Failed to resolve Google credentials path: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error resolving credentials path", e);
        }
    }

    // Create Google Drive service
    private Drive createDriveService() {
        try {
            String credentialsPath = getPathToGoogleCredentials();
            GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(credentialsPath))
                    .createScoped(Collections.singleton(DriveScopes.DRIVE));

            return new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    credential
            ).setApplicationName("SellingCourse").build();
        } catch (IOException e) {
            log.error("Failed to create Google Drive service due to IO error: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create Google Drive service", e);
        } catch (GeneralSecurityException e) {
            log.error("Security error while creating Google Drive service: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Security error during Google Drive initialization", e);
        }
    }

    // Upload file to Google Drive
    private String uploadFileToDrive(File file, String mimeType) {
        try {
            Drive driveService = createDriveService();

            // Create file metadata
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(file.getName());
            fileMetadata.setParents(Collections.singletonList(FOLDER_ID));

            // Create file content
            FileContent mediaContent = new FileContent(mimeType, file);

            // Upload file
            com.google.api.services.drive.model.File uploadedFile = driveService.files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id, webContentLink")
                    .execute();

            // Make the file publicly accessible
            com.google.api.services.drive.model.Permission permission = new com.google.api.services.drive.model.Permission()
                    .setType("anyone")
                    .setRole("reader");

            driveService.permissions().create(uploadedFile.getId(), permission).execute();

            log.info("File uploaded successfully: {}", uploadedFile.getWebContentLink());
            return "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();
        } catch (IOException e) {
            log.error("IO error during file upload: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed due to IO error", e);
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during file upload", e);
        }
    }

    // Save MultipartFile to Drive
    private String saveMultipartFileToDrive(MultipartFile multipartFile, String mimeType) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            log.warn("Attempted to upload an empty file");
            return null;
        }

        try {
            // Create temporary file
            String originalFilename = multipartFile.getOriginalFilename();
            File tempFile = File.createTempFile("upload-", "-" + (originalFilename != null ? originalFilename : "file"));

            // Write content to temp file
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(multipartFile.getBytes());
            }

            // Determine correct MIME type
            String actualMimeType = mimeType;
            if (originalFilename != null) {
                if (originalFilename.toLowerCase().endsWith(".jpg") || originalFilename.toLowerCase().endsWith(".jpeg")) {
                    actualMimeType = "image/jpeg";
                } else if (originalFilename.toLowerCase().endsWith(".png")) {
                    actualMimeType = "image/png";
                } else if (originalFilename.toLowerCase().endsWith(".mp4")) {
                    actualMimeType = "video/mp4";
                }
            }

            // Upload file
            String fileUrl = uploadFileToDrive(tempFile, actualMimeType);

            // Delete temp file
            if (!tempFile.delete()) {
                log.warn("Temporary file could not be deleted: {}", tempFile.getAbsolutePath());
            }

            return fileUrl;
        } catch (IOException e) {
            log.error("Failed to save MultipartFile: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process file", e);
        }
    }

    // Add course with files
    public Course addCourseWithFiles(String title, String description, BigDecimal price, Long teacherId, Long categoryId,
                                     MultipartFile thumbnail, MultipartFile video) {
        try {
            String thumbnailUrl = saveMultipartFileToDrive(thumbnail, "image/jpeg");
            String videoUrl = saveMultipartFileToDrive(video, "video/mp4");

            Course course = new Course();
            course.setTitle(title);
            course.setDescription(description);
            course.setPrice(price);
            course.setTeacherId(teacherId);
            course.setCategoryId(categoryId);
            course.setThumbnailUrl(thumbnailUrl);
            course.setVideoUrl(videoUrl);
            course.setStatus(1);

            return courseRepository.save(course);
        } catch (ResponseStatusException e) {
            log.error("Error while adding course: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding course: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error while adding course", e);
        }
    }
    // Update course with files
    public Course updateCourseWithFiles(Long id, String title, String description, BigDecimal price, Long teacherId,
                                        Long categoryId, MultipartFile thumbnail, MultipartFile video) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setTitle(title);
            course.setDescription(description);
            course.setPrice(price);
            course.setTeacherId(teacherId);
            course.setCategoryId(categoryId);

            if (thumbnail != null && !thumbnail.isEmpty()) {
                course.setThumbnailUrl(saveMultipartFileToDrive(thumbnail, "image/jpeg"));
            }

            if (video != null && !video.isEmpty()) {
                course.setVideoUrl(saveMultipartFileToDrive(video, "video/mp4"));
            }

            return courseRepository.save(course);
        } else {
            throw new RuntimeException("Course not found with ID: " + id);
        }
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with ID: " + id);
        }

        // Xóa các CartDetails liên quan đến Course
        cartDetailRepository.deleteByCourseID(id);

        // Xóa các OrderItems liên quan đến Course
        orderItemRepository.deleteByCourseId(id);

        // Cuối cùng, xóa Course
        courseRepository.deleteById(id);
    }

    // Get course by ID
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
    }

    // Find courses by title
    public List<Course> findCoursesByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

    // Get courses by status
    public List<Course> getCoursesByStatus(Integer status) {
        return courseRepository.findByStatus(status);
    }

    // Update course status
    public Course updateCourseStatus(Long id, Integer status) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setStatus(status);
            return courseRepository.save(course);
        } else {
            throw new RuntimeException("Course not found with ID: " + id);
        }
    }

    public Course updateCancelReason(Long id, String cancelReason, Integer status) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setStatus(status);
            course.setCancelReason(cancelReason);
            return courseRepository.save(course);
        } else {
            throw new RuntimeException("Course not found with ID: " + id);
        }
    }




}

