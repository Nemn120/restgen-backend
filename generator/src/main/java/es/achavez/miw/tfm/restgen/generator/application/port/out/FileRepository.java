package es.achavez.miw.tfm.restgen.generator.application.port.out;

import es.achavez.miw.tfm.restgen.generator.domain.GithubRepository;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.GitHubUploadDto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface FileRepository {

    void upload(String uuid,Path projectPath);

    List<File> downloadFolder(String uuid) throws IOException;

    GithubRepository uploadGithub(String urlRepository, GitHubUploadDto dto) throws IOException;

    InputStream downloadZip(String uuid) throws IOException;

}
