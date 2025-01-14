package org.example.sellingcourese.Service;

import org.example.sellingcourese.Model.Account;
import org.example.sellingcourese.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // Tìm người dùng từ cơ sở dữ liệu dựa trên username
        Optional<Account> accountOptional = accountRepository.findByUserName(userName);

        // Kiểm tra nếu không tìm thấy tài khoản
        if (accountOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + userName);
        }

        // Lấy tài khoản từ Optional
        Account account = accountOptional.get();

        // Tạo danh sách các quyền (authorities) dựa trên roleId
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (account.getRoleId() == 1) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else if (account.getRoleId() == 2) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
        } else if (account.getRoleId() == 3) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            throw new IllegalArgumentException("Invalid role ID: " + account.getRoleId());
        }

        // Trả về đối tượng UserDetails
        return new org.springframework.security.core.userdetails.User(
                account.getUserName(),
                account.getPassWord(),
                authorities
        );
    }
}
