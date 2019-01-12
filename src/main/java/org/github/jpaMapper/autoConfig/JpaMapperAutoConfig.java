package org.github.jpaMapper.autoConfig;

import org.github.jpaMapper.annotation.JpaMapperScan;
import org.github.jpaMapper.JpaMapperScanner;
import lombok.Setter;
import org.github.jpaMapper.jpaTemplate.NamedJpaTemplateHolder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

@Setter
@Configuration
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class JpaMapperAutoConfig implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private BeanFactory beanFactory;
    private ResourceLoader resourceLoader;
    // 在 Spring 初始化过程中如果发生错误，Spring 会再次执行此类，从而产生有误导性的“重复的 sql”错误，使用此变量阻止再次执行
    private static boolean isInited = false;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (isInited) { return; }
        isInited = true;
        JpaMapperScanner scanner = new JpaMapperScanner(registry, (String) AnnotationUtils.getDefaultValue(JpaMapperScan.class, "entityManagerFactoryRef"));
        scanner.setResourceLoader(resourceLoader);
        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
        scanner.scan(packages.stream().toArray(String[]::new));
        NamedJpaTemplateHolder.setBeanFactory(beanFactory);
    }
}
