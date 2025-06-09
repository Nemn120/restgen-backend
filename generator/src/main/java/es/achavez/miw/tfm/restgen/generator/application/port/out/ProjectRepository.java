package es.achavez.miw.tfm.restgen.generator.application.port.out;

import es.achavez.miw.tfm.restgen.generator.domain.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(String id);

    List<Project> findAll();

    void deleteById(String id);
}
