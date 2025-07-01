package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.controller;

import es.achavez.miw.tfm.restgen.generator.application.port.in.EntityUsesCases;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.EntityDTO;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.FindAllEntityDTO;
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
    private EntityUsesCases entityUsesCases;
    @Autowired
    private EntityRestMapper entityMapper;

    @GetMapping("/project/{id}")
    public ResponseEntity<List<FindAllEntityDTO>> findByEntityId(
            @PathVariable String id) {
        List<JavaClass> entities = entityUsesCases.findByProjectId(id);
        List<FindAllEntityDTO> EntityDTO = entityMapper.mapToFindAllDTO(entities);
        return ResponseEntity.ok(EntityDTO);
    }

    @GetMapping("/project/{projectId}/class/{className}")
    public ResponseEntity<EntityDTO> findByProjectIdAndEntityId(
            @PathVariable String projectId, @PathVariable String className) {
        JavaClass entity = entityUsesCases.findByProjectIdAndClassName(projectId, className);
        EntityDTO EntityDTO = entityMapper.mapToDTO(entity);
        return ResponseEntity.ok(EntityDTO);
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<?> create(@PathVariable String projectId, @RequestBody EntityDTO EntityDTO) {
        JavaClass javaClass = entityMapper.mapToDomain(EntityDTO);
        JavaClass savedEntity = entityUsesCases.saveOrUpdate(projectId, javaClass);
        return ResponseEntity.created(URI.create(API_ENTITIES + projectId + "/class/" + savedEntity.getName()))
                .build();
    }

    @DeleteMapping("/project/{projectId}/class/{className}")
    public ResponseEntity<Void> delete(@PathVariable String projectId, @PathVariable String className) {
        entityUsesCases.delete(projectId, className);
        return ResponseEntity.noContent().build();
    }

}
