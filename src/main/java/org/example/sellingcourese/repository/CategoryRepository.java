package org.example.sellingcourese.repository;

import org.example.sellingcourese.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Tìm danh mục theo tên
    Optional<Category> findByName(String name);

    // Tìm danh mục theo tên chứa từ khóa (không phân biệt hoa thường)
    List<Category> findByNameContainingIgnoreCase(String name);
}
