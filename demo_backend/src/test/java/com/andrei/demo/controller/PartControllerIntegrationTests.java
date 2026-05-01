package com.andrei.demo.controller;

import com.andrei.demo.model.Part;
import com.andrei.demo.model.Person;
import com.andrei.demo.repository.PartRepository;
import com.andrei.demo.repository.PersonRepository;
import com.andrei.demo.util.JwtUtil;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class PartControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String FIXTURE_PATH = "src/test/resources/fixtures/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        partRepository.deleteAll();
        personRepository.deleteAll();
        partRepository.flush();
        personRepository.flush();

        Person adminUser = new Person();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin.part@test.com");
        adminUser.setPassword("password");
        adminUser.setAge(35);
        adminUser.setRole(com.andrei.demo.model.Role.ADMIN);
        adminUser = personRepository.save(adminUser);

        adminToken = jwtUtil.createToken(adminUser);

        seedDatabase();
    }

    private void seedDatabase() throws Exception {
        String seedDataJson = loadFixture("part_seed.json");
        List<Part> parts = objectMapper.readValue(seedDataJson, new TypeReference<>() {});
        partRepository.saveAll(parts);
    }

    private String loadFixture(String fileName) throws IOException {
        return Files.readString(Paths.get(FIXTURE_PATH + fileName));
    }

    @Test
    void testGetParts() throws Exception {
        mockMvc.perform(get("/part")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetPartById() throws Exception {
        Part savedPart = partRepository.findAll().getFirst();

        mockMvc.perform(get("/part/" + savedPart.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPart.getId().toString()))
                .andExpect(jsonPath("$.name").value(savedPart.getName()));
    }

    @Test
    void testAddPart_ValidPayload() throws Exception {
        String newPartJson = loadFixture("valid_part.json");

        mockMvc.perform(post("/part")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPartJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Brake Pads"))
                .andExpect(jsonPath("$.price").value(45.99));
    }

    @Test
    void testAddPart_InvalidPayload_ReturnsBadRequest() throws Exception {
        String invalidPartJson = loadFixture("invalid_part.json");

        mockMvc.perform(post("/part")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPartJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePart() throws Exception {
        Part savedPart = partRepository.findAll().get(0);
        String updateJson = "{\"name\": \"Upgraded Brakes\", \"price\": 120.00}";

        mockMvc.perform(put("/part/" + savedPart.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Upgraded Brakes"))
                .andExpect(jsonPath("$.price").value(120.00));
    }

    @Test
    void testUpdatePartPrice_Patch() throws Exception {
        Part savedPart = partRepository.findAll().getFirst();

        mockMvc.perform(patch("/part/" + savedPart.getId() + "/price")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("99.99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void testDeletePart() throws Exception {
        Part savedPart = partRepository.findAll().getFirst();

        mockMvc.perform(delete("/part/" + savedPart.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        assert(partRepository.findById(savedPart.getId()).isEmpty());
    }
}