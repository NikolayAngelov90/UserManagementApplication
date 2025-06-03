package com.usermanagement.web.mapper;

import com.usermanagement.model.User;
import com.usermanagement.web.dto.UserInfoResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static UserInfoResponse fromUser(User entity) {
        return UserInfoResponse.builder()
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .dateOfBirth(entity.getDateOfBirth())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
