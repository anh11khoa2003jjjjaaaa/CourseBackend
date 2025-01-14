package org.example.sellingcourese.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    public String displayName;
    public String email;
    public String passWord;
    public String phone;
    public String address;
    public String userName;
    public Long roleId;

}
