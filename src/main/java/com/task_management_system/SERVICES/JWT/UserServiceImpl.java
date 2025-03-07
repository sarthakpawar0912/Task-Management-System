package com.task_management_system.SERVICES.JWT;
import com.task_management_system.ENTITY.User;
import com.task_management_system.REPOSITORY.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements  UserService{

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetailsService userDetailsService() {
        return this::loadUserByUsername ;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch user by email from the repository
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        System.out.println("User found: " + email);

        // Return a Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getUserRole().name())) // Convert role to GrantedAuthority
        );
    }

   /* public UserDetailsService userDetailsService() {
        return this;
    }*/
}
