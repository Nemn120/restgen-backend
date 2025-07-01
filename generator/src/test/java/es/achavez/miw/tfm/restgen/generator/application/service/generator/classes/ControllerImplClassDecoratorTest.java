package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class ControllerImplClassDecoratorTest {

    @Mock
    private JavaClassSource javaClassSource;

    @Mock
    private JavaClass javaClass;

    @Mock
    private MavenProjectPath mavenProjectPath;

    private ControllerImplClassGenerator<JavaClassSource> controllerImplClassDecorator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controllerImplClassDecorator = new ControllerImplClassGenerator<>(javaClassSource, javaClass, mavenProjectPath);
    }

    @Test
    void testDecorate() {
        when(javaClass.getName()).thenReturn("TestEntity");

        controllerImplClassDecorator.decorate();

        verify(javaClassSource).setName("TestEntityController");
        verify(javaClassSource).setPackage("com.example.controller");
        verify(javaClassSource, atLeastOnce()).addAnnotation(anyString());
        verify(javaClassSource, atLeastOnce()).addField();
        verify(javaClassSource, atLeastOnce()).addMethod();
    }
}
