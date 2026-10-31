package com.emotions.emotions.impls;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.emotions.emotions.entities.User;
import com.emotions.emotions.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findOneByEmail(username);
        if(user.isPresent()) {
            User userObj = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                .username(userObj.getEmail())
                .password(userObj.getPassword())
                .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
