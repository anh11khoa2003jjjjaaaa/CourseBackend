package org.example.sellingcourese.repository;

import org.example.sellingcourese.Model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserName(String username);
    List<Account> findByRoleId(Long roleId);

}

