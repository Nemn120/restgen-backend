package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.controller;

import es.achavez.miw.tfm.restgen.generator.application.service.GeneratorService;
import es.achavez.miw.tfm.restgen.generator.application.service.ProjectService;
import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.*;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.mapper.ProjectRestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
    public ResponseEntity<?> create(@RequestBody ProjectRequestDTO projectDTO) {
        Project project = projectMapper.mapCreateToDomain(projectDTO);
        Project savedProject = projectService.save(project);
        return ResponseEntity.created(URI.create(API_PROJECTS + savedProject.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody ProjectRequestDTO projectDTO) {
        Project project = projectMapper.mapCreateToDomain(projectDTO);
        project.setId(id);
        Project savedProject = projectService.update(project);
        return ResponseEntity.ok(savedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        projectService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody ProjectIdDTO generate) throws IOException {
        generatorService.execute(generate.id());
        return ResponseEntity.ok("Project generated");
    }

    @GetMapping("/clone")
    public ResponseEntity<ProjectIdDTO> clone(@RequestBody ProjectIdDTO id) {
        String idProject = projectService.cloneProject(id.id());
        ProjectIdDTO projectIdDTO = new ProjectIdDTO(idProject);
        return ResponseEntity.ok(projectIdDTO);
    }

    @PostMapping("/saveAndGenerate")
    public ResponseEntity<ProjectIdDTO> saveAnGenerate(@RequestBody Project project) {
        Project saved = projectService.save(project);
        ProjectIdDTO projectIdDTO = new ProjectIdDTO(saved.getId());
        return ResponseEntity.ok(projectIdDTO);
    }

    @GetMapping(value = "/{id}/download", produces ="application/zip")
    public ResponseEntity<StreamingResponseBody> getStreamingResponseBodyResponseEntity(
            @PathVariable String id) throws Exception {
        StreamingResponseBody responseBody = projectService.download(id);
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment;filename=" + id + ".zip")
                .contentType(MediaType.valueOf("application/zip"))
                .body(responseBody);
    }

    @GetMapping("/{id}/diagram")
    public ResponseEntity<GetDiagramDTO> getPlantUmlDiagram(@PathVariable String id) {
        Project project = projectService.findDiagramPlantUmlById(id);
        if (project == null || project.getPlantUmlDiagram() == null) {
            return ResponseEntity.notFound().build();
        }
        GetDiagramDTO diagramDTO = new GetDiagramDTO(project.getPlantUmlDiagram());
        return ResponseEntity.ok(diagramDTO);
    }
}