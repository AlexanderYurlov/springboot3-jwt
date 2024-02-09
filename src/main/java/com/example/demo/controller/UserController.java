package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {

    private static final String BASE_PATH = "user";
    private static final String BY_ID = "/{id}";
    private static final String BASE_PATH_ID = BASE_PATH + BY_ID;

    private UserService userService;

    @GetMapping(BASE_PATH_ID)
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable @NotNull Long id) {
        User user = userService.getUserById(id);
        return new UserDto(user);
    }

    @GetMapping(BASE_PATH)
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> findUsersByFilter(
            @RequestParam(required = false) String dateOfBirth,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = {"name"}) Pageable pageable
    ) {
        Page<UserDto> users = userService.findUsersByFilter(dateOfBirth, email, phone, name, pageable);
        return users;
    }

}
