package com.example.demo.controller;

import com.example.demo.dto.EmailDto;
import com.example.demo.dto.OperationResultDto;
import com.example.demo.exception.ContactValidationException;
import com.example.demo.model.Email;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class EmailController {

    private static final String BASE_PATH = "email";
    private static final String BY_ID = "/{id}";
    private static final String BASE_PATH_ID = BASE_PATH + BY_ID;

    private EmailService emailService;
    private UserService userService;

    @PostMapping(BASE_PATH)
    @ResponseStatus(HttpStatus.OK)
    public EmailDto addEmail(@Valid @RequestBody EmailDto emailDto, @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.getUserById(Long.valueOf(userDetails.getUsername()));
        Email email;
        try {
            email = emailService.addEmail(user, emailDto);
        } catch (ContactValidationException e) {
            return new EmailDto(e);
        }
        return new EmailDto(email, true);
    }

    @PutMapping(BASE_PATH_ID)
    @ResponseStatus(HttpStatus.OK)
    public EmailDto updateEmail(@PathVariable("id") Long id, @Valid @RequestBody EmailDto emailDto, @AuthenticationPrincipal UserDetails userDetails) {
        var userId = Long.valueOf(userDetails.getUsername());
        Email email;
        try {
            email = emailService.updateEmail(id, emailDto, userId);
        } catch (ContactValidationException e) {
            return new EmailDto(e);
        }
        return new EmailDto(email, true);
    }

    @DeleteMapping(BASE_PATH_ID)
    @ResponseStatus(HttpStatus.OK)
    public OperationResultDto removeEmail(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        var userId = Long.valueOf(userDetails.getUsername());
        try {
            emailService.removeEmail(id, userId);
            return new OperationResultDto(true);
        } catch (ContactValidationException e) {
            return new OperationResultDto(e);
        }
    }

}
