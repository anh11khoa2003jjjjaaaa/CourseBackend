package org.example.sellingcourese.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long cartDetailID;
    private Long cartID;
    private Long userID;
    private Date createdDate;
    private Long courseID;
    private int quantity;
    private BigDecimal price;
    private String courseTitle;
    private String courseDescription;
}