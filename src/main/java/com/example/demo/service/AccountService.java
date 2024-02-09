package com.example.demo.service;

import com.example.demo.dto.MoneyTransferDto;
import com.example.demo.exception.RejectedTransferException;
import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

    private static final Map<Long, BigDecimal> BALANCE_LIMIT = new HashMap<>();
    private static final BigDecimal MAX_PERCENT = new BigDecimal("3.07");
    private static final BigDecimal ONE_TIME_PERCENT = new BigDecimal("1.1");
    private AccountRepository accountRepository;

    @Transactional
    public Account transfer(MoneyTransferDto transferDto, Long userId) {
        var account = accountRepository.findByUserId(userId)
                .orElseThrow();
        var receiverAccount = accountRepository.findByUserId(transferDto.getReceiverId())
                .orElseThrow();
        BigDecimal userBalance = account.getBalance().subtract(transferDto.getAmount());
        BigDecimal userReceiverBalance;
        if (userBalance.compareTo(new BigDecimal(0)) > 0) {
            account.setBalance(userBalance);
            userReceiverBalance = receiverAccount.getBalance().add(transferDto.getAmount());
            receiverAccount.setBalance(userReceiverBalance);
            accountRepository.saveAll(List.of(account, receiverAccount));
            return account;
        } else {
            log.info("Not enough money for transfer for userId: " + userId);
            throw new RejectedTransferException("Not enough money for transfer for userId: " + userId);
        }
    }

    @Transactional
    public void balanceMultiply() {
        var updateList = new ArrayList<Account>();
        var accountList = accountRepository.findAllAccounts();
        for (Account account : accountList) {
            Long id = account.getId();
            if (BALANCE_LIMIT.containsKey(id)) {
                var newBalance = account.getBalance().multiply(ONE_TIME_PERCENT);
                if (newBalance.compareTo(BALANCE_LIMIT.get(id)) <= 0) {
                    account.setBalance(newBalance);
                    updateList.add(account);
                }
            } else {
                BALANCE_LIMIT.put(id, account.getBalance().multiply(MAX_PERCENT));
                account.setBalance(account.getBalance().multiply(ONE_TIME_PERCENT));
                updateList.add(account);
            }
        }
        accountRepository.saveAll(updateList);
    }
}
