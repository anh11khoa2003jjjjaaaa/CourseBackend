package org.example.sellingcourese.Service;

import jakarta.persistence.EntityNotFoundException;
import org.example.sellingcourese.Model.*;
import org.example.sellingcourese.Request.OrderDTO;
import org.example.sellingcourese.Request.OrderDetailsResponse;
import org.example.sellingcourese.Request.OrderItemDTO;

import org.example.sellingcourese.repository.*;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderItemRepository;
    @Autowired
    private  OrderDetailRepository orderDetailRepository;


    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public List<Order> getCompletedOrders() {
        Long completedStatusId = 3L; // Trạng thái "Complete" có ID là 3
        return orderRepository.findByStatusId(completedStatusId);
    }
    public List<OrderDetailsResponse> getOrdersByUserIdAndStatus(Long userId, Long statusId) {
        // Lấy tất cả các đơn hàng của userId với trạng thái là 3
        List<Order> orders = orderRepository.findByUserIdAndStatusId(userId, statusId);

        return orders.stream().map(order -> {
            // Lấy tất cả các OrderItem cho đơn hàng
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

            // Tính tổng số tiền của đơn hàng
            BigDecimal totalAmount = orderItems.stream()
                    .map(orderItem -> orderItem.getCourse().getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Trả về OrderDetailsResponse cho từng đơn hàng
            return new OrderDetailsResponse(orderItems, order, totalAmount);
        }).collect(Collectors.toList());
    }
    // Add a new order
    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setStatusId(2L);

        Order savedOrder = orderRepository.save(order);

        Set<OrderItem> orderItems = orderDTO.getOrderItems().stream().map(itemDTO -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setCourseId(itemDTO.getCourseId());
            orderItem.setQuantity(itemDTO.getQuantity());
            return orderItem;
        }).collect(Collectors.toSet());

        orderItemRepository.saveAll(orderItems);

        savedOrder.setOrderItems(orderItems);
        return savedOrder;
    }
    // Update an order
    public Order updateOrder(Long id, Order orderDetails) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setUserId(orderDetails.getUserId());
            order.setTotalPrice(orderDetails.getTotalPrice());
            order.setStatusId(orderDetails.getStatusId());
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with ID: " + id);
        }
    }

//    // Delete an order
//    public void deleteOrder(Long id) {
//        if (orderRepository.existsById(id)) {
//            orderRepository.deleteById(id);
//        } else {
//            throw new RuntimeException("Order not found with ID: " + id);
//        }
//    }
// Delete an order and its items if the status is 2
@Transactional
public void deleteOrder(Long orderId) {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
    if (!orderOptional.isPresent()) {
        throw new RuntimeException("Order not found with ID " + orderId);
    }

    // In ra thông tin order trước khi xóa
    System.out.println("Deleting order with ID: " + orderId);

    try {
        // Lấy Order và xóa, các OrderItem sẽ được xóa tự động do cascade
        orderRepository.deleteById(orderId);
    } catch (Exception e) {
        // Log chi tiết lỗi
        System.out.println("Error while deleting order: " + e.getMessage());
        throw new RuntimeException("Error deleting order: " + e.getMessage());
    }
}



    // Find order by ID

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id));
    }


    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderDetails(Long id) {
        // Tìm đơn hàng theo OrderID
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));

        // Lấy chi tiết đơn hàng
        List<OrderItem> orderDetails = orderDetailRepository.findByOrderId(id);

        // Tính tổng tiền
        BigDecimal totalAmount = order.getTotalPrice();

        return new OrderDetailsResponse(orderDetails, order, totalAmount);
    }
    // Save order items for an order
    public void saveOrderItems(Long orderId, List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            item.setOrderId(orderId);
            orderItemRepository.save(item);
        }
    }
    public List<OrderDetailsResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            BigDecimal totalAmount = BigDecimal.ZERO; // Sử dụng giá trị mặc định hoặc logic khác
            return new OrderDetailsResponse(orderItems, order, totalAmount);
        }).collect(Collectors.toList());
    }

}