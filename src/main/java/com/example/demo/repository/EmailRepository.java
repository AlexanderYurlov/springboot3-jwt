package com.example.demo.repository;

import com.example.demo.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface EmailRepository extends JpaRepository<Email, Long> {

    Optional<Email> findByEmail(String email);
    List<Email> findAllByUserId(Long userId);

}
