//
//package org.example.sellingcourese.Service;
//
//import org.example.sellingcourese.Model.Cart;
//import org.example.sellingcourese.Model.CartDetail;
//import org.example.sellingcourese.Model.Course;
//import org.example.sellingcourese.Request.CartDTO;
//import org.example.sellingcourese.Request.CourseDTO;
//import org.example.sellingcourese.repository.CartRepository;
//import org.example.sellingcourese.repository.CartDetailRepository;
//import org.example.sellingcourese.repository.CourseRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class CartService {
//
//    @Autowired
//    private CartRepository cartRepository;
//    @Autowired
//    private CourseRepository courseRepository;
//    @Autowired
//    private CartDetailRepository cartDetailRepository;
//
//    // Get all carts
//    public List<Cart> getAllCarts() {
//        return cartRepository.findAll();
//    }
//
//    // Find cart by ID
//    public Cart getCartById(Long id) {
//        return cartRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + id));
//    }
//
//    // Save cart with details
//    @Transactional
//    public Cart saveCartWithDetails(CartDTO cartDTO) {
//        // Tạo đối tượng Cart từ thông tin trong CartDTO
//        Cart cart = new Cart();
//        cart.setUserID(cartDTO.getUserID());
//        cart.setCreatedDate(new Date());
//
//        // Lưu thông tin Cart vào cơ sở dữ liệu trước để lấy CartID
//        Cart savedCart = cartRepository.save(cart);
//
//        // Tạo và lưu thông tin CartDetail dựa trên CartDTO
//        CartDetail cartDetail = new CartDetail();
//        cartDetail.setCartID(savedCart.getCartID());
//        cartDetail.setCourseID(cartDTO.getCourseID());
//        cartDetail.setQuantity(cartDTO.getQuantity());
//        cartDetail.setPrice(cartDTO.getPrice());
//
//        cartDetailRepository.save(cartDetail);
//
//        // Gắn thông tin chi tiết vào đối tượng Cart để trả về
//        savedCart.setCartDetails(Collections.singletonList(cartDetail));
//
//        return savedCart;
//    }
//
//
//    // Update cart with details
//    @Transactional
//    public Cart updateCartWithDetails(Long cartId, Cart updatedCart, List<CartDetail> updatedDetails) {
//        // Kiểm tra cart tồn tại
//        Cart existingCart = cartRepository.findById(cartId)
//                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));
//
//        // Cập nhật thông tin Cart
//        existingCart.setUserID(updatedCart.getUserID());
//        existingCart.setCreatedDate(new Date());
//        Cart savedCart = cartRepository.save(existingCart);
//
//        // Xóa các CartDetail cũ
//        cartDetailRepository.deleteAllByCartID(cartId);
//
//        // Thêm các CartDetail mới
//        for (CartDetail cartDetail : updatedDetails) {
//            cartDetail.setCartID(savedCart.getCartID());
//            cartDetailRepository.save(cartDetail);
//        }
//
//        // Gắn danh sách CartDetail mới vào Cart để trả về
//        savedCart.setCartDetails(updatedDetails);
//        return savedCart;
//    }
//
//
//    @Transactional
//    public void deleteCartNews(Long cartID) {
//        cartDetailRepository.deleteByCartID(cartID);
//        cartRepository.deleteById(cartID);
//    }
//    // Delete a cart
//    @Transactional
//    public void deleteCart(Long id) {
//        if (cartRepository.existsById(id)) {
//            cartDetailRepository.deleteAllByCartID(id);
//            cartRepository.deleteById(id);
//        } else {
//            throw new RuntimeException("Cart not found with ID: " + id);
//        }
//    }
//
//    // Remove a course from the cart
//    public void removeCourseFromCart(Long cartDetailId) {
//        if (cartDetailRepository.existsById(cartDetailId)) {
//            cartDetailRepository.deleteById(cartDetailId);
//        } else {
//            throw new RuntimeException("CartDetail not found with ID: " + cartDetailId);
//        }
//    }
//
//    public List<Cart> getCartsByUserID(Long userID) {
//        return cartRepository.findByUserID(userID);
//    }
//
//    public List<CartDetail> getCartDetailsByCartID(Long cartID) {
//        return cartDetailRepository.findByCartID(cartID);
//    }
//    @Transactional
//    public List<CartDTO> getCartDetailsWithCourseByCartID(Long cartID) {
//        List<CartDetail> cartDetails = cartDetailRepository.findByCartID(cartID);
//
//        return cartDetails.stream().map(cartDetail -> {
//            Course course = courseRepository.findById(cartDetail.getCourseID()).orElse(null);
//
//            // Tạo CourseDTO
//            CourseDTO courseDTO = course == null ? null : new CourseDTO(
//
//                    course.getTitle(),
//                    course.getDescription()
//            );
//
//            // Tạo CartDTO
//            return new CartDTO(
//                    cartDetail.getCartID(),
//                    cartDetail.getCart().getCreatedDate(),
//                    cartDetail.getCourseID(),
//                    cartDetail.getQuantity(),
//                    cartDetail.getPrice(),
//                    courseDTO
//            );
//        }).collect(Collectors.toList());
//    }
//
//
//
//}
package org.example.sellingcourese.Service;

import org.example.sellingcourese.Model.Cart;
import org.example.sellingcourese.Model.CartDetail;
import org.example.sellingcourese.Model.Course;
import org.example.sellingcourese.Model.OrderItem;
import org.example.sellingcourese.Request.CartDTO;
import org.example.sellingcourese.Request.CourseDTO;
import org.example.sellingcourese.repository.CartRepository;
import org.example.sellingcourese.repository.CartDetailRepository;
import org.example.sellingcourese.repository.CourseRepository;
import org.example.sellingcourese.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CartDetailRepository cartDetailRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // Get all carts
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    // Find cart by ID
    public Cart getCartById(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + id));
    }

    // Save cart with details
//    @Transactional
//    public Cart saveCartWithDetails(CartDTO cartDTO) {
//        // Tạo đối tượng Cart từ thông tin trong CartDTO
//        Cart cart = new Cart();
//        cart.setUserID(cartDTO.getUserID());
//        cart.setCreatedDate(new Date());
//
//        // Lưu thông tin Cart vào cơ sở dữ liệu trước để lấy CartID
//        Cart savedCart = cartRepository.save(cart);
//
//        // Tạo và lưu thông tin CartDetail dựa trên CartDTO
//        CartDetail cartDetail = new CartDetail();
//        cartDetail.setCartID(savedCart.getCartID());
//        cartDetail.setCourseID(cartDTO.getCourseID());
//        cartDetail.setQuantity(cartDTO.getQuantity());
//        cartDetail.setPrice(cartDTO.getPrice());
//
//        cartDetailRepository.save(cartDetail);
//
//        // Gắn thông tin chi tiết vào đối tượng Cart để trả về
//        savedCart.setCartDetails(Collections.singletonList(cartDetail));
//
//        return savedCart;
//    }

    public boolean isCourseInOrder(Long courseId, Long userId) {
        // Tìm tất cả order items của user này
        List<OrderItem> orderItems =orderDetailRepository.findByOrder_UserId(userId);
        // Kiểm tra courseID có trùng không
        return orderItems.stream().anyMatch(orderItem -> orderItem.getCourseId().equals(courseId));
    }

    @Transactional
    public Map<String, Object> saveCartWithDetails(CartDTO cartDTO) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra sản phẩm đã được đặt hàng và thanh toán chưa
        boolean isOrdered = isCourseInOrder(cartDTO.getCourseID(), cartDTO.getUserID());
        if (isOrdered) {
            response.put("status", "error");
            response.put("message", "Khóa học này đã được đặt hàng và thanh toán trước đó. Không thể thêm vào giỏ hàng.");
            return response;
        }

        // Tìm giỏ hàng hiện tại của người dùng
        Cart cart = cartRepository.findByUserID(cartDTO.getUserID())
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    // Nếu chưa có giỏ hàng, tạo mới
                    Cart newCart = new Cart();
                    newCart.setUserID(cartDTO.getUserID());
                    newCart.setCreatedDate(new Date());
                    return cartRepository.save(newCart);
                });

        // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
        cartDetailRepository.findByCartIDAndCourseID(cart.getCartID(), cartDTO.getCourseID())
                .ifPresentOrElse(
                        // Nếu đã tồn tại, tăng số lượng sản phẩm
                        existingCartDetail -> {
                            existingCartDetail.setQuantity(existingCartDetail.getQuantity() + cartDTO.getQuantity());
                            cartDetailRepository.save(existingCartDetail);
                        },
                        // Nếu chưa tồn tại, thêm mới sản phẩm vào giỏ hàng
                        () -> {
                            CartDetail newCartDetail = new CartDetail();
                            newCartDetail.setCartID(cart.getCartID());
                            newCartDetail.setCourseID(cartDTO.getCourseID());
                            newCartDetail.setQuantity(cartDTO.getQuantity());
                            newCartDetail.setPrice(cartDTO.getPrice());
                            cartDetailRepository.save(newCartDetail);
                        }
                );

        // Lấy danh sách CartDetails và gắn vào Cart
        List<CartDetail> cartDetails = cartDetailRepository.findAllByCartID(cart.getCartID());
        cart.setCartDetails(cartDetails);

        // Trả về phản hồi thành công
        response.put("status", "success");
        response.put("message", "Khóa học đã được thêm vào giỏ hàng thành công.");
        response.put("cart", cart);
        return response;
    }

//    @Transactional
//    public Cart saveCartWithDetails(CartDTO cartDTO) {
//        // Lấy danh sách giỏ hàng của người dùng
//        List<Cart> carts = cartRepository.findByUserID(cartDTO.getUserID());
//        Cart cart;
//
//        if (!carts.isEmpty()) {
//            // Nếu danh sách không rỗng, lấy giỏ hàng đầu tiên
//            cart = carts.get(0);
//        } else {
//            // Nếu danh sách rỗng, tạo mới giỏ hàng
//            cart = new Cart();
//            cart.setUserID(cartDTO.getUserID());
//            cart.setCreatedDate(new Date());
//            cart = cartRepository.save(cart);
//        }
//
//        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
//        Optional<CartDetail> existingCartDetail = cartDetailRepository.findByCartIDAndCourseID(cart.getCartID(), cartDTO.getCourseID());
//
//        if (existingCartDetail.isPresent()) {
//            // Nếu đã tồn tại, tăng số lượng sản phẩm
//            CartDetail cartDetail = existingCartDetail.get();
//            cartDetail.setQuantity(cartDetail.getQuantity() + cartDTO.getQuantity());
//            cartDetailRepository.save(cartDetail);
//        } else {
//            // Nếu chưa tồn tại, thêm mới sản phẩm vào giỏ hàng
//            CartDetail newCartDetail = new CartDetail();
//            newCartDetail.setCartID(cart.getCartID());
//            newCartDetail.setCourseID(cartDTO.getCourseID());
//            newCartDetail.setQuantity(cartDTO.getQuantity());
//            newCartDetail.setPrice(cartDTO.getPrice());
//            cartDetailRepository.save(newCartDetail);
//        }
//
//        // Gắn danh sách CartDetails vào đối tượng Cart để trả về
//        List<CartDetail> cartDetails = cartDetailRepository.findAllByCartID(cart.getCartID());
//        cart.setCartDetails(cartDetails);
//
//        return cart;
//    }

    // Update cart with details
    @Transactional
    public Cart updateCartWithDetails(Long cartId, Cart updatedCart, List<CartDetail> updatedDetails) {
        // Kiểm tra cart tồn tại
        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));

        // Cập nhật thông tin Cart
        existingCart.setUserID(updatedCart.getUserID());
        existingCart.setCreatedDate(new Date());
        Cart savedCart = cartRepository.save(existingCart);

        // Xóa các CartDetail cũ
        cartDetailRepository.deleteAllByCartID(cartId);

        // Thêm các CartDetail mới
        for (CartDetail cartDetail : updatedDetails) {
            cartDetail.setCartID(savedCart.getCartID());
            cartDetailRepository.save(cartDetail);
        }

        // Gắn danh sách CartDetail mới vào Cart để trả về
        savedCart.setCartDetails(updatedDetails);
        return savedCart;
    }

    @Transactional
    public void deleteCartNews(Long cartID) {
        cartDetailRepository.deleteByCartID(cartID);
        cartRepository.deleteById(cartID);
    }

    // Delete a cart
    @Transactional
    public void deleteCart(Long id) {
        if (cartRepository.existsById(id)) {
            cartDetailRepository.deleteAllByCartID(id);
            cartRepository.deleteById(id);
        } else {
            throw new RuntimeException("Cart not found with ID: " + id);
        }
    }

    // Remove a course from the cart
    public void removeCourseFromCart(Long cartDetailId) {
        if (cartDetailRepository.existsById(cartDetailId)) {
            cartDetailRepository.deleteById(cartDetailId);
        } else {
            throw new RuntimeException("CartDetail not found with ID: " + cartDetailId);
        }
    }

    public List<Cart> getCartsByUserID(Long userID) {
        return cartRepository.findByUserID(userID);
    }

    public List<CartDetail> getCartDetailsByCartID(Long cartID) {
        return cartDetailRepository.findByCartID(cartID);
    }

    @Transactional
    public List<CartDTO> getCartDetailsWithCourseByCartID(Long cartID) {
        List<CartDetail> cartDetails = cartDetailRepository.findByCartID(cartID);

        return cartDetails.stream().map(cartDetail -> {
            Course course = courseRepository.findById(cartDetail.getCourseID()).orElse(null);

            // Tạo CartDTO
            return new CartDTO(
                    cartDetail.getCartDetailID(),
                    cartDetail.getCartID(),
                    cartDetail.getCart().getUserID(),
                    cartDetail.getCart().getCreatedDate(),
                    cartDetail.getCourseID(),
                    cartDetail.getQuantity(),
                    cartDetail.getPrice(),
                    course != null ? course.getTitle() : null,
                    course != null ? course.getDescription() : null
            );
        }).collect(Collectors.toList());

    }
    @Transactional
    public void clearCartByUserID(Long userID) {
        List<Cart> carts = cartRepository.findByUserID(userID);
        for (Cart cart : carts) {
            cartDetailRepository.deleteByCartID(cart.getCartID());
        }
        cartRepository.deleteByUserID(userID);
    }
}