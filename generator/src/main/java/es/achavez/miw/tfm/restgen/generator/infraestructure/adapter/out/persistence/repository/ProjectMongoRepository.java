package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.repository;

import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.ProjectDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectMongoRepository extends MongoRepository<ProjectDocument, String> {

}
