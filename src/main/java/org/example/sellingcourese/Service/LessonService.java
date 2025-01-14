//// LessonService.java
//package org.example.sellingcourese.Service;
//
//import org.example.sellingcourese.Model.Lesson;
//import org.example.sellingcourese.repository.LessonRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class LessonService {
//
//    @Autowired
//    private LessonRepository lessonRepository;
//
//    // Get all lessons
//    public List<Lesson> getAllLessons() {
//        return lessonRepository.findAll();
//    }
//
//    // Add a new lesson
//    public Lesson addLesson(Lesson lesson) {
//        return lessonRepository.save(lesson);
//    }
//
//    // Update an existing lesson
//    public Lesson updateLesson(Long id, Lesson lessonDetails) {
//        Optional<Lesson> optionalLesson = lessonRepository.findById(id);
//        if (optionalLesson.isPresent()) {
//            Lesson lesson = optionalLesson.get();
//            lesson.setTitle(lessonDetails.getTitle());
//            lesson.setContent(lessonDetails.getContent());
//            lesson.setVideoUrl(lessonDetails.getVideoUrl());
//            lesson.setCourseId(lessonDetails.getCourseId());
//            return lessonRepository.save(lesson);
//        } else {
//            throw new RuntimeException("Lesson not found with ID: " + id);
//        }
//    }
//
//    // Delete a lesson
//    public void deleteLesson(Long id) {
//        if (lessonRepository.existsById(id)) {
//            lessonRepository.deleteById(id);
//        } else {
//            throw new RuntimeException("Lesson not found with ID: " + id);
//        }
//    }
//
//    // Find lesson by ID
//    public Lesson getLessonById(Long id) {
//        return lessonRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Lesson not found with ID: " + id));
//    }
//
//    // Find lessons by title
//    public List<Lesson> findLessonsByTitle(String title) {
//        return lessonRepository.findByTitleContainingIgnoreCase(title);
//    }
//}
