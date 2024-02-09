package com.example.demo.dto;

import com.example.demo.exception.RejectedTransferException;
import com.example.demo.model.Account;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto extends OperationResultDto {

    public AccountDto(Account account) {
        id = account.getId();
        balance = account.getBalance();
    }

    public AccountDto(Account account, Boolean result) {
        super(result);
        id = account.getId();
        balance = account.getBalance();
    }

    public AccountDto(RejectedTransferException e) {
        super(e);
    }

    @Positive
    private Long id;
    private BigDecimal balance;

}
