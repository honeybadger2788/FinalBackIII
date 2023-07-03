package com.dh.msusers.repository;

import com.dh.msusers.model.User;

import java.util.List;

public interface UserRepository {

  User findById(String id);

}