package com.finance.billtick.user.dto;

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
}
