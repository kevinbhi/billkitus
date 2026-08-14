package com.finance.billtick.user.controller;


import com.finance.billtick.user.model.User;
import com.finance.billtick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping("/")

    public User createUser(@RequestBody User userrequest) {
        User user = new User();
        user.setEmail(userrequest.getEmail());
        user.setPassword(userrequest.getPassword());
        user.setFirstName(userrequest.getFirstName());
        user.setLastName(userrequest.getLastName());
        user.setPhone(userrequest.getPhone());
        userRepository.save(user);
        return user;
    }

    @GetMapping(value = "/")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping(value = "/{Id}")

    public User updateUser(@PathVariable Long Id, @RequestBody User userrequest) {
        User user = userRepository.findById(Id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(userrequest.getEmail());
        user.setPassword(userrequest.getPassword());
        user.setFirstName(userrequest.getFirstName());
        user.setLastName(userrequest.getLastName());
        user.setPhone(userrequest.getPhone());
        userRepository.save(user);
        return user;
    }

    @DeleteMapping(value = "/")
    public void deleteUser(@RequestParam int id) {
        userRepository.deleteById((long) id);
    }

}
