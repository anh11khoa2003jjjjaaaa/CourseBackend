package org.example.sellingcourese.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long rating;

    @Column(columnDefinition = "text",nullable = true)
    private String comment;

    // Cột user_id để lưu ID người dùng
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Quan hệ với User, chỉ ánh xạ, không quản lý khóa ngoại
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // Cột course_id để lưu ID khóa học
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    // Quan hệ với Course, chỉ ánh xạ, không quản lý khóa ngoại
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;


}
