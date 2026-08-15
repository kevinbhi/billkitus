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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        User user = userMapper.toUser(userRequest);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userMapper.toUserResponseList(userRepository.findAll());
    }

    @Transactional
    public UserResponse updateUser(Integer id, UserRequest userRequest) {
        User user = assertUser(id);
        userMapper.updateUser(userRequest, user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse patchUser(Integer id, UserPatchRequest userPatchRequest) {
        User user = assertUser(id);
        userMapper.patchUser(userPatchRequest, user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Integer id) {
        User user = assertUser(id);
        userRepository.delete(user);
    }

    private User assertUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
