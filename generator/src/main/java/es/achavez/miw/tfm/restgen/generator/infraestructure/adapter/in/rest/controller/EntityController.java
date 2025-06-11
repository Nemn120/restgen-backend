package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.controller;

import es.achavez.miw.tfm.restgen.generator.application.service.EntityService;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.EntityDTO;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.mapper.EntityRestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(EntityController.API_ENTITIES)
public class EntityController {

    public static final String API_ENTITIES = "/api/entities";
    @Autowired
    private EntityService entityService;
    @Autowired
    private EntityRestMapper entityMapper;

    @GetMapping("/project/{id}")
    public ResponseEntity<List<EntityDTO>> findByEntityId(
            @PathVariable String id) {
        List<JavaClass> entities = entityService.findByProjectId(id);
        List<EntityDTO> EntityDTO = entityMapper.mapToDTO(entities);
        return ResponseEntity.ok(EntityDTO);
    }

    @GetMapping("/project/{projectId}/class/{className}")
    public ResponseEntity<EntityDTO> findByProjectIdAndEntityId(
            @PathVariable String projectId, @PathVariable String className) {
        JavaClass entity = entityService.findByProjectIdAndClassName(projectId, className);
        EntityDTO EntityDTO = entityMapper.mapToDTO(entity);
        return ResponseEntity.ok(EntityDTO);
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<?> create(@PathVariable String projectId, @RequestBody EntityDTO EntityDTO) {
        JavaClass javaClass = entityMapper.mapToDomain(EntityDTO);
        JavaClass savedEntity = entityService.save(projectId, javaClass);
        return ResponseEntity.created(URI.create(API_ENTITIES + projectId + "/class/" + savedEntity.getName()))
                .build();
    }

    @PutMapping("/project/{projectId}")
    public ResponseEntity<?> update(@PathVariable String projectId, @RequestBody EntityDTO EntityDTO) {
        JavaClass javaClass = entityMapper.mapToDomain(EntityDTO);
        JavaClass savedEntity = entityService.update(projectId, javaClass);
        return ResponseEntity.ok(savedEntity);
    }

    @DeleteMapping("/project/{projectId}/class/{className}")
    public ResponseEntity<Void> delete(@PathVariable String projectId, @PathVariable String className) {
        entityService.delete(projectId, className);
        return ResponseEntity.noContent().build();
    }

}
