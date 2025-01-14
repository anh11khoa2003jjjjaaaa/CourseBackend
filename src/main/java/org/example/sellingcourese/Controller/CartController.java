//package org.example.sellingcourese.Controller;
//
//import org.example.sellingcourese.Model.Cart;
//import org.example.sellingcourese.Model.CartDetail;
//import org.example.sellingcourese.Model.Course;
//import org.example.sellingcourese.Request.CartDTO;
//import org.example.sellingcourese.Request.CartWithDetailsDTO;
//import org.example.sellingcourese.Service.CartService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/public/carts")
//public class CartController {
//
//    @Autowired
//    private CartService cartService;
//
//    // Lấy danh sách tất cả các Cart
//    @GetMapping
//    public ResponseEntity<List<Cart>> getAllCarts() {
//        List<Cart> carts = cartService.getAllCarts();
//        return ResponseEntity.ok(carts);
//    }
//
//    // Lấy thông tin Cart theo ID
//    @GetMapping("/{id}")
//    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
//        Cart cart = cartService.getCartById(id);
//        return ResponseEntity.ok(cart);
//    }
//    @PostMapping
//    public ResponseEntity<Cart> saveCartWithDetails(@RequestBody CartDTO cartDTO) {
//        // Gọi Service để lưu thông tin Cart và CartDetails
//        Cart savedCart = cartService.saveCartWithDetails(cartDTO);
//        return ResponseEntity.ok(savedCart);
//    }
//
//
//    // Cập nhật thông tin Cart kèm các CartDetail
//    @PutMapping("/{id}")
//    public ResponseEntity<Cart> updateCartWithDetails(
//            @PathVariable Long id,
//            @RequestBody Cart updatedCart,
//            @RequestBody List<CartDetail> updatedDetails
//    ) {
//        Cart updatedCartResponse = cartService.updateCartWithDetails(id, updatedCart, updatedDetails);
//        return ResponseEntity.ok(updatedCartResponse);
//    }
//
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteCartnew(@PathVariable Long id) {
//        cartService.deleteCartNews(id);
//        return ResponseEntity.ok("Cart with ID " + id + " has been deleted.");
//    }
////    // Xóa Cart theo ID
////    @DeleteMapping("/{id}")
////    public ResponseEntity<String> deleteCart(@PathVariable Long id) {
////        cartService.deleteCart(id);
////        return ResponseEntity.ok("Cart with ID " + id + " has been deleted.");
////    }
//
//    // Xóa một CartDetail theo ID
//    @DeleteMapping("/details/{cartDetailId}")
//    public ResponseEntity<String> removeCourseFromCart(@PathVariable Long cartDetailId) {
//
//        cartService.removeCourseFromCart(cartDetailId);
//        return ResponseEntity.ok("CartDetail with ID " + cartDetailId + " has been deleted.");
//    }
//
//    @GetMapping("/user/{userID}")
//    public ResponseEntity<List<Cart>> getCartsByUserID(@PathVariable Long userID) {
//        List<Cart> carts = cartService.getCartsByUserID(userID);
//        return ResponseEntity.ok(carts);
//    }
//
//    @GetMapping("/details/{cartID}")
//
//    public ResponseEntity<List<CartDTO>> getCartDetailsWithCourseByCartID(@PathVariable Long cartID) {
//        List<CartDTO> cartDetails = cartService.getCartDetailsWithCourseByCartID(cartID);
//        return ResponseEntity.ok(cartDetails);
//    }
//
//
//
//}
package org.example.sellingcourese.Controller;

import org.example.sellingcourese.Model.Cart;
import org.example.sellingcourese.Model.CartDetail;
import org.example.sellingcourese.Request.CartDTO;
import org.example.sellingcourese.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/public/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    // Lấy danh sách tất cả các Cart
    @GetMapping
    public ResponseEntity<List<Cart>> getAllCarts() {
        List<Cart> carts = cartService.getAllCarts();
        return ResponseEntity.ok(carts);
    }

    // Lấy thông tin Cart theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        Cart cart = cartService.getCartById(id);
        return ResponseEntity.ok(cart);
    }

//    @PostMapping
//    public ResponseEntity<Cart> saveCartWithDetails(@RequestBody CartDTO cartDTO) {
//        // Gọi Service để lưu thông tin Cart và CartDetails
//        Cart savedCart = cartService.saveCartWithDetails(cartDTO);
//
//        return ResponseEntity.ok(savedCart);
//    }
@PostMapping
public ResponseEntity<Map<String, Object>> saveCartWithDetails(@RequestBody CartDTO cartDTO) {
    // Gọi Service để lưu thông tin Cart và CartDetails
    Map<String, Object> response = cartService.saveCartWithDetails(cartDTO);

    // Kiểm tra trạng thái trong phản hồi
    if ("error".equals(response.get("status"))) {
        return ResponseEntity.badRequest().body(response); // Trả về HTTP 400 nếu có lỗi
    }

    return ResponseEntity.ok(response); // Trả về HTTP 200 nếu thành công
}

    // Cập nhật thông tin Cart kèm các CartDetail
    @PutMapping("/{id}")
    public ResponseEntity<Cart> updateCartWithDetails(
            @PathVariable Long id,
            @RequestBody Cart updatedCart,
            @RequestBody List<CartDetail> updatedDetails
    ) {
        Cart updatedCartResponse = cartService.updateCartWithDetails(id, updatedCart, updatedDetails);
        return ResponseEntity.ok(updatedCartResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartnew(@PathVariable Long id) {
        cartService.deleteCartNews(id);
        return ResponseEntity.ok("Cart with ID " + id + " has been deleted.");
    }

    // Xóa một CartDetail theo ID
    @DeleteMapping("/details/{cartDetailId}")
    public ResponseEntity<String> removeCourseFromCart(@PathVariable Long cartDetailId) {
        cartService.removeCourseFromCart(cartDetailId);
        return ResponseEntity.ok("CartDetail with ID " + cartDetailId + " has been deleted.");
    }

    @GetMapping("/user/{userID}")
    public ResponseEntity<List<Cart>> getCartsByUserID(@PathVariable Long userID) {
        List<Cart> carts = cartService.getCartsByUserID(userID);
        return ResponseEntity.ok(carts);
    }
    @DeleteMapping("/user/{userID}")
    public ResponseEntity<Void> clearCartByUserID(@PathVariable Long userID) {
        cartService.clearCartByUserID(userID);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/details/{cartID}")
    public ResponseEntity<List<CartDTO>> getCartDetailsWithCourseByCartID(@PathVariable Long cartID) {
        List<CartDTO> cartDetails = cartService.getCartDetailsWithCourseByCartID(cartID);
        return ResponseEntity.ok(cartDetails);
    }
}