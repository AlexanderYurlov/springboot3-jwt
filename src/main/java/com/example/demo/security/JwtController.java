package com.example.demo.security;

import com.example.demo.repository.EmailRepository;
import com.example.demo.repository.PhoneRepository;
import com.example.demo.model.Email;
import com.example.demo.model.Phone;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@Slf4j
@RestController
@AllArgsConstructor
public class JwtController {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,13}$");
    private JwtUserDetailsService userDetailsService;
    private AuthenticationManager authenticationManager;
    private TokenManager tokenManager;
    private PhoneRepository phoneRepository;
    private EmailRepository emailRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> createToken(@RequestBody JwtRequest request) {
        var userId = findUserIdByUsername(request.getUsername());
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userId, request.getPassword()));
        } catch (DisabledException e) {
            log.info("user disabled for userId = {}", userId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (BadCredentialsException e) {
            log.info("invalid credentials for userId = {}", userId);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userDetails = userDetailsService.loadUserByUsername(userId.toString());
        var jwtToken = tokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(jwtToken));
    }

    private Long findUserIdByUsername(String username) {
        Long userId;
        try {
            if (PHONE_PATTERN.matcher(username).matches()) {
                Phone phone = phoneRepository.findByPhone(username)
                        .orElseThrow();
                userId = phone.getUser().getId();
            } else {
                Email email = emailRepository.findByEmail(username)
                        .orElseThrow();
                userId = email.getUser().getId();
            }
        } catch (Exception e) {
            log.warn("Impossible to find User by username: {} ", username);
            return null;
        }
        return userId;
    }

}
