
package org.example.sellingcourese.Controller;

import org.example.sellingcourese.Model.Course;
import org.example.sellingcourese.Service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/public/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // Get all courses
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Course> addCourse(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("teacherId") Long teacherId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "video", required = false) MultipartFile video) {

        // Log các file nhận được
        if (thumbnail != null) {
            System.out.println("Received thumbnail: " + thumbnail.getOriginalFilename());
        } else {
            System.out.println("No thumbnail file received");
        }

        if (video != null) {
            System.out.println("Received video: " + video.getOriginalFilename());
        } else {
            System.out.println("No video file received");
        }

        Course course = courseService.addCourseWithFiles(title, description, price, teacherId, categoryId, thumbnail, video);
        return ResponseEntity.ok(course);
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