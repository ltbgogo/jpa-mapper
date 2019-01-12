package org.github.jpaMapper;

import lombok.SneakyThrows;
import org.github.jpaMapper.metadata.SqlMetadataHolder;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Set;

public class JpaMapperScanner extends ClassPathBeanDefinitionScanner {

    private String entityManagerFactoryRef;

    public JpaMapperScanner(BeanDefinitionRegistry registry, String entityManagerFactoryRef) {
        super(registry, false);
        this.setBeanNameGenerator(new AnnotationBeanNameGenerator());
        this.addIncludeFilter(new AssignableTypeFilter(JpaMapper.class));
        this.entityManagerFactoryRef = entityManagerFactoryRef;
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : beanDefinitions) {
            this.processBeanDefinition(holder);
        }
        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface()
                && !beanDefinition.getMetadata().getClassName().equals(JpaMapper.class.getName())
                && beanDefinition.getMetadata().isIndependent();
    }

    @SneakyThrows
    private void processBeanDefinition(BeanDefinitionHolder holder) {
        GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
        Class mapperClass = Class.forName(definition.getBeanClassName());
        definition.getConstructorArgumentValues().addGenericArgumentValue(mapperClass);
        definition.getConstructorArgumentValues().addGenericArgumentValue(entityManagerFactoryRef);
        definition.setBeanClass(JpaMapperFactoryBean.class);
        // 注册SqlMetadata
        SqlMetadataHolder.of().registerSqlMetadata(mapperClass);
    }
}
