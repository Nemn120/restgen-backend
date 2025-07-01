package es.achavez.miw.tfm.restgen.generator.application.service;

import es.achavez.miw.tfm.restgen.generator.application.port.in.GeneratorUsesCases;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.JavaClassGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GeneratorServiceTest {

    @InjectMocks
    private GeneratorUsesCases generatorUsesCases;

    @Mock
    private JavaClassGenerator javaClassGenerator;

    /*@Test
    void testMapInMavenProperties() {
        Project project = mock(Project.class);
        ProjectProperties properties = mock(ProjectProperties.class);
        MavenProperties mavenProperties = mock(MavenProperties.class);
        ApplicationProperties applicationProperties = mock(ApplicationProperties.class);

        when(project.getProperties()).thenReturn(properties);
        when(properties.getMaven()).thenReturn(mavenProperties);
        when(properties.getApplication()).thenReturn(applicationProperties);
        when(mavenProperties.getGroupId()).thenReturn("com.example");
        when(mavenProperties.getArtifactId()).thenReturn("example-artifact");
        when(mavenProperties.getVersion()).thenReturn("1.0.0");
        when(applicationProperties.getPort()).thenReturn(8080);
        when(applicationProperties.getBasePath()).thenReturn("/api");
        when(project.getName()).thenReturn("ExampleProject");

        MavenPropertiesArchetype result = generatorService.mapInMavenProperties(project);

        assert result.getGroupId().equals("com.example");
        assert result.getArtifactId().equals("example-artifact");
        assert result.getVersion().equals("1.0.0");
        assert result.getPort().equals(8080);
        assert result.getBasePath().equals("/api");
        assert result.getAppName().equals("ExampleProject");
    }*/
}