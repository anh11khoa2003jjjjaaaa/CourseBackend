//// LessonController.java
//package org.example.sellingcourese.Controller;
//
//import org.example.sellingcourese.Model.Lesson;
//import org.example.sellingcourese.Service.LessonService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/public/lessons")
//public class LessonController {
//
//    @Autowired
//    private LessonService lessonService;
//
//    // Get all lessons
//    @GetMapping
//    public List<Lesson> getAllLessons() {
//        return lessonService.getAllLessons();
//    }
//
//    // Get lesson by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<Lesson> getLessonById(@PathVariable Long id) {
//        return ResponseEntity.ok(lessonService.getLessonById(id));
//    }
//
//    // Find lessons by title
//    @GetMapping("/search")
//    public List<Lesson> findLessonsByTitle(@RequestParam String title) {
//        return lessonService.findLessonsByTitle(title);
//    }
//
//    // Add a new lesson
//    @PostMapping
//    public Lesson addLesson(@RequestBody Lesson lesson) {
//        return lessonService.addLesson(lesson);
//    }
//
//    // Update a lesson
//    @PutMapping("/{id}")
//    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id, @RequestBody Lesson lessonDetails) {
//        return ResponseEntity.ok(lessonService.updateLesson(id, lessonDetails));
//    }
//
//    // Delete a lesson
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
//        lessonService.deleteLesson(id);
//        return ResponseEntity.noContent().build();
//    }
//}