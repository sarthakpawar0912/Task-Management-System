package com.task_management_system.SERVICES.AUTH;


import com.task_management_system.DTO.SignupRequest;
import com.task_management_system.DTO.UserDto;

public interface AuthService {
    void createAnAdminAccount();

    UserDto signupUser(SignupRequest signupRequest);

    boolean   hasUserWithEmail(String email);
}
