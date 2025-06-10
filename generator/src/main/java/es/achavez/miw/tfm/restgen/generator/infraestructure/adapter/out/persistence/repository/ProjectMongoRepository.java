package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.repository;

import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.persistence.document.ProjectDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProjectMongoRepository extends MongoRepository<ProjectDocument, String> {

    @Query(value = "{ '_id': ?0 }", fields = "{ 'classes': 1 }")
    List<JavaClass> findJavaClassByProjectId(String id);

    @Query(value = "{ '_id': ?0, 'classes.name': ?1 }", fields = "{ 'classes.$': 1 }")
    JavaClass findJavaClassByProjectIdAndClassName(String id, String className);
}
