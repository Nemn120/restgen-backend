package es.achavez.miw.tfm.restgen.generator.application.port.in;

import es.achavez.miw.tfm.restgen.generator.domain.Project;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.GitHubUploadDto;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;

public interface ProjectUsesCases {

    List<Project> findAll();

    Project findById(String id);

    Project save(Project project);

    Project update(Project project);

    void deleteById(String id);

    String cloneProject(String id);

    StreamingResponseBody download(String id) throws IOException;

    Project findDiagramPlantUmlById(String id);

    void uploadToGitHub(String projectId, GitHubUploadDto dto) throws IOException;
}
