package com.example.demo.repository;

import com.example.demo.model.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface PhoneRepository extends JpaRepository<Phone, Long> {

    Optional<Phone> findByPhone(String phone);

    List<Phone> findAllByUserId(Long userId);
}
