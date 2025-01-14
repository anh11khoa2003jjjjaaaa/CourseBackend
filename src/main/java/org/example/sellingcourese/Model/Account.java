package org.example.sellingcourese.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID", updatable = false, nullable = false)
    private Long accountID;

    @Column(name = "Username", nullable = false, length = 255, unique = true)
    private String userName;

    @Column(name = "Password", nullable = false, length = 255)
    private String passWord;

    // Cột RoleId lưu giá trị ID vai trò, không sử dụng khóa ngoại
    @Column(name = "RoleId", nullable = false)
    private Long roleId;

    // Quan hệ Many-to-One với Role (chỉ để truy xuất dữ liệu, không ép buộc khóa ngoại)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoleId", insertable = false, updatable = false) // Chỉ ánh xạ, không quản lý giá trị RoleId
    private Role role;
}
