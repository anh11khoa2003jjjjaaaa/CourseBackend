package org.example.sellingcourese.Service;

import org.example.sellingcourese.Model.Category;
import org.example.sellingcourese.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // 1. Lấy tất cả danh mục
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 2. Thêm danh mục mới
    public Category addCategory(Category category) {
        // Kiểm tra xem tên danh mục đã tồn tại hay chưa
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
        if (existingCategory.isPresent()) {
            throw new IllegalStateException("Danh mục '" + category.getName() + "' đã tồn tại!");
        }
        return categoryRepository.save(category);
    }

    // 3. Sửa danh mục
    public Category updateCategory(Long id, Category updatedCategory) {
        Optional<Category> existingCategory = categoryRepository.findById(id);
        if (existingCategory.isEmpty()) {
            throw new IllegalStateException("Danh mục với ID " + id + " không tồn tại!");
        }

        Category category = existingCategory.get();
        category.setName(updatedCategory.getName());
        return categoryRepository.save(category);
    }

    // 4. Xóa danh mục
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalStateException("Danh mục với ID " + id + " không tồn tại!");
        }
        categoryRepository.deleteById(id);
    }

    // 5. Tìm danh mục theo ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Danh mục với ID " + id + " không tồn tại!"));
    }

    // 6. Tìm danh mục theo tên (không phân biệt hoa thường)
    public List<Category> getCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }
}
