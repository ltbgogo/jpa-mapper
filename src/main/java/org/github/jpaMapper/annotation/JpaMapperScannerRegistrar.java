package org.github.jpaMapper.annotation;

import org.github.jpaMapper.JpaMapperScanner;
import lombok.Setter;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

@Setter
public class JpaMapperScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

  private ResourceLoader resourceLoader;

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes mapperScanAttr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(JpaMapperScan.class.getName()));
    JpaMapperScanner scanner = new JpaMapperScanner(registry, mapperScanAttr.getString("entityManagerFactoryRef"));
    scanner.setResourceLoader(resourceLoader);
    scanner.doScan(mapperScanAttr.getStringArray("basePackages"));
  }
}
