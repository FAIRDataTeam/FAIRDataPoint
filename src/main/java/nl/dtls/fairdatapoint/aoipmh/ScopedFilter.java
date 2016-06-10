package nl.dtls.fairdatapoint.aoipmh;

import com.lyncode.xoai.dataprovider.filter.Scope;

/**
 *
 * @author Shamanou van Leeuwen
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
