//package org.example.sellingcourese.Model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "Lessons")
//public class Lesson {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, length = 255)
//    private String title;
//
//    @Column(nullable = false, columnDefinition = "text")
//    private String content;
//
//    @Column(name = "video_url")
//    private String videoUrl;
//
//    // Thêm cột course_id để lưu ID khóa học
//    @Column(name = "course_id", nullable = false)
//    private Long courseId;
//
//    // Quan hệ Many-to-One với Course, chỉ ánh xạ, không quản lý khóa ngoại
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "course_id", insertable = false, updatable = false)
//    private Course course;
//}
