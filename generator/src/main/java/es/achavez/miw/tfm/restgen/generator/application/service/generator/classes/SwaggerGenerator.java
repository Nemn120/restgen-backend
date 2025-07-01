package es.achavez.miw.tfm.restgen.generator.application.service.generator.classes;

import es.achavez.miw.tfm.restgen.generator.application.service.generator.archetype.MavenProjectPath;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.directory.PackageDirectory;
import es.achavez.miw.tfm.restgen.generator.application.service.generator.directory.PackageDirectoryLayer;
import es.achavez.miw.tfm.restgen.generator.domain.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import static es.achavez.miw.tfm.restgen.generator.domain.AnnotationPersistence.*;

public class SwaggerGenerator {

    private static final Logger logger = LogManager.getLogger(SwaggerGenerator.class);
    
    private static final String swaggerClassName = "Swagger";

    protected JavaClassSource javaClassSource;
    protected SwaggerJavaClass swaggerJavaClass;
    protected MavenProjectPath mavenProjectPath;
    protected PackageDirectory packageDirectory;

    public SwaggerGenerator(JavaClassSource javaClassSource, SwaggerJavaClass swaggerJavaClass, MavenProjectPath mavenProjectPath) {
        this.swaggerJavaClass = swaggerJavaClass;
        this.javaClassSource = javaClassSource;
        this.packageDirectory = new PackageDirectory(this.swaggerJavaClass, mavenProjectPath);
    }

    public void decorate() {
        logger.info("decorate: decorando: "+ this.getClass().getSimpleName());
        addPackageClass();
        String swaggerClassName = SwaggerGenerator.swaggerClassName + DirectoryLayerPath.CONFIG.getClassName();
        javaClassSource.setName(swaggerClassName);

        addAnnotationAndImport(CONFIGURATION);
        javaClassSource.addImport(SECURITY_SCHEME_TYPE.getPackageName());
        javaClassSource.addImport(SECURITY_SCHEME_IN.getPackageName());
        javaClassSource.addImport(OPEN_API_GROUPED.getPackageName());
        javaClassSource.addImport(OPEN_API.getPackageName());
        javaClassSource.addImport(OPEN_API_INFO.getPackageName());

        javaClassSource.addImport("org.springframework.context.annotation.Bean");
        javaClassSource.addImport("io.swagger.v3.oas.models.info.Info");


        addSecuritySchemeAnnotation();
        addGroupedOpenApiMethods(swaggerJavaClass);
        addApiInfoMethod(swaggerJavaClass);
    }

    protected void addAnnotationAndImport(AnnotationPersistence entity) {
        this.javaClassSource.addImport(entity.getPackageName());
        javaClassSource.addAnnotation(entity.getAnnotationName());
    }

    protected void addPackageClass(){
        PackageDirectoryLayer layer = packageDirectory.getLayer(DirectoryLayerPath.CONFIG);
        javaClassSource.setPackage(layer.getPackagePath());
    }

    private void addSecuritySchemeAnnotation() {
        javaClassSource.addAnnotation("SecurityScheme")
                .setLiteralValue("type", "SecuritySchemeType.HTTP")
                .setLiteralValue("in", "SecuritySchemeIn.DEFAULT")
                .setLiteralValue("name", "\"JWT\"")
                .setLiteralValue("scheme", "\"bearer\"");
    }

    private void addGroupedOpenApiMethods(SwaggerJavaClass swaggerJavaClass) {
        for (DocketField field : swaggerJavaClass.getDocketFields()) {
            javaClassSource.addMethod()
                    .setName("api" + field.getCamelCaseName())
                    .setReturnType("GroupedOpenApi")
                    .setPublic()
                    .setBody("return GroupedOpenApi.builder()\n" +
                            "        .group(\"" +swaggerJavaClass.getApiName() + "-" + field.getApiName() +"\")\n" +
                            "        .pathsToMatch(\"/" + field.getApiName() + "/**\")\n" +
                            "        .build();")
                    .addAnnotation("Bean");
        }

        javaClassSource.addMethod()
                .setName("apiAuth")
                .setReturnType("GroupedOpenApi")
                .setPublic()
                .setBody("return GroupedOpenApi.builder()\n" +
                        "        .group(\"auth\")\n" +
                        "        .pathsToMatch(\"/api/auth/**\")\n" +
                        "        .build();")
                .addAnnotation("Bean");
    }

    private void addApiInfoMethod(SwaggerJavaClass entityClass) {
        StringBuilder infoBuilder = new StringBuilder("return new OpenAPI()\n")
                .append("        .info(new Info()\n")
                .append("            .title(\"").append(entityClass.getTitle()).append("\")\n")
                .append("            .description(\"").append(entityClass.getDescription()).append("\")\n");

        if (entityClass.getVersion() != null) {
            infoBuilder.append("            .version(\"").append(entityClass.getVersion()).append("\")\n");
        }
        if (entityClass.getLicense() != null) {
            infoBuilder.append("            .license(\"").append(entityClass.getLicense()).append("\")\n");
        }
        if (entityClass.getLicenseUrl() != null) {
            infoBuilder.append("            .licenseUrl(\"").append(entityClass.getLicenseUrl()).append("\")\n");
        }
        if (entityClass.getTermsOfServiceUrl() != null) {
            infoBuilder.append("            .termsOfService(\"").append(entityClass.getTermsOfServiceUrl()).append("\")\n");
        }
        infoBuilder.append("        );");

        javaClassSource.addMethod()
                .setName("apiInfo")
                .setReturnType("OpenAPI")
                .setPublic()
                .setBody(infoBuilder.toString())
                .addAnnotation("Bean");
    }

    public JavaClassSource getJavaClassSource() {
        return javaClassSource;
    }
}
