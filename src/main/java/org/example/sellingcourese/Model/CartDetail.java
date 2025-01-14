package org.example.sellingcourese.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CartDetails")
public class CartDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CartDetailID")
    private Long cartDetailID;

    @Column(name = "CartID", nullable = false)
    private Long cartID;

    @Column(name = "CourseID", nullable = false)
    private Long courseID;

    @Column(name = "Quantity", nullable = false)
    private int quantity;

    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CartID", insertable = false, updatable = false)
    private Cart cart;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseID", insertable = false, updatable = false)
    private Course course;

//    @Column(name = "Size", nullable = false, length = 50)
//    private String size;
//
//    @Column(name = "Image", length = 255,nullable = true)
//    private String image;
}
