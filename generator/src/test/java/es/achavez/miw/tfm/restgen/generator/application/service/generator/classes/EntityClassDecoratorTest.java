package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.domain.EntityClass;
import es.achavez.miw.tfm.restgen.generator.domain.JavaClass;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class EntityClassDecoratorTest {

    @Mock
    private JavaClassSource javaClassSource;

    @Mock
    private JavaClass javaClass;

    @Mock
    private EntityClass entityClass;

    private EntityClassGenerator<JavaClassSource> entityClassDecorator;

    @Mock
    private MavenProjectPath mavenProjectPath;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(javaClass.getEntity()).thenReturn(entityClass);
        entityClassDecorator = new EntityClassGenerator<>(javaClassSource, javaClass, mavenProjectPath);
    }

    @Test
    void testDecorate() {
        when(javaClass.getName()).thenReturn("TestEntity");
        when(entityClass.getTableName()).thenReturn("test_table");

        entityClassDecorator.decorate();

        verify(javaClassSource).setName("TestEntity");
        verify(javaClassSource, atLeastOnce()).addAnnotation(anyString());
        verify(javaClassSource, atLeastOnce()).addField();
        verify(javaClassSource, atLeastOnce()).addMethod();
    }
}