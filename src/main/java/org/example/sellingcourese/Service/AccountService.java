package org.example.sellingcourese.Service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import io.jsonwebtoken.io.IOException;
import org.apache.coyote.Request;
import org.apache.hc.core5.http2.impl.nio.ClientHttpProtocolNegotiationStarter;
import org.example.sellingcourese.JWT.JwtService;
import org.example.sellingcourese.Model.Account;
import org.example.sellingcourese.Model.User;
import org.example.sellingcourese.Request.RegisterDTO;
import org.example.sellingcourese.repository.AccountRepository;
import org.example.sellingcourese.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.util.*;


@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder; // Đảm bảo bạn đã cấu hình PasswordEncoder trong ứng dụng


    // 1. Lấy tất cả tài khoản
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // 2. Thêm mới tài khoản và người dùng
    @Transactional
    public Account registerAccount(RegisterDTO registerDTO) {
        // Kiểm tra xem tên người dùng đã tồn tại trong hệ thống chưa
        Optional<Account> existingAccount = accountRepository.findByUserName(registerDTO.getUserName());
        if (existingAccount.isPresent()) {
            throw new IllegalStateException("Tài khoản với tên người dùng '" + registerDTO.getUserName() + "' đã tồn tại!");
        }

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassWord());

        // Tạo đối tượng Account từ thông tin DTO
        Account account = new Account();
        account.setUserName(registerDTO.getUserName());
        account.setPassWord(encodedPassword);
        account.setRoleId(registerDTO.getRoleId());

        // Lưu thông tin tài khoản vào cơ sở dữ liệu
        Account savedAccount = accountRepository.save(account);

        // Tạo đối tượng User từ thông tin DTO
        User user = new User();
        user.setDisplayName(registerDTO.getDisplayName());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setAddress(registerDTO.getAddress());
        user.setAccountID(savedAccount.getAccountID()); // Liên kết tài khoản với người dùng

        // Lưu thông tin người dùng vào cơ sở dữ liệu
        userRepository.save(user);

        // Trả về tài khoản đã lưu
        return savedAccount;
    }

    // 3. Cập nhật thông tin tài khoản
    public Account updateAccount(Long accountId, Account accountDetails) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setUserName(accountDetails.getUserName());
            account.setPassWord(accountDetails.getPassWord());
            account.setRoleId(accountDetails.getRoleId());
            return accountRepository.save(account);
        }
        return null;
    }

    // 4. Xóa tài khoản
    @Transactional
    public boolean deleteAccount(Long accountId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            accountRepository.deleteById(accountId);
            return true;
        }
        return false;
    }

    // 5. Tìm kiếm tài khoản theo username
    public String login(String username, String password) {
        // Lấy tài khoản, nếu không tìm thấy thì ném ngoại lệ
        Account account = accountRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra mật khẩu
        if (passwordEncoder.matches(password, account.getPassWord())) {
            // Tạo token nếu mật khẩu hợp lệ
            return jwtService.generateToken(
                    account.getUserName(),
                    String.valueOf(account.getAccountID()),

                    account.getRoleId(),
                    account.getRole().getName()// Truyền roleID vào
            );
        } else {
            throw new RuntimeException("Invalid password");
        }
    }




    // 7. Tìm kiếm tài khoản theo ID
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId).orElse(null);
    }

    // 8. Tìm kiếm tài khoản theo tên
    public Optional<Account> findByName(String name) {
        return accountRepository.findByUserName(name); // Repository đã trả về Optional
    }
    public List<Account> findByRoleId(Long roleId) {
        return accountRepository.findByRoleId(roleId);
    }

    @Transactional
    public void updateRole(Long accountId, Long roleId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
        account.setRoleId(roleId);
        accountRepository.save(account);
    }

    @Transactional
    public String resetPassword(String email) {
        // Tìm User dựa trên email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Long accountId = user.getAccountID();

            // Tìm Account tương ứng với User
            Optional<Account> accountOptional = accountRepository.findById(accountId);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                String newPassword = generateRandomPassword();

                // Cập nhật mật khẩu
                account.setPassWord(passwordEncoder.encode(newPassword));
                accountRepository.save(account);

                return newPassword;
            } else {
                throw new RuntimeException("Không tìm thấy tài khoản với ID: " + accountId);
            }
        } else {
            throw new RuntimeException("Không tìm thấy người dùng với email: " + email);
        }
    }


    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
    @Transactional
    public void processOAuthPostLogin(String email, String name) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            // Tạo tài khoản mới nếu người dùng chưa tồn tại
            Account account = new Account();
            account.setUserName(email);
            account.setPassWord(passwordEncoder.encode(generateRandomPassword()));
            account.setRoleId(2L); // Đặt role mặc định là Học sinh
            Account savedAccount = accountRepository.save(account);

            User user = new User();
            user.setDisplayName(name);
            user.setEmail(email);
            user.setAccountID(savedAccount.getAccountID());
            userRepository.save(user);
        }
    }
    @Transactional
    public String generateToken(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Account account = user.getAccount();
            return jwtService.generateToken(account.getUserName(), String.valueOf(account.getAccountID()), account.getRoleId(), account.getRole().getName());
        } else {
            throw new RuntimeException("Không tìm thấy tài khoản với email: " + email);
        }
    }



}
