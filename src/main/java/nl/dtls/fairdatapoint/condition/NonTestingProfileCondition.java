package nl.dtls.fairdatapoint.condition;

import nl.dtls.fairdatapoint.Profiles;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class NonTestingProfileCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return !conditionContext.getEnvironment().acceptsProfiles(Profiles.TESTING);
    }
}
