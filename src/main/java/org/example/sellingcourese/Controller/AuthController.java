//package org.example.sellingcourese.Controller;
//
//
//import lombok.RequiredArgsConstructor;
//import org.example.sellingcourese.Service.EmailService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final EmailService emailService;
//
//    @GetMapping("/oauth2/success")
//    public ResponseEntity<?> oauth2LoginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
//        String email = oauth2User.getAttribute("email");
//        String name = oauth2User.getAttribute("name");
//
//        try {
//            emailService.sendWelcomeEmail(email, name);
//            return ResponseEntity.ok().body("Login successful");
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Error sending welcome email");
//        }
//    }
//
//    @GetMapping("/oauth2/failure")
//    public ResponseEntity<?> oauth2LoginFailure() {
//        return ResponseEntity.badRequest().body("Login failed");
//    }
//}
package org.example.sellingcourese.Controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.sellingcourese.Service.AccountService;
import org.example.sellingcourese.Service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/public/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmailService emailService;
    private final AccountService accountService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            // Reset mật khẩu và lấy mật khẩu mới
            String newPassword = accountService.resetPassword(email);

            // Nội dung email HTML
            String subject = "Reset Your Password";
            String content = String.format("""
                <html>
                    <body>
                        <h2>Reset Password Request</h2>
                        <p>Hello,</p>
                        <p>Your password has been reset successfully. Please find your new password below:</p>
                        <p><strong>%s</strong></p>
                        <p>We recommend that you log in and change your password immediately for security purposes.</p>
                        <br>
                        <p>Best regards,</p>
                        <p>Course Platform Team</p>
                    </body>
                </html>
                """, newPassword);

            // Gửi email
            emailService.sendEmail(email, subject, content);

            return ResponseEntity.ok().body("Mật khẩu mới đã được gửi đến email của bạn.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi gửi email đặt lại mật khẩu.");
        }
    }
    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2LoginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Authentication failed."));
        }

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        try {
            accountService.processOAuthPostLogin(email, name);
            String token = accountService.generateToken(email);
            emailService.sendWelcomeEmail(email, name);

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "token", token,
                    "email", email,
                    "name", name
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error processing login."));
        }
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<?> oauth2LoginFailure() {
        return ResponseEntity.badRequest().body("Login failed");
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> currentUser(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        if (oAuth2AuthenticationToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        OAuth2User principal = oAuth2AuthenticationToken.getPrincipal();
        return ResponseEntity.ok(principal.getAttributes());
    }
//    @GetMapping("/token")
//    public String getAccessToken(@RequestParam String code) {
//        // Gọi phương thức getToken để lấy access token từ Google OAuth2
//
//            String accessToken = accountService.getToken(code);
//            if (accessToken != null) {
//                return accessToken;  // Trả về access token dưới dạng String
//            } else {
//                return "Error: Unable to retrieve access token";
//            }
//
//    }


    @GetMapping("/login")
    public Map<String, Object> home(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();
        if (principal != null) {
            response.put("name", principal.getAttribute("name"));
            response.put("email", principal.getAttribute("email"));
        } else {
            response.put("message", "User not authenticated");
        }
        return response;
    }
}