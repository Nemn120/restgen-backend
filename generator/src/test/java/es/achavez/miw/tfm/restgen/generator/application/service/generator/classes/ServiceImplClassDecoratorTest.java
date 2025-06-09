package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class ServiceImplClassDecoratorTest {

    @Mock
    private JavaClassSource javaClassSource;

    @Mock
    private JavaClass javaClass;

    @Mock
    private MavenProjectPath mavenProjectPath;

    private ServiceImplClassDecorator<JavaClassSource> serviceImplClassDecorator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serviceImplClassDecorator = new ServiceImplClassDecorator<>(javaClassSource, javaClass, mavenProjectPath);
    }

    @Test
    void testDecorate() {
        when(javaClass.getName()).thenReturn("TestService");

        serviceImplClassDecorator.decorate();

        verify(javaClassSource).setName("TestServiceImpl");
        verify(javaClassSource, atLeastOnce()).addAnnotation(anyString());
        verify(javaClassSource, atLeastOnce()).addMethod();
    }
}