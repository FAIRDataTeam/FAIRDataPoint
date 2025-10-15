package org.fairdatapoint.util;

import org.fairdatapoint.entity.base.CustomGeneratedUUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.id.uuid.UuidGenerator;
import org.hibernate.id.uuid.UuidValueGenerator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.UUID;

public class CustomUuidGenerator extends UuidGenerator {

    public CustomUuidGenerator(
            CustomGeneratedUUID config,
            Member member,
            CustomIdGeneratorCreationContext creationContext
    ) {
        super(createUuidGeneratorFromCustomUuid(config), member, creationContext);
    }

    @Override
    public Object generate(
            SharedSessionContractImplementor session,
            Object owner,
            Object currentValue,
            EventType eventType
    ) {
        if (currentValue instanceof UUID) {
            return currentValue;
        }
        return super.generate(session, owner, currentValue, eventType);
    }

    @Override
    public boolean allowAssignedIdentifiers() {
        return true;
    }

    private static org.hibernate.annotations.UuidGenerator createUuidGeneratorFromCustomUuid(
            CustomGeneratedUUID annotation
    ) {
        return new org.hibernate.annotations.UuidGenerator() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return org.hibernate.annotations.UuidGenerator.class;
            }

            @Override
            public Style style() {
                return annotation.style();
            }

            @Override
            public Class<? extends UuidValueGenerator> algorithm() {
                return annotation.algorithm();
            }
        };
    }
}
