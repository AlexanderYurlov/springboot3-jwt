package com.example.demo.dto;

import com.example.demo.model.User;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    public UserDto(User user) {
        id = user.getId();
        name = user.getName();
        birthDate = user.getBirthDate();
        password = user.getPassword();
        account = new AccountDto(user.getAccount());
        phones = user.getPhones().stream()
                .map(PhoneDto::new)
                .collect(Collectors.toList());
        emails = user.getEmails().stream()
                .map(EmailDto::new)
                .collect(Collectors.toList());
    }

    @Positive
    private Long id;

    @Size(max = 500)
    private String name;

    private LocalDate birthDate;

    @Size(min = 8, max = 500)
    private String password;

    private AccountDto account;

    private List<PhoneDto> phones;

    private List<EmailDto> emails;

}
