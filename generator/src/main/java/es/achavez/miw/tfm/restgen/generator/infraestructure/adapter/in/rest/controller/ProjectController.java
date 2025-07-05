package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.controller;

import es.achavez.miw.tfm.restgen.generator.application.port.in.GeneratorUsesCases;
import es.achavez.miw.tfm.restgen.generator.application.port.in.ProjectUsesCases;
import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.*;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.mapper.ProjectRestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(ProjectController.API_PROJECTS)
public class ProjectController {

    public static final String API_PROJECTS = "/api/projects";

    private final ProjectUsesCases projectUsesCases;
    private final ProjectRestMapper projectMapper;
    private final GeneratorUsesCases generatorUsesCases;
    @Autowired
    public ProjectController(ProjectUsesCases projectUsesCases, ProjectRestMapper projectMapper, GeneratorUsesCases generatorUsesCases) {
        this.projectUsesCases = projectUsesCases;
        this.projectMapper = projectMapper;
        this.generatorUsesCases = generatorUsesCases;
    }

    @GetMapping
    public List<FindAllProjectResponseDTO> findAllPublic() {
        return projectMapper.mapToFindProjectResponse(projectUsesCases.findAllPublic());
    }

    @GetMapping("/my-projects")
    public List<FindAllProjectResponseDTO> findAllMyProjects(@RequestHeader("Authorization") String token) {
        return projectMapper.mapToFindProjectResponse(
                projectUsesCases.findByUser(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProjectResponseDTO> findById(@PathVariable String id) {
        Project project = projectUsesCases.findById(id);
        GetProjectResponseDTO projectDTO = projectMapper.mapToGetProjectResponse(project);
        return ResponseEntity.ok(projectDTO);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProjectRequestDTO projectDTO) {
        Project project = projectMapper.mapCreateToDomain(projectDTO);
        Project savedProject = projectUsesCases.save(project);
        return ResponseEntity.created(URI.create(API_PROJECTS + savedProject.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody ProjectRequestDTO projectDTO) {
        Project project = projectMapper.mapCreateToDomain(projectDTO);
        project.setId(id);
        Project savedProject = projectUsesCases.update(project);
        return ResponseEntity.ok(savedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        projectUsesCases.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody ProjectIdDTO generate) throws IOException {
        generatorUsesCases.generate(generate.id());
        return ResponseEntity.ok("Project generated");
    }

    @GetMapping("/{id}/clone")
    public ResponseEntity<ProjectIdDTO> clone(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        String idProject = projectUsesCases.cloneProject(token, id);
        ProjectIdDTO projectIdDTO = new ProjectIdDTO(idProject);
        return ResponseEntity.ok(projectIdDTO);
    }

    @PostMapping("/saveAndGenerate")
    public ResponseEntity<ProjectIdDTO> saveAnGenerate(@RequestBody Project project) {
        Project saved = projectUsesCases.save(project);
        ProjectIdDTO projectIdDTO = new ProjectIdDTO(saved.getId());
        return ResponseEntity.ok(projectIdDTO);
    }

    @GetMapping(value = "/{id}/download", produces ="application/zip")
    public ResponseEntity<InputStreamResource> getStreamingResponseBodyResponseEntity(
            @PathVariable String id) throws Exception {
        InputStream responseBody = projectUsesCases.download(id);
        InputStreamResource resource = new InputStreamResource(responseBody);
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment;filename=" + id + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/{id}/diagram")
    public ResponseEntity<GetDiagramDTO> getPlantUmlDiagram(@PathVariable String id) {
        Project project = projectUsesCases.findDiagramPlantUmlById(id);
        if (project == null || project.getPlantUmlDiagram() == null) {
            return ResponseEntity.notFound().build();
        }
        GetDiagramDTO diagramDTO = new GetDiagramDTO(project.getPlantUmlDiagram());
        return ResponseEntity.ok(diagramDTO);
    }

    @PostMapping("/upload/{projectId}")
    public ResponseEntity<String> uploadProjectToGitHub(
            @PathVariable String projectId,
            @RequestBody GitHubUploadDto dto) {
        try {
            projectUsesCases.uploadToGitHub(projectId, dto);
            return ResponseEntity.ok("Proyecto subido exitosamente a GitHub.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al subir el proyecto: " + e.getMessage());
        }
    }
}