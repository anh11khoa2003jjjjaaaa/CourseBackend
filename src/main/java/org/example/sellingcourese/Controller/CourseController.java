
package org.example.sellingcourese.Controller;

import org.example.sellingcourese.Model.Course;
import org.example.sellingcourese.Service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("https://course-ui.vercel.app")
@RequestMapping("/public/courses")
public class CourseController {
    private static final Logger log = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    private CourseService courseService;

    // Get all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> addCourse(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("teacherId") Long teacherId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "video", required = false) MultipartFile video) {

        try {
            // Validate input fields
            if (title == null || title.isBlank()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Title is required."));
            }
            if (description == null || description.isBlank()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Description is required."));
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("Price must be greater than zero."));
            }
            if (teacherId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Teacher ID is required."));
            }
            if (categoryId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Category ID is required."));
            }

            // Log information for file uploads
            log.info("Received request to add course with title: {}", title);
            if (thumbnail != null && !thumbnail.isEmpty()) {
                log.info("Thumbnail received: {}", thumbnail.getOriginalFilename());
            } else {
                log.info("No thumbnail file received");
            }
            if (video != null && !video.isEmpty()) {
                log.info("Video received: {}", video.getOriginalFilename());
            } else {
                log.info("No video file received");
            }

            // Call service to add course
            Course course = courseService.addCourseWithFiles(title, description, price, teacherId, categoryId, thumbnail, video);
            log.info("Course added successfully with ID: {}", course.getId());
            return ResponseEntity.ok(course);

        } catch (RuntimeException e) {
            // Application-specific error handling (e.g., upload failure, database error)
            log.error("Application error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("An error occurred while processing your request: " + e.getMessage()));
        } catch (Exception e) {
            // Unexpected error handling
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("An unexpected error occurred. Please try again later."));
        }
    }


    // Hàm tạo lỗi chi tiết
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("error", message);
        return errorResponse;
    }


    // Update an existing course
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("teacherId") Long teacherId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "video", required = false) MultipartFile video) {

        Course updatedCourse = courseService.updateCourseWithFiles(id, title, description, price, teacherId, categoryId, thumbnail, video);
        return ResponseEntity.ok(updatedCourse);
    }

    // Delete a course
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok().build();
    }

    // Get a course by ID
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    // Find courses by title
    @GetMapping("/search")
    public List<Course> findCoursesByTitle(@RequestParam String title) {
        return courseService.findCoursesByTitle(title);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Course> updateCourseStatus(@PathVariable Long id) {
        Course updatedCourse = courseService.updateCourseStatus(id, 0);
        return ResponseEntity.ok(updatedCourse);
    }

    @PutMapping("/cancelReason/{id}")
    public ResponseEntity<Course>updateReason(@PathVariable Long id, @RequestParam String reason) {
        Course updateCancel=courseService.updateCancelReason(id,reason,2);
        return ResponseEntity.ok(updateCancel);
    }
    @GetMapping("/approved")
    public ResponseEntity<List<Course>> getApprovedCourses() {
        List<Course> approvedCourses = courseService.getCoursesByStatus(0);
        return ResponseEntity.ok(approvedCourses);

    }
}