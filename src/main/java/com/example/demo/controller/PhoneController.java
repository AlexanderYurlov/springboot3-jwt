package com.example.demo.controller;

import com.example.demo.dto.OperationResultDto;
import com.example.demo.dto.PhoneDto;
import com.example.demo.exception.ContactValidationException;
import com.example.demo.model.Phone;
import com.example.demo.service.PhoneService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class PhoneController {

    private static final String BASE_PATH = "phone";
    private static final String BY_ID = "/{id}";
    private static final String BASE_PATH_ID = BASE_PATH + BY_ID;

    private PhoneService phoneService;
    private UserService userService;

    @PostMapping(BASE_PATH)
    @ResponseStatus(HttpStatus.OK)
    public PhoneDto addPhone(@Valid @RequestBody PhoneDto phoneDto, @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.getUserById(Long.valueOf(userDetails.getUsername()));
        Phone phone;
        try {
            phone = phoneService.addPhone(user, phoneDto);
        } catch (ContactValidationException e) {
            return new PhoneDto(e);
        }
        return new PhoneDto(phone, true);
    }

    @PutMapping(BASE_PATH_ID)
    @ResponseStatus(HttpStatus.OK)
    public PhoneDto updatePhone(@PathVariable Long id, @Valid @RequestBody PhoneDto phoneDto, @AuthenticationPrincipal UserDetails userDetails) {
        var userId = Long.valueOf(userDetails.getUsername());
        Phone phone;
        try {
            phone = phoneService.updatePhone(id, phoneDto, userId);
        } catch (ContactValidationException e) {
            return new PhoneDto(e);
        }
        return new PhoneDto(phone, true);
    }

    @DeleteMapping(BASE_PATH_ID)
    @ResponseStatus(HttpStatus.OK)
    public OperationResultDto removePhone(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        var userId = Long.valueOf(userDetails.getUsername());
        try {
            phoneService.removePhone(id, userId);
            return new OperationResultDto(true);
        } catch (ContactValidationException e) {
            return new OperationResultDto(e);
        }
    }

}
