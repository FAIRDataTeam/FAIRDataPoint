/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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

/**
 * Custom UUID generator that allows for assigned UUIDs.
 * If a UUID is already assigned to the entity, it will be used as is.
 * Otherwise, a new UUID will be generated using the specified strategy.
 * This is needed because the default UuidGenerator does not allow for assigned identifiers
 * that we want to support in some cases such as populating initial data fixtures.
 */
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
