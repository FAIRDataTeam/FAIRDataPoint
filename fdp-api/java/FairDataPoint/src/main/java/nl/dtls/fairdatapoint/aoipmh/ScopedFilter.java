package nl.dtls.fairdatapoint.aoipmh;

import com.lyncode.xoai.dataprovider.filter.Scope;

/**
 *
 * @author Shamanou van Leeuwen
 * @Since 2016-07-02
 */
public final class ScopedFilter {
    private final Condition condition;
    private final Scope scope;

    public ScopedFilter(Condition condition, Scope scope) {
        this.condition = condition;
        this.scope = scope;
    }

    public Condition getCondition() {
        return condition;
    }

    public Scope getScope() {
        return scope;
    }
}
