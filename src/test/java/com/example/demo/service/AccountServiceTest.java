package com.example.demo.service;

import com.example.demo.dto.MoneyTransferDto;
import com.example.demo.exception.RejectedTransferException;
import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = "app.scheduling.enable=false")
public class AccountServiceTest {

    @MockBean
    AccountRepository mockAccountRepository;

    @Autowired
    AccountService accountService;

    @Test
    void transferTest() {
        Mockito.when(mockAccountRepository.findByUserId(Mockito.eq(123L))).thenReturn(Optional.of(Account.builder()
                .id(1L)
                .balance(new BigDecimal(100777111))
                .build()));
        Mockito.when(mockAccountRepository.findByUserId(Mockito.eq(777L))).thenReturn(Optional.of(Account.builder()
                .id(7L)
                .balance(new BigDecimal(100111777))
                .build()));
        var updAccount1 = Account.builder()
                .id(1L)
                .balance(new BigDecimal(100555000))
                .build();
        var updAccount2 = Account.builder()
                .id(7L)
                .balance(new BigDecimal(100333888))
                .build();
        accountService.transfer(new MoneyTransferDto(777L, new BigDecimal(222111)), 123L);
        verify(mockAccountRepository, times(1)).saveAll(Mockito.eq(List.of(updAccount1, updAccount2)));
    }

    @Test
    void transferRejectedTest() {
        Mockito.when(mockAccountRepository.findByUserId(125L)).thenReturn(Optional.of(Account.builder()
                .id(1L)
                .balance(new BigDecimal(100111))
                .build()));
        Mockito.when(mockAccountRepository.findByUserId(777L)).thenReturn(Optional.of(Account.builder()
                .id(7L)
                .balance(new BigDecimal(100111777))
                .build()));
        assertThrows(RejectedTransferException.class, () -> accountService.transfer(new MoneyTransferDto(777L, new BigDecimal(222111)), 125L));
    }

//    @TestConfiguration
//    public class TestConfig {
//        @Bean
//        public AccountService testAccountService(@Qualifier("mockAccountRepository") AccountRepository mockAccountRepository) {
//            return new AccountService(mockAccountRepository);
//        }
//    }
}
