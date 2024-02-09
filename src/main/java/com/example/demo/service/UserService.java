package com.example.demo.service;

import com.example.demo.repository.UserFilterRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.UserDto;
import com.example.demo.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private UserFilterRepository userFilterRepository;

    @Transactional
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " not found"));
    }

    @Transactional
    public Page<UserDto> findUsersByFilter(String dateOfBirth, String email, String phone, String name, Pageable pageable) {
        var order = pageable.getSort().toString();
        var limit = pageable.getPageSize();
        var offset = pageable.getOffset();
        Long totalCount = userFilterRepository.getTotalCount(dateOfBirth, email, phone, name);
        List<User> users= userFilterRepository.findUsersByFilter(dateOfBirth, email, phone, name, order, limit, offset);
        return new PageImpl<>(users.stream()
                .map(UserDto::new)
                .collect(Collectors.toList()), pageable, Objects.requireNonNull(totalCount));
    }

}
