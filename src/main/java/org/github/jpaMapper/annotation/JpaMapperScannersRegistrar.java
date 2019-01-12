package org.github.jpaMapper.annotation;

import lombok.Setter;
import org.github.jpaMapper.JpaMapperScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

@Setter
public class JpaMapperScannersRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

  private ResourceLoader resourceLoader;

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes mapperScansAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(JpaMapperScans.class.getName()));
    for (AnnotationAttributes mapperScanAttr : mapperScansAttrs.getAnnotationArray("value")) {
      JpaMapperScanner scanner = new JpaMapperScanner(registry, mapperScanAttr.getString("entityManagerFactoryRef"));
      scanner.setResourceLoader(resourceLoader);
      scanner.doScan(mapperScanAttr.getStringArray("basePackages"));
    }
  }
}