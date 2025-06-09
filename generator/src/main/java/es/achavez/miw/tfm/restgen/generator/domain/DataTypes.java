package es.achavez.miw.tfm.restgen.generator.domain;

public enum DataTypes {

    INTEGER("Integer", null),
    DOUBLE("Double", null),
    LONG("Long", null),
    STRING("String", null),
    DATE("Date", "java.util.Date"),
    LOCAL_DATE("LocalDate","java.time.LocalDate"),
    LOCAL_DATE_TIME("LocalDateTime", "java.time.LocalDateTime"),
    BIG_DECIMAL("BigDecimal","java.math.BigDecimal"),
    BOOLEAN("Boolean",null),
    LOGGER("Logger", "java.util.logging.Logger");



    private final String name;
    private final String importPath;

    private DataTypes(String name, String importPath) {
        this.name = name;
        this.importPath = importPath;
    }

    public String getName() {
        return name;
    }

    public String getImportPath() {
        return importPath;
    }
}
