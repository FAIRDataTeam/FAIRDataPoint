package nl.dtls.fairdatapoint.service.index.common;

import nl.dtls.fairdatapoint.entity.exception.FeatureDisabledException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IndexFeatureAspect {

    @Value("${fdp-index.enabled:false}")
    private boolean fdpIndexEnabled;

    @Around("@annotation(nl.dtls.fairdatapoint.service.index.common.RequiredEnabledIndexFeature)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!fdpIndexEnabled) {
            throw new FeatureDisabledException("Index functionality is turn off");
        }
        return joinPoint.proceed();
    }


}
