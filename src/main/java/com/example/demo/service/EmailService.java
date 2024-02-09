package com.example.demo.service;

import com.example.demo.repository.EmailRepository;
import com.example.demo.dto.EmailDto;
import com.example.demo.exception.ContactValidationException;
import com.example.demo.model.Email;
import com.example.demo.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Component
@AllArgsConstructor
public class EmailService {

    private EmailRepository emailRepository;

    @Transactional
    public Email addEmail(User user, EmailDto email) {
        checkIsExist(email);
        return emailRepository.save(Email.builder()
                .email(email.getEmail())
                .user(user)
                .build());
    }

    @Transactional
    public Email updateEmail(@NotNull Long id, EmailDto emailDto, Long userId) {
        checkIsExist(emailDto);
        var email = emailRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Email with id = " + id + " not found"));
        if (!userId.equals(email.getUser().getId())) {
            throw new RuntimeException("User cannot edit another user's contacts");
        }
        email.setEmail(emailDto.getEmail());
        return emailRepository.save(email);
    }

    @Transactional
    public void removeEmail(@NotNull Long id, Long userId) {
        var list = emailRepository.findAllByUserId(userId);
        var email = list.stream().filter(e -> e.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Email with id = " + id + " not found"));
        if (!userId.equals(email.getUser().getId())) {
            throw new ContactValidationException("User cannot edit another user's contacts");
        }
        if (list.size() == 1) {
            throw new ContactValidationException("It is impossible to delete the only contact id: " + id);
        }
        emailRepository.delete(email);
    }

    private void checkIsExist(EmailDto emailDto) {
        var isExist = isExist(emailDto);
        if (isExist) {
            throw new ContactValidationException("Этот емэйл уже существует в базе данных: " + emailDto.getEmail());
        }
    }

    private boolean isExist(EmailDto emailDto) {
        return emailRepository.exists(new Example<>() {
            @Override
            public Email getProbe() {
                return Email.builder()
                        .email(emailDto.getEmail())
                        .build();
            }

            @Override
            public ExampleMatcher getMatcher() {
                return ExampleMatcher.matching()
                        .withIgnorePaths("id")
                        .withMatcher("email", exact());
            }
        });
    }

}
