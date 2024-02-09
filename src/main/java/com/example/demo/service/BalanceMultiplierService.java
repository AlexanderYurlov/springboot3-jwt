package com.example.demo.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BalanceMultiplierService {

    private AccountService accountService;

    @Scheduled(fixedDelay = 30_000)
    public void balanceMultiplierTask() {
        log.info("Start BalanceMultiplierService");
        accountService.balanceMultiply();
        log.info("Finished BalanceMultiplierService");
    }

}
