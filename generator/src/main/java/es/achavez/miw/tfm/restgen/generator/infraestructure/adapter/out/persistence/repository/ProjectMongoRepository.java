package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.repository;

import es.achavez.miw.tfm.restgen.generator.domain.ProjectStatus;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.ProjectDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProjectMongoRepository extends MongoRepository<ProjectDocument, String> {

    @Query(value = "{ '_id': ?0 }", fields = "{ 'plantUmlDiagram': 1 }")
    ProjectDocument findPlantUmlDiagramById(String id);

    List<ProjectDocument> findByCreationUser(String user);

    List<ProjectDocument> findByIsPrivateAndStatus(Boolean aFalse, ProjectStatus projectStatus);
}
