package com.task_management_system.DTO;

import com.task_management_system.ENUMS.UserRole;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;

}
