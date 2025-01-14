//package org.example.sellingcourese.Request;
//
//import lombok.RequiredArgsConstructor;
//import org.example.sellingcourese.Request.OrderItemDTO;
//import org.example.sellingcourese.Model.Course;
//import org.example.sellingcourese.Model.OrderItem;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class OrderItemMapper {
//    public OrderItem toOrderItem(OrderItemDTO dto, Long orderId) {
//        OrderItem item = new OrderItem();
//        item.setOrderId(orderId);
//        item.setCourseId(dto.getCourseId());
//        item.setQuantity(dto.getQuantity());
//        return item;
//    }
//
//    public OrderItemDTO toOrderItemDTO(OrderItem item) {
//        return new OrderItemDTO(
//                item.getId(),
//                item.getOrderId(),
//                item.getCourseId(),
//                Optional.ofNullable(item.getCourse())
//                        .map(Course::getTitle)
//                        .orElse("Unknown Course"),
//                item.getQuantity()
//        );
//    }
//}