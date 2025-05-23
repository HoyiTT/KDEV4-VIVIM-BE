package com.welcommu.moduleservice.user.dto;

import com.welcommu.moduledomain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String companyName;
    private String companyRole;
    private Boolean isDeleted;;


    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone(),
            user.getCompany() != null ? user.getCompany().getName() : null,
            user.getCompany() != null ? user.getCompany().getCompanyRole().toString() : null,
            user.getIsDeleted()
        );
    }
}
