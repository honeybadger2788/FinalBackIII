package com.dh.msusers.controller;

import com.dh.msusers.model.User;
import com.dh.msusers.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id){
        return ResponseEntity.ok().body((service.findById(id)));
    }

//    @PostMapping("/create")
//    public ResponseEntity<List<User>> addUsers(){
//        return ResponseEntity.ok().body(service.addUsers());
//    }

}
