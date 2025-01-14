package org.example.sellingcourese.Controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.sellingcourese.Model.Order;
import org.example.sellingcourese.Model.OrderItem;
import org.example.sellingcourese.Request.OrderDTO;
import org.example.sellingcourese.Request.OrderDetailsResponse;
import org.example.sellingcourese.Service.OrderService;
import org.example.sellingcourese.Service.OrderItemService;
import org.example.sellingcourese.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/public/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderRepository orderRepository;
    // Get all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
    // Endpoint để lấy danh sách đơn hàng hoàn tất
    @GetMapping("/completed")
    public ResponseEntity<List<Order>> getCompletedOrders() {
        List<Order> completedOrders = orderService.getCompletedOrders();
        return ResponseEntity.ok(completedOrders);
    }
    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Add a new order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        Order savedOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(savedOrder);
    }

    // Update an order
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDetails));
    }

    // Delete an order
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
//        orderService.deleteOrder(id);
//        return ResponseEntity.noContent().build();
//    }

    // Save order items for an order
    @PostMapping("/{orderId}/items")
    public ResponseEntity<Void> saveOrderItems(@PathVariable Long orderId, @RequestBody List<OrderItem> orderItems) {
        orderService.saveOrderItems(orderId, orderItems);
        return ResponseEntity.ok().build();
    }

    // Get all order items
    @GetMapping("/items")
    public List<OrderItem> getAllOrderItems() {
        return orderItemService.getAllOrderItems();
    }

    // Get order item by ID
    @GetMapping("/items/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getOrderItemById(id));
    }

    // Add a new order item
    @PostMapping("/items")
    public OrderItem addOrderItem(@RequestBody OrderItem orderItem) {
        return orderItemService.addOrderItem(orderItem);
    }

    // Update an order item
    @PutMapping("/items/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItem orderItemDetails) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(id, orderItemDetails));
    }

    // Delete an order item
    // Endpoint để xóa đơn hàng
    @DeleteMapping("/delete/{orderId}")
    @Transactional
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok().body("Order deleted successfully");
        } catch (RuntimeException e) {
            // Log lỗi chi tiết ở đây
            return ResponseEntity.badRequest().body("Error deleting order: " + e.getMessage());
        }
    }


//    @DeleteMapping("/items/{id}")
//    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
//        orderItemService.deleteOrderItem(id);
//        return ResponseEntity.noContent().build();
//    }

    @GetMapping("/get/{orderId}")
    public ResponseEntity<OrderDetailsResponse> getOrderDetails(@PathVariable Long orderId) {
        try {
            // Gọi đến service để lấy chi tiết đơn hàng
            OrderDetailsResponse orderDetails = orderService.getOrderDetails(orderId);

            // Trả về chi tiết đơn hàng
            return ResponseEntity.ok(orderDetails);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDetailsResponse>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderDetailsResponse> orderDetailsResponses = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orderDetailsResponses);
    }
    @GetMapping("/status/3/{userId}")
    public ResponseEntity<List<OrderDetailsResponse>> getOrdersByUserIdAndStatus(@PathVariable Long userId) {
        List<OrderDetailsResponse> orderDetailsResponses = orderService.getOrdersByUserIdAndStatus(userId, 3L);
        return ResponseEntity.ok(orderDetailsResponses);
    }
}