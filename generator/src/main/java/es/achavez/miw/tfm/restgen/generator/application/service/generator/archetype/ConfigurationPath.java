package es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigurationPath {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationPath.class);

    private Path applicationPath;

    public ConfigurationPath(String uuid) {
        LOG.info("ConfigurationPath :: ConfigurationPath()");
        try
        {

            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/properties/configuration.properties"));
            String pathProject = properties.getProperty("path-project");
            if(StringUtils.hasText(pathProject)){
                String userHome = System.getProperty("user.dir");
                applicationPath = Paths.get(Paths.get(userHome).getParent().toString(), pathProject,uuid);
                if (!applicationPath.toFile().exists()){
                    applicationPath.toFile().mkdir();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Path getApplicationPath() {
        return applicationPath;
    }

    public static void main(String[] args) {
        ConfigurationPath configurationPath = new ConfigurationPath("test-uuid");
        System.out.println("Application Path: " + configurationPath.getApplicationPath());
    }
}
