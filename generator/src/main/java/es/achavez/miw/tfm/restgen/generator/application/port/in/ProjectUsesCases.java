package es.achavez.miw.tfm.restgen.generator.application.port.in;

import es.achavez.miw.tfm.restgen.generator.domain.GithubRepository;
import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.GitHubUploadDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ProjectUsesCases {

    List<Project> findAllPublic();

    Project findById(String id);

    Project save(Project project);

    Project update(Project project);

    void deleteById(String id);

    String cloneProject(String token, String id);

    InputStream download(String id) throws IOException;

    Project findDiagramPlantUmlById(String id);

    GithubRepository uploadToGitHub(String projectId, GitHubUploadDto dto) throws IOException;

    List<Project> findByUser(String token);
}
