
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // Helper method to get path to Google credentials
    private String getPathToGoogleCredentials() {
        String currentDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDirectory, CREDENTIALS_FILE_PATH );
        return filePath.toString();
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

            // Set file permissions to make it publicly accessible
            com.google.api.services.drive.model.File uploadedFile = driveService.files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id, webContentLink")
                    .execute();

            // Make the file publicly accessible
            com.google.api.services.drive.model.Permission permission = new com.google.api.services.drive.model.Permission()
                    .setType("anyone")
                    .setRole("reader");

            driveService.permissions().create(uploadedFile.getId(), permission).execute();

            // Return the direct download link
            return "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to Google Drive: " + e.getMessage(), e);
        }
    }


    // Create Google Drive service
    private Drive createDriveService() throws GeneralSecurityException, IOException {
        String credentialsPath = getPathToGoogleCredentials();

        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(credentialsPath))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential
        ).setApplicationName("SellingCourse").build();
    }

    // Save MultipartFile to a temporary file and upload it to Google Drive
//    private String saveMultipartFileToDrive(MultipartFile multipartFile, String mimeType) {
//        if (multipartFile == null || multipartFile.isEmpty()) {
//            return null;
//        }
//        try {
//            // Create a temporary file
//            File tempFile = File.createTempFile("upload-", "-" + multipartFile.getOriginalFilename());
//            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//                fos.write(multipartFile.getBytes());
//            }
//
//            // Upload file to Google Drive
//            String fileUrl = uploadFileToDrive(tempFile, mimeType);
//
//            // Delete the temporary file
//            tempFile.delete();
//
//            return fileUrl;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to process file: " + multipartFile.getOriginalFilename(), e);
//        }
//    }
    private String saveMultipartFileToDrive(MultipartFile multipartFile, String mimeType) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }
        try {
            // Create a temporary file with original file name
            String originalFilename = multipartFile.getOriginalFilename();
            File tempFile = File.createTempFile("upload-", "-" + (originalFilename != null ? originalFilename : "file"));

            // Write multipart file content to temp file
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(multipartFile.getBytes());
            }

            // Determine correct MIME type based on file extension
            String actualMimeType = mimeType;
            if (originalFilename != null) {
                String extension = originalFilename.toLowerCase();
                if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
                    actualMimeType = "image/jpeg";
                } else if (extension.endsWith(".png")) {
                    actualMimeType = "image/png";
                } else if (extension.endsWith(".mp4")) {
                    actualMimeType = "video/mp4";
                }
            }

            // Upload to Google Drive
            String fileUrl = uploadFileToDrive(tempFile, actualMimeType);

            // Clean up temp file
            if (!tempFile.delete()) {
                System.out.println("Warning: Temporary file could not be deleted");
            }

            return fileUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file: " + multipartFile.getOriginalFilename(), e);
        }
    }


    public Course addCourseWithFiles(String title, String description, BigDecimal price, Long teacherId, Long categoryId,
                                     MultipartFile thumbnail, MultipartFile video) {
        try {
            // Upload thumbnail
            String thumbnailUrl = null;
            if (thumbnail != null && !thumbnail.isEmpty()) {
                try {
                    thumbnailUrl = saveMultipartFileToDrive(thumbnail, "image/jpeg");
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload thumbnai: " + e.getMessage(), e);
                }
            }

            // Upload video
            String videoUrl = null;
            if (video != null && !video.isEmpty()) {
                try {
                    videoUrl = saveMultipartFileToDrive(video, "video/mp4");
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload video: " + e.getMessage(), e);
                }
            }

            // Create new course
            Course course = new Course();
            course.setTitle(title);
            course.setDescription(description);
            course.setPrice(price);
            course.setTeacherId(teacherId);
            course.setCategoryId(categoryId);
            course.setThumbnailUrl(thumbnailUrl);
            course.setVideoUrl(videoUrl);
            course.setStatus(1); // Default status

            try {
                return courseRepository.save(course);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save course to database: " + e.getMessage(), e);
            }
        } catch (RuntimeException e) {
            // Log error for debugging
            log.error("Error occurred while adding course: {}", e.getMessage(), e);
            throw e; // Rethrow to preserve original exception details
        } catch (Exception e) {
            // Handle unexpected exceptions
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred: " + e.getMessage(), e);
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

