package com.example.demo.service;

import com.example.demo.exception.ContactValidationException;
import com.example.demo.repository.PhoneRepository;
import com.example.demo.dto.PhoneDto;
import com.example.demo.model.Phone;
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
public class PhoneService {

    private PhoneRepository phoneRepository;

    @Transactional
    public Phone addPhone(User user, PhoneDto phoneDto) {
        checkIsExist(phoneDto);
        return phoneRepository.save(Phone.builder()
                .phone(phoneDto.getPhone())
                .user(user)
                .build());
    }

    @Transactional
    public Phone updatePhone(@NotNull Long id, PhoneDto phoneDto, Long userId) {
        checkIsExist(phoneDto);
        var phone = phoneRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Phone with id = " + id + " not found"));
        if (!userId.equals(phone.getUser().getId())) {
            throw new RuntimeException("User cannot edit another user's contacts");
        }
        phone.setPhone(phoneDto.getPhone());
        return phoneRepository.save(phone);
    }

    @Transactional
    public void removePhone(@NotNull Long id, Long userId) {
        var list = phoneRepository.findAllByUserId(userId);
        var phone = list.stream().filter(e -> e.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Phone with id = " + id + " not found"));
        if (!userId.equals(phone.getUser().getId())) {
            throw new ContactValidationException("User cannot edit another user's contacts");
        }
        if (list.size() == 1) {
            throw new ContactValidationException("It is impossible to delete the only contact id: " + id);
        }
        phoneRepository.delete(phone);
    }

    private void checkIsExist(PhoneDto phoneDto) {
        var isExist = isExist(phoneDto);
        if (isExist) {
            throw new ContactValidationException("Этот номер телефона уже существует в базе данных: " + phoneDto.getPhone());
        }
    }

    private boolean isExist(PhoneDto phoneDto) {
        return phoneRepository.exists(new Example<>() {
            @Override
            public Phone getProbe() {
                return Phone.builder()
                        .phone(phoneDto.getPhone())
                        .build();
            }

            @Override
            public ExampleMatcher getMatcher() {
                return ExampleMatcher.matching()
                        .withIgnorePaths("id")
                        .withMatcher("phone", exact());
            }
        });
    }

}
