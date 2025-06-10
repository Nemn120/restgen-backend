package es.achavez.miw.tfm.restgen.generator.application.port.out;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileRepository {

    void upload(String uuid,Path projectPath);

    List<File> downloadFolder(String uuid) throws IOException;

}
