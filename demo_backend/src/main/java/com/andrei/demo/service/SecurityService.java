package com.andrei.demo.service;

import com.andrei.demo.model.LoginResponse;
import com.andrei.demo.model.Person;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.util.JwtUtil;
import com.andrei.demo.util.PasswordUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class SecurityService {

    private final PersonRepository personRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    public LoginResponse login(String email, String password) {
        Optional<Person> maybePerson = personRepository.findByEmail(email);

        if(maybePerson.isEmpty()) {
            log.warn("SECURITY EVENT: Failed login attempt for non-existent email: {}", email);
            return new LoginResponse("Invalid email or password");
        }

        Person person = maybePerson.get();
        if(passwordUtil.checkPassword(password, person.getPassword())) {
            log.info("SECURITY EVENT: Successful login for user: {}", email);

            String token = jwtUtil.createToken(person);
            return new LoginResponse(person.getRole().name(), token);

        } else {
            log.warn("SECURITY EVENT: Failed login attempt (bad password) for user: {}", email);
            return new LoginResponse("Invalid email or password");
        }
    }
}