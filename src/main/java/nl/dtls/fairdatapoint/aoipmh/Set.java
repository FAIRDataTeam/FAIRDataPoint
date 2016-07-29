package nl.dtls.fairdatapoint.aoipmh;

public class Set {
    public static Set set (String spec) {
        return new Set(spec);
    }

    private final String spec;
    private String name;
    private Condition condition;

    public Set(String spec) {
        this.spec = spec;
    }

    public String getName() {
        return name;
    }

    public Set withName(String name) {
        this.name = name;
        return this;
    }

    public Condition getCondition() {
        return condition;
    }

    public boolean hasCondition() {
        return condition != null;
    }

    public Set withCondition(Condition condition) {
        this.condition = condition;
        return this;
    }

    public String getSpec() {
        return spec;
    }

    public nl.dtls.fairdatapoint.aoipmh.writables.Set toOAIPMH () {
        nl.dtls.fairdatapoint.aoipmh.writables.Set set = new  nl.dtls.fairdatapoint.aoipmh.writables.Set();
        set.withName(getName());
        set.withSpec(getSpec());
        return set;
    }
}
