package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import org.jboss.forge.roaster.model.source.JavaSource;

public interface JavaClassDecorator<T extends JavaSource> {

    void decorate();

    String printClass();

    T getJavaClassSource();

}
