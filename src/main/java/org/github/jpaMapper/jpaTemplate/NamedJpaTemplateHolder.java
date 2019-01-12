package org.github.jpaMapper.jpaTemplate;

import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

public class NamedJpaTemplateHolder {

    @Setter
    private static BeanFactory beanFactory;
    private static Map<String, NamedJpaTemplate> jpaTemplateMap = new HashMap<>();

    @SneakyThrows
    public static NamedJpaTemplate get(String entityManagerFactoryRef) {
        NamedJpaTemplate jpaTemplate = jpaTemplateMap.get(entityManagerFactoryRef);
        if (jpaTemplate == null) {
            synchronized(jpaTemplateMap) {
                if (!jpaTemplateMap.containsKey(entityManagerFactoryRef)) {
                    // entityManager
                    EntityManagerFactory entityManagerFactory = beanFactory.getBean(entityManagerFactoryRef, EntityManagerFactory.class);
                    EntityManager entityManager = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
                    // transformInjector
                    PersistenceProvider persistenceProvider = PersistenceProvider.fromEntityManager(entityManager);
                    TransformerInjector transformerInjector = (TransformerInjector) Class.forName(TransformerInjector.class.getName() + persistenceProvider.name()).newInstance();
                    // new jpaTemplate
                    jpaTemplateMap.put(entityManagerFactoryRef, jpaTemplate = new NamedJpaTemplate(entityManager, transformerInjector));
                }
            }
        }
        return jpaTemplate;
    }
}
