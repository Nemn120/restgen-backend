package es.achavez.miw.tfm.restgen.generator.domain;

public enum Relation {

    MANY_TO_ONE("ManyToOne", "javax.persistence.ManyToOne"),
    ONE_TO_ONE("OneToOne", "javax.persistence.OneToOne"),
    ONE_TO_MANY("OneToMany", "javax.persistence.OneToMany");

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
