package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.s3;

import es.achavez.miw.tfm.restgen.generator.application.port.out.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;

@Repository
public class FileRepositoryImpl implements FileRepository {

    @Autowired
    private S3Service service;

    @Override
    public void upload(String uuid, Path projectPath) {
        service.uploadFolder(uuid, projectPath.toFile());
    }
}
