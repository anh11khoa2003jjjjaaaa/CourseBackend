//package org.example.sellingcourese.Service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.example.sellingcourese.Service.AccountService;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final AccountService accountService;
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
//
//        try {
//            // Process the OAuth2 login and get JWT token
//            accountService.processOAuthPostLogin(oauthUser.getEmail(), oauthUser.getName());
//            String token = accountService.generateToken(oauthUser.getEmail());
//
//            // Redirect to frontend with token
//            String redirectUrl = "http://localhost:3000/oauth2/callback?token=" + token;
//            response.sendRedirect(redirectUrl);
//        } catch (Exception e) {
//            response.sendRedirect("http://localhost:3000/login?error=true");
//        }
//    }
//}