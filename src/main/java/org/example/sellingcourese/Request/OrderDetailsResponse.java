package org.example.sellingcourese.Request;

import org.example.sellingcourese.Model.Order;
import org.example.sellingcourese.Model.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public class OrderDetailsResponse {

    private List<OrderItem> orderDetails;
    private Order order;
    private BigDecimal totalAmount;

    public OrderDetailsResponse(List<OrderItem> orderDetails, Order order, BigDecimal totalAmount) {
        this.orderDetails = orderDetails;
        this.order = order;
        this.totalAmount = totalAmount;
    }

    public List<OrderItem> getOrderDetails() {
        return orderDetails;
    }

    public Order getOrder() {
        return order;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}