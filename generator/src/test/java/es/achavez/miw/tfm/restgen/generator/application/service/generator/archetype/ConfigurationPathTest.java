package es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

class ConfigurationPathTest {

    private static final String UUID = "test-uuid";
    private static final String PATH_PROJECT = "test-project";

    @BeforeEach
    void setup() {
        try (MockedStatic<ConfigurationPath> mockedStatic = mockStatic(ConfigurationPath.class)) {
            Properties properties = new Properties();
            properties.setProperty("path-project", PATH_PROJECT);
            InputStream inputStream = new ByteArrayInputStream(("path-project=" + PATH_PROJECT).getBytes());

            mockedStatic.when(() -> ConfigurationPath.class.getResourceAsStream("/properties/configuration.properties"))
                    .thenReturn(inputStream);
        }
    }

    @Test
    void testGetApplicationPath() {
        try (MockedStatic<System> systemMock = mockStatic(System.class);
             MockedStatic<StringUtils> stringUtilsMock = mockStatic(StringUtils.class)) {

            systemMock.when(() -> System.getProperty("user.dir")).thenReturn("/user/home");

            stringUtilsMock.when(() -> StringUtils.hasText(PATH_PROJECT)).thenReturn(true);

            ConfigurationPath configurationPath = new ConfigurationPath(UUID);

            Path expectedPath = Paths.get("/user", "test-project", UUID);
            assertEquals(expectedPath, configurationPath.getApplicationPath());
        }
    }
}