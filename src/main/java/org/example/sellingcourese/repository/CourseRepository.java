package org.example.sellingcourese.repository;

import org.example.sellingcourese.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTitleContainingIgnoreCase(String title);
    List<Course> findByStatus(Integer status);
    List<Course> findByCategoryId(Long categoryId);

}
