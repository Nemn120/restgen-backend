package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.s3;

import es.achavez.miw.tfm.restgen.generator.application.port.out.FileRepository;
import es.achavez.miw.tfm.restgen.generator.domain.GithubRepository;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.GitHubUploadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Repository
public class FileRepositoryImpl implements FileRepository {

    @Autowired
    private S3Repository service;

    @Override
    public void upload(String uuid, Path projectPath) {
        service.uploadFolder(uuid, projectPath.toFile());
    }

    @Override
    public List<File> downloadFolder(String uuid) throws IOException {
        return service.downloadFolder(uuid);
    }

    @Override
    public GithubRepository uploadGithub(String urlRepository, GitHubUploadDto dto) throws IOException {
        return service.uploadGithub(urlRepository, dto);
    }
}
