package org.example.sellingcourese.repository;

import org.example.sellingcourese.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user theo email
    Optional<User> findByEmail(String email);
    User findByAccountID(Long accountID);
    // Tìm user theo tên (không phân biệt hoa thường)
    List<User> findByDisplayNameContainingIgnoreCase(String displayName);
   List<User> findByAccountRoleId(Long roleId);

}
