package org.fairdatapoint.entity.base;

import org.fairdatapoint.util.CustomUuidGenerator;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.ValueGenerationType;
import org.hibernate.id.uuid.UuidValueGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IdGeneratorType(CustomUuidGenerator.class)
@ValueGenerationType(generatedBy = CustomUuidGenerator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomGeneratedUUID {

    UuidGenerator.Style style() default UuidGenerator.Style.AUTO;

    Class<? extends UuidValueGenerator> algorithm() default UuidValueGenerator.class;

}
