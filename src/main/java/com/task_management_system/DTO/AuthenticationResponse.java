package com.task_management_system.DTO;

import com.task_management_system.ENUMS.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {
    private String jwt;
    private Long UserId;
    private UserRole userRole;}
