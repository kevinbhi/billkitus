package com.finance.billtick.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import com.finance.billtick.user.model.Role;

@Getter
@Setter
public class UserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be in valid format.")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "FirstName is required")
    @Size(min = 6, message = "FirstName must be at least 6 characters")
    private String firstName;

    @NotBlank(message = "LastName is required")
    @Size(min = 2, message = "LastName must be at least 2 characters")
    private String lastName;

    @NotBlank(message = "Phone is required")
    @Size(min = 10, message = "Phone must be at least 10 characters")
    private String phone;

    @NotNull(message = "Role is required")
    private Role role;
}
