package org.example.sellingcourese.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    public Long userId;

    public Long courseId;
    public BigDecimal totalPrice;
    public Long statusId;
    public int quantity;
    private List<OrderItemDTO> orderItems;
}
