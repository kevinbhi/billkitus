package com.finance.billtick.user.mapper;

import com.finance.billtick.user.dto.UserPatchRequest;
import com.finance.billtick.user.dto.UserRequest;
import com.finance.billtick.user.dto.UserResponse;
import com.finance.billtick.user.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "role", source = "role", defaultValue = "USER")
    User toUser(UserRequest userRequest);

    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponseList(List<User> users);

    @Mapping(target = "role", source = "role", defaultValue = "USER")
    void updateUser(UserRequest userRequest, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchUser(UserPatchRequest userRequest, @MappingTarget User user);
}
