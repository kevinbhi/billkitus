package com.finance.billtick.user.controller;


import com.finance.billtick.user.dto.UserPatchRequest;
import com.finance.billtick.user.dto.UserRequest;
import com.finance.billtick.user.dto.UserResponse;
import com.finance.billtick.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @GetMapping()
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping(value = "/{id}")
    public UserResponse updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequest userRequest) {
        return userService.updateUser(id, userRequest);
    }

    @PatchMapping(value = "/{id}")
    public UserResponse patchUser(@PathVariable Integer id, @Valid @RequestBody UserPatchRequest userPatchRequest) {
        return userService.patchUser(id, userPatchRequest);
    }

    @DeleteMapping()
    public void deleteUser(@RequestParam Integer id) {
        userService.deleteUser(id);
    }

}
