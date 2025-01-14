package org.example.sellingcourese.Service;

import org.example.sellingcourese.Model.Role;
import org.example.sellingcourese.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // 1. Lấy tất cả roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // 2. Thêm mới role
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    // 3. Sửa role
    public Role updateRole(Long id, Role updatedRole) {
        Optional<Role> existingRole = roleRepository.findById(id);
        if (existingRole.isEmpty()) {
            throw new IllegalStateException("Role với ID " + id + " không tồn tại!");
        }

        Role role = existingRole.get();
        role.setName(updatedRole.getName());
        role.setDescription(updatedRole.getDescription());
        return roleRepository.save(role);
    }

    // 4. Xóa role
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalStateException("Role với ID " + id + " không tồn tại!");
        }
        roleRepository.deleteById(id);
    }

    // 5. Tìm kiếm role theo ID
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Role với ID " + id + " không tồn tại!"));
    }

    // 6. Tìm kiếm role theo tên
    public List<Role> getRolesByName(String name) {
        return roleRepository.findByNameContainingIgnoreCase(name);
    }
}
