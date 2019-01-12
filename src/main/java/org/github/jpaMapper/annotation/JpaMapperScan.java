package org.github.jpaMapper.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(JpaMapperScannerRegistrar.class)
@Repeatable(JpaMapperScans.class)
public @interface JpaMapperScan {

    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};

    String entityManagerFactoryRef() default "entityManagerFactory";
}