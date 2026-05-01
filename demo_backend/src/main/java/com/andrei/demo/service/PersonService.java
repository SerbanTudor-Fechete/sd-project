package com.andrei.demo.service;

import com.andrei.demo.config.DuplicateEmailException;
import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.Person;
import com.andrei.demo.model.PersonCreateDTO;
import com.andrei.demo.model.ResetResponse;
import com.andrei.demo.model.Role;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.util.PasswordUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PasswordUtil passwordUtil;
    private final EmailService emailService;

    public List<Person> getPeople() {
        return personRepository.findAll();
    }

    public Person addPerson(PersonCreateDTO personDTO) {

        if (personRepository.existsByEmail(personDTO.getEmail())) {
            throw new DuplicateEmailException("This email is already in use.");
        }

        Person person = new Person();

        person.setName(personDTO.getName());
        person.setAge(personDTO.getAge());
        person.setEmail(personDTO.getEmail());
        String hashedPassword = passwordUtil.hashPassword(personDTO.getPassword());
        person.setPassword(hashedPassword);

        if (personDTO.getRole() != null && personDTO.getRole().equalsIgnoreCase("ADMIN")) {
            person.setRole(Role.ADMIN);
        } else {
            person.setRole(Role.CUSTOMER);
        }

        return personRepository.save(person);
    }

    public Person updatePerson(UUID uuid, Person person) throws ValidationException{
        Optional<Person> personOptional =
                personRepository.findById(uuid);

        if(personOptional.isEmpty()) {
            throw new ValidationException("Person with id " + uuid + " not found");
        }
        Person existingPerson = personOptional.get();

        existingPerson.setName(person.getName());
        existingPerson.setAge(person.getAge());
        existingPerson.setEmail(person.getEmail());

        return personRepository.save(existingPerson);
    }

    public void deletePerson(UUID uuid) {
        personRepository.deleteById(uuid);
    }

    public Person getPersonByEmail(String email) {
        return personRepository.findByEmail(email).orElseThrow(
                () -> new IllegalStateException("Person with email " + email + " not found"));
    }

    public Person getPersonById(UUID uuid) {
        return personRepository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("Person with id " + uuid + " not found"));
    }

    public List<Person> getPeopleByRole(Role role) {
        return personRepository.findByRole(role);
    }

    public void initiatePasswordReset(String email) {
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email."));

        String code = String.format("%06d", new java.util.Random().nextInt(999999));

        person.setResetCode(code);
        person.setResetCodeExpiresAt(java.time.LocalDateTime.now().plusMinutes(15));
        personRepository.save(person);

        emailService.sendResetCode(email, code);
    }

    @Transactional
    public void completePasswordReset(ResetResponse dto) {
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new RuntimeException("Passwords do not match!");
        }

        Person person = personRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (person.getResetCode() == null || !person.getResetCode().equals(dto.code())) {
            throw new RuntimeException("Invalid verification code.");
        }

        if (person.getResetCodeExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Verification code has expired.");
        }

        person.setPassword(passwordUtil.hashPassword(dto.newPassword()));
        person.setResetCode(null);
        personRepository.save(person);

        emailService.sendResetConfirmation(person.getEmail());
    }
}
