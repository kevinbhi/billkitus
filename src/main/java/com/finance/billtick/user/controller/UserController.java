package com.finance.billtick.user.controller;

import com.finance.billtick.user.dto.UserPatchRequest;
import com.finance.billtick.user.dto.UserRequest;
import com.finance.billtick.user.dto.UserResponse;
import com.finance.billtick.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }

    @GetMapping()
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable Integer id, @Valid @RequestBody UserPatchRequest userPatchRequest) {
        return ResponseEntity.ok(userService.patchUser(id, userPatchRequest));
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUser(@RequestParam Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
