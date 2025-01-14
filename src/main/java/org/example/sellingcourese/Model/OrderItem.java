package org.example.sellingcourese.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cột order_id để lưu ID đơn hàng
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    // Quan hệ với Order, chỉ ánh xạ, không quản lý khóa ngoại
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;

    // Cột course_id để lưu ID khóa học
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    // Quan hệ với Course, chỉ ánh xạ, không quản lý khóa ngoại
//    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    @Column(nullable = false)
    private int quantity;


}
