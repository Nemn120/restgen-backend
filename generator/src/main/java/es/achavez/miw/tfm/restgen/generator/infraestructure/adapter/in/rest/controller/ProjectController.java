package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.controller;

import es.achavez.miw.tfm.restgen.generator.application.service.GeneratorService;
import es.achavez.miw.tfm.restgen.generator.application.service.ProjectService;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.ObjectMapperJSON;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.ObjectMapperYAML;
import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.CreateProjectRequestDTO;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.FindAllProjectResponseDTO;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.GetProjectResponseDTO;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.mapper.ProjectRestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(ProjectController.API_PROJECTS)
public class ProjectController {

    public static final String API_PROJECTS = "/api/projects";

    private final ProjectService projectService;
    private final ProjectRestMapper projectMapper;
    private final GeneratorService generatorService;

    @Autowired
    public ProjectController(ProjectService projectService, ProjectRestMapper projectMapper, GeneratorService generatorService) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.generatorService = generatorService;
    }

    @GetMapping
    public List<FindAllProjectResponseDTO> findAll() {
        return projectMapper.mapToFindProjectResponse(projectService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProjectResponseDTO> findById(@PathVariable String id) {
        Project project = projectService.findById(id);
        GetProjectResponseDTO projectDTO = projectMapper.mapToGetProjectResponse(project);
        return ResponseEntity.ok(projectDTO);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateProjectRequestDTO projectDTO) {
        Project project = projectMapper.mapCreateToDomain(projectDTO);
        Project savedProject = projectService.save(project);
        return ResponseEntity.created(URI.create(API_PROJECTS + savedProject.getId())).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        projectService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generate() throws IOException {
        Project project = ObjectMapperYAML.getInstance().readObjectByPath("src/main/resources/project-02.yml", Project.class);
        generatorService.execute(project);
        return ResponseEntity.ok("Project service is running");
    }
}