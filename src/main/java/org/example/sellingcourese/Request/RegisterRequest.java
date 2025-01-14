package org.example.sellingcourese.Request;

import lombok.Data;
import org.example.sellingcourese.Model.Account;
import org.example.sellingcourese.Model.User;

@Data
public class RegisterRequest {
    private Account account;
    private User user;

}
