package es.achavez.miw.tfm.restgen.generator.domain;

public enum Relation {

    MANY_TO_ONE("ManyToOne", "jakarta.persistence.ManyToOne"),
    ONE_TO_ONE("OneToOne", "jakarta.persistence.OneToOne"),
    ONE_TO_MANY("OneToMany", "jakarta.persistence.OneToMany");

    private String name;
    private String packageImport;

    Relation(String oneToMany, String s) {
        this.name = oneToMany;
        this.packageImport = s;
    }

    public String getName() {
        return name;
    }

    public String getPackageImport() {
        return packageImport;
    }
}
