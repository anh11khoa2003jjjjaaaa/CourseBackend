//package org.example.sellingcourese.Controller;
//
//import org.example.sellingcourese.Service.VNPayService;
//import org.springframework.stereotype.Controller;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//
//@RestController
//@CrossOrigin(origins = "http://localhost:3000")
//public class CheckoutVNpayController {
//    @Autowired
//    private VNPayService vnPayService;
//
//    @PostMapping("/submitOrder")
//    public String submitOrder(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
//        int orderTotal = (int) payload.get("amount");
//        String orderInfo = (String) payload.get("orderInfo");
//        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
//        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
//        return vnpayUrl; // Trả về URL thanh toán trực tiếp
//    }
//
//    @GetMapping("/vnpay-payment")
//    public String getMapping(HttpServletRequest request, Model model) {
//        int paymentStatus = vnPayService.orderReturn(request);
//
//        String orderInfo = request.getParameter("vnp_OrderInfo");
//        String paymentTime = request.getParameter("vnp_PayDate");
//        String transactionId = request.getParameter("vnp_TransactionNo");
//        String totalPrice = request.getParameter("vnp_Amount");
//
//        model.addAttribute("orderId", orderInfo);
//        model.addAttribute("totalPrice", totalPrice);
//        model.addAttribute("paymentTime", paymentTime);
//        model.addAttribute("transactionId", transactionId);
//        model.addAttribute("paymentStatus", paymentStatus == 1 ? "00" : "01");
//
//        if (paymentStatus == 1) {
//            return "redirect:http://localhost:3000/home"; // Chuyển hướng về trang chủ sau khi thanh toán thành công
//        } else {
//            return "orderfail";
//        }
//    }
//}

package org.example.sellingcourese.Controller;

import org.example.sellingcourese.Model.Order;
import org.example.sellingcourese.Service.OrderService;
import org.example.sellingcourese.Service.VNPayService;
import org.example.sellingcourese.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CheckoutVNpayController {
    @Autowired
    private VNPayService vnPayService;
 @Autowired
 private OrderRepository orderRepository;
    @PostMapping("/submitOrder")
    public String submitOrder(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        int orderTotal = (int) payload.get("amount");
        String orderInfo = (String) payload.get("orderInfo");
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
        return vnpayUrl; // Trả về URL thanh toán trực tiếp
    }

//    @GetMapping("/vnpay-payment")
//    public void getMapping(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        int paymentStatus = vnPayService.orderReturn(request);
//
//        if (paymentStatus == 1) {
//            response.sendRedirect("http://localhost:3000/home");
//            Order order=new Order();
//            order.setStatusId(3L);// Chuyển hướng về trang chủ sau khi thanh toán thành công
//        } else {
//            response.sendRedirect("http://localhost:3000/orderfail"); // Chuyển hướng về trang thất bại nếu thanh toán không thành công
//        }
//    }
@GetMapping("/vnpay-payment")
public void getMapping(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int paymentStatus = vnPayService.orderReturn(request);

    // Kiểm tra trạng thái thanh toán
    if (paymentStatus == 1) {
        // Lấy Order ID từ tham số vnp_OrderInfo
        String vnpOrderInfo = request.getParameter("vnp_OrderInfo");
        if (vnpOrderInfo != null) {
            try {
                // Giải mã và lấy Order ID
                String decodedOrderInfo = URLDecoder.decode(vnpOrderInfo, StandardCharsets.UTF_8.name());
                String[] parts = decodedOrderInfo.split(":");
                if (parts.length > 1) {
                    Long orderId = Long.parseLong(parts[1].trim()); // Lấy giá trị ID
                    // Cập nhật trạng thái đơn hàng
                    Optional<Order> orderOptional = orderRepository.findById(orderId);
                    if (orderOptional.isPresent()) {
                        Order order = orderOptional.get();
                        order.setStatusId(3L);  // Cập nhật trạng thái là "Complete" (3)
                        orderRepository.save(order);  // Lưu lại cập nhật
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("http://localhost:3000/orderfail");
                return;
            }
        }
        // Chuyển hướng về trang chủ sau khi thanh toán thành công
        response.sendRedirect("http://localhost:3000/home");
    } else {
        // Nếu thanh toán không thành công, chuyển hướng về trang thất bại
        response.sendRedirect("http://localhost:3000/orderfail");
    }
}


}