package org.example.medmanagement.service;

import org.example.medmanagement.dto.RegisterRequest;
import org.example.medmanagement.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto register(RegisterRequest request);
    UserDto create(UserDto dto);
    UserDto findById(Long id);
    UserDto update(Long id, UserDto dto);
    void delete(Long id);
    UserDto findByEmail(String email);
    List<UserDto> findAll();
}
