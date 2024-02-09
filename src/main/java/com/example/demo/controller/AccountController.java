package com.example.demo.controller;

import com.example.demo.dto.AccountDto;
import com.example.demo.dto.MoneyTransferDto;
import com.example.demo.exception.RejectedTransferException;
import com.example.demo.model.Account;
import com.example.demo.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AccountController {

    private static final String BASE_PATH = "account";

    private AccountService accountService;

    @PostMapping(BASE_PATH)
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getUserById(@Valid @RequestBody MoneyTransferDto transferDto, @AuthenticationPrincipal UserDetails userDetails) {
        var userId = Long.valueOf(userDetails.getUsername());
        try {
            Account account = accountService.transfer(transferDto, userId);
        return new AccountDto(account, true);
        } catch (RejectedTransferException e) {
            return new AccountDto(e);
        }
    }

}
