package org.example.sellingcourese.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.sellingcourese.Model.Account;
import org.example.sellingcourese.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
@Autowired
private AccountRepository accountRepository;
    private final String SECRET_KEY = "ZyeWMAZy29Ok7GeIzG90JwX5G6PP0jifXd/O/as/W3GtZSvCeDp8RCuPmXfOxmpM";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    // Extract the username (subject) from the JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract the userID from the JWT token
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userID", String.class));
    }

    // Extract expiration date
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract any claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT has expired", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generate a token with both username and userID
    public String generateToken(String username, String AccountID, Long roleID,String RoleName) {


        Map<String, Object> claims = new HashMap<>();
        claims.put("AccountID", AccountID); // Thêm userID vào claims
        claims.put("roleID", roleID);
        claims.put("RoleName", RoleName); // Thêm roleID vào claims
        return createToken(claims, username); // Gọi phương thức createToken
    }

    // Create a token with claims and subject
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)  // Set the username as the subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Validate the token by checking username and expiration
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
