package com.finance.billtick.user.dto;
import com.finance.billtick.user.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role; 
}
