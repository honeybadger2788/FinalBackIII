package com.dh.msusers.service;

import com.dh.msusers.model.User;
import com.dh.msusers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(String id){
        return userRepository.findById(id);
    }

//    public List<User> addUsers(){
//        return userRepository.addUsers();
//    }

}
