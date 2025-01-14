package org.example.sellingcourese.Service;

import org.example.sellingcourese.Model.User;
import org.example.sellingcourese.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 1. Lấy tất cả users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    // Lấy thông tin User theo AccountID
    public User getUserByAccountID(Long accountID) {
        return userRepository.findByAccountID(accountID);
    }
    // 2. Thêm user mới
    public User addUser(User user) {
        // Kiểm tra email hoặc số điện thoại đã tồn tại
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalStateException("Email '" + user.getEmail() + "' đã được sử dụng!");
        }
        return userRepository.save(user);
    }

    // 3. Sửa user
    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new IllegalStateException("Người dùng với ID " + id + " không tồn tại!");
        }

        User user = existingUser.get();
        user.setDisplayName(updatedUser.getDisplayName());
        user.setPhone(updatedUser.getPhone());
        user.setEmail(updatedUser.getEmail());
        user.setAddress(updatedUser.getAddress());
        return userRepository.save(user);
    }

    // 4. Xóa user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalStateException("Người dùng với ID " + id + " không tồn tại!");
        }
        userRepository.deleteById(id);
    }

    // 5. Tìm user theo ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Người dùng với ID " + id + " không tồn tại!"));
    }

    // 6. Tìm user theo tên (tìm kiếm không phân biệt hoa thường)
    public List<User> getUsersByName(String name) {
        return userRepository.findByDisplayNameContainingIgnoreCase(name);
    }
    public List<User> getTeachers() {
        return userRepository.findByAccountRoleId(1L);
    }

}
