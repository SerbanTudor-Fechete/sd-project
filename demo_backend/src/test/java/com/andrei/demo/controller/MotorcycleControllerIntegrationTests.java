package com.andrei.demo.controller;

import com.andrei.demo.model.Motorcycle;
import com.andrei.demo.model.Person;
import com.andrei.demo.repository.MotorcycleRepository;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.util.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class MotorcycleControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MotorcycleRepository motorcycleRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String FIXTURE_PATH = "src/test/resources/fixtures/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Person testOwner;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        motorcycleRepository.deleteAll();
        personRepository.deleteAll();
        motorcycleRepository.flush();
        personRepository.flush();

        testOwner = new Person();
        testOwner.setName("Test Owner");
        testOwner.setEmail("owner@test.com");
        testOwner.setPassword("password");
        testOwner.setAge(30);
        testOwner.setRole(com.andrei.demo.model.Role.CUSTOMER);
        testOwner = personRepository.save(testOwner);

        Person adminUser = new Person();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin.moto@test.com");
        adminUser.setPassword("password");
        adminUser.setAge(35);
        adminUser.setRole(com.andrei.demo.model.Role.ADMIN);
        adminUser = personRepository.save(adminUser);

        adminToken = jwtUtil.createToken(adminUser);

        seedDatabase();
    }

    private void seedDatabase() throws Exception {
        String seedDataJson = loadFixture("motorcycle_seed.json");
        List<Motorcycle> bikes = objectMapper.readValue(seedDataJson, new TypeReference<List<Motorcycle>>() {});

        bikes.forEach(bike -> bike.setOwner(testOwner));
        motorcycleRepository.saveAll(bikes);
    }

    @Test
    void testGetMotorcycles() throws Exception {
        mockMvc.perform(get("/motorcycle")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].brand", Matchers.containsInAnyOrder("Honda", "Yamaha")));
    }

    @Test
    void testAddMotorcycle_ValidPayload() throws Exception {
        String validJson = loadFixture("valid_motorcycle.json");

        String finalPayload = validJson.substring(0, validJson.lastIndexOf("}"))
                + ", \"ownerId\": \"" + testOwner.getId().toString() + "\"}";

        mockMvc.perform(post("/motorcycle")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finalPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.brand").value("Suzuki"));
    }

    private String loadFixture(String fileName) throws IOException {
        return Files.readString(Paths.get(FIXTURE_PATH + fileName));
    }

    @Test
    void testGetMotorcycleById() throws Exception {
        Motorcycle savedBike = motorcycleRepository.findAll().getFirst();

        mockMvc.perform(get("/motorcycle/" + savedBike.getId())
                        .header("Authorization", "Bearer " + adminToken)) // 🔥 Token added
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBike.getId().toString()))
                .andExpect(jsonPath("$.brand").value(savedBike.getBrand()));
    }

    @Test
    void testUpdateMotorcycle() throws Exception {
        Motorcycle savedBike = motorcycleRepository.findAll().getFirst();
        String updateJson = loadFixture("valid_motorcycle.json");

        String finalPayload = updateJson.substring(0, updateJson.lastIndexOf("}"))
                + ", \"ownerId\": \"" + testOwner.getId().toString() + "\"}";

        mockMvc.perform(put("/motorcycle/" + savedBike.getId())
                        .header("Authorization", "Bearer " + adminToken) // 🔥 Token added
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finalPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedBike.getId().toString()));
    }

    @Test
    void testDeleteMotorcycle() throws Exception {
        Motorcycle savedBike = motorcycleRepository.findAll().getFirst();

        mockMvc.perform(delete("/motorcycle/" + savedBike.getId())
                        .header("Authorization", "Bearer " + adminToken)) // 🔥 Token added
                .andExpect(status().isOk());

        assert(motorcycleRepository.findById(savedBike.getId()).isEmpty());
    }

    @Test
    void testAddMotorcycle_InvalidPayload_ReturnsBadRequest() throws Exception {
        String invalidJson = loadFixture("invalid_motorcycle.json");

        mockMvc.perform(post("/motorcycle")
                        .header("Authorization", "Bearer " + adminToken) // 🔥 Token added
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}