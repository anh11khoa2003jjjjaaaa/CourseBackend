package org.example.sellingcourese.Controller;

import org.example.sellingcourese.Model.Account;
import org.example.sellingcourese.Model.User;
import org.example.sellingcourese.Request.RegisterDTO;
import org.example.sellingcourese.Request.RegisterRequest;
import org.example.sellingcourese.Service.AccountService;
import org.example.sellingcourese.repository.RoleUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/public/accounts")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // 1. Lấy tất cả tài khoản
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // 2. Đăng ký tài khoản mới (lưu cả Account và User)
    @PostMapping("/register")
    public ResponseEntity<Account> registerAccount(@RequestBody RegisterDTO registerDTO) {
        Account registeredAccount = accountService.registerAccount(registerDTO);
        return new ResponseEntity<>(registeredAccount, HttpStatus.CREATED);
    }

    // 3. Cập nhật tài khoản
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable("id") Long accountId, @RequestBody Account accountDetails) {
        Account updatedAccount = accountService.updateAccount(accountId, accountDetails);
        if (updatedAccount != null) {
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 4. Xóa tài khoản
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long accountId) {
        boolean isDeleted = accountService.deleteAccount(accountId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 5. Tìm kiếm tài khoản theo username
    @GetMapping("/search")
    public ResponseEntity<Account> findAccountByUserName(@RequestParam String userName) {
        return accountService.findByName(userName)
                .map(account -> new ResponseEntity<>(account, HttpStatus.OK)) // Nếu có giá trị, trả về account
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Nếu không, trả về NOT_FOUND
    }


    // 6. Đăng nhập tài khoản (kiểm tra username và password)
    // Endpoint xử lý đăng nhập
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam String username, @RequestParam String password) {
        try {
            // Gọi service để thực hiện đăng nhập
            String token = accountService.login(username, password);

            // Trả về token
            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
    // 7. Tìm kiếm tài khoản theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Account> findById(@PathVariable("id") Long accountId) {
        Account account = accountService.findById(accountId);
        if (account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @GetMapping("/teacher/{roleId}")
    public ResponseEntity<List<Account>> findByRoleId(@PathVariable("roleId") Long roleId) {
        return new ResponseEntity<>(accountService.findByRoleId(roleId), HttpStatus.OK);
    }

    @PutMapping("/{accountId}/role")
    public ResponseEntity<Void> updateRole(@PathVariable Long accountId, @RequestBody RoleUpdateRequest request) {
        accountService.updateRole(accountId, request.getRoleId());
        return ResponseEntity.ok().build();
    }


}
