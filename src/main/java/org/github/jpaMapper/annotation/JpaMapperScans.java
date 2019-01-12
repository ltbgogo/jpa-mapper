package org.github.jpaMapper.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(JpaMapperScannersRegistrar.class)
public @interface JpaMapperScans {

    JpaMapperScan[] value() default {};
}
