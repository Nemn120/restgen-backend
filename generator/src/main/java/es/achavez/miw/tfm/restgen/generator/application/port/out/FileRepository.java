package es.achavez.miw.tfm.restgen.generator.application.port.out;

import java.nio.file.Path;

public interface FileRepository {

    void upload(String uuid,Path projectPath);

}
