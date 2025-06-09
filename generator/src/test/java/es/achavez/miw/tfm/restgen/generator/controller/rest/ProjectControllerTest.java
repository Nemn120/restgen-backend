package es.achavez.miw.tfm.restgen.generator.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.achavez.miw.tfm.restgen.generator.domain.ProjectStatus;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.ProjectDocument;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.repository.ProjectMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectMongoRepository projectMongoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        projectMongoRepository.deleteAll();
        generateRandomProjects(3);
    }

    public void generateRandomProjects(int count) {
        for (int i = 0; i < count; i++) {
            projectMongoRepository.save(ProjectDocument.builder()
                    .id(String.valueOf(i + 1))
                    .name("Project " + (i + 1))
                    .description("Description " + (i + 1))
                    .status(ProjectStatus.CREATED)
                    .creationDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .creationUser("user" + (i + 1))
                    .updateUser("user" + (i + 1))
                    .urlRepository("http://repo" + (i + 1) + ".com")
                    .build());
        }
    }

    @Test
    void shouldFindAll() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void shouldFindById() throws Exception {
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isNotEmpty());
    }

    @Test
    void shouldDelete() throws Exception {
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetProjectById() throws Exception {
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Project 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void shouldGenerateProject() throws Exception {
        mockMvc.perform(get("/api/projects/generate"))
                .andExpect(status().isOk());
    }
}