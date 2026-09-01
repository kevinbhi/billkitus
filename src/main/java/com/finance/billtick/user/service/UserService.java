package com.finance.billtick.user.service;

import com.finance.billtick.exception.ResourceNotFoundException;
import com.finance.billtick.user.dto.UserPatchRequest;
import com.finance.billtick.user.dto.UserRequest;
import com.finance.billtick.user.dto.UserResponse;
import com.finance.billtick.user.mapper.UserMapper;
import com.finance.billtick.user.model.User;
import com.finance.billtick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;



    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        User user = userMapper.toUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userMapper.toUserResponseList(userRepository.findAll());
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = assertUser(id);
        userMapper.updateUser(userRequest, user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse patchUser(Long id, UserPatchRequest userPatchRequest) {
        User user = assertUser(id);
        userMapper.patchUser(userPatchRequest, user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = assertUser(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private User assertUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
