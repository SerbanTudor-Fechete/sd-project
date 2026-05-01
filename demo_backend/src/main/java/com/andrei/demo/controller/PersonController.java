package com.andrei.demo.controller;

import com.andrei.demo.config.ValidationException;
import com.andrei.demo.model.*;
import com.andrei.demo.service.PersonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class PersonController {
    private final PersonService personService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/person")
    public List<Person> getPeople() {
        return personService.getPeople();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/person/{uuid}")
    public Person getPersonById(@PathVariable UUID uuid) {
        return personService.getPersonById(uuid);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/person/email/{email}")
    public Person getPersonByEmail(@PathVariable String email) {
        return personService.getPersonByEmail(email);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/person")
    public Person addPerson(
            @Valid @RequestBody PersonCreateDTO personDTO
    ) {
        return personService.addPerson(personDTO);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/person/{uuid}")
    public Person updatePerson(@PathVariable UUID uuid,
                               @RequestBody Person person)
            throws ValidationException {
        return personService.updatePerson(uuid, person);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/person/{uuid}")
    public void deletePerson(@PathVariable UUID uuid) {
        personService.deletePerson(uuid);
    }

    @PostMapping("/person/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ResetRequest request) {
        personService.initiatePasswordReset(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/person/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetResponse request) {
        personService.completePasswordReset(request);
        return ResponseEntity.ok("Password has been reset successfully.");
    }

    @GetMapping("/person/customers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Person> getOnlyCustomers() {
        return personService.getPeopleByRole(Role.CUSTOMER);
    }

}
