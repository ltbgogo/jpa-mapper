//package org.jpa.mapper.test.book;
//
//import org.h2.jdbcx.JdbcDataSource;
//import org.jpa.mapper.test.jtaConfig.AtomikosJtaPlatform;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaVendorAdapter;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//
//import static org.jpa.mapper.test.book.BookJtaConfig.ID;
//
//@Configuration
//@DependsOn("transactionManager")
//@EnableJpaRepositories(basePackages = "org.jpa.mapper.test." + ID, entityManagerFactoryRef = ID + "EntityManager", transactionManagerRef = "transactionManager")
//public class BookJtaConfig {
//
//    static final String ID = "book";
//
//    @Bean(name = ID + "DataSource", initMethod = "init", destroyMethod = "close")
//    public AtomikosDataSourceBean dataSource() {
//        JdbcDataSource h2XaDataSource = new JdbcDataSource();
//        h2XaDataSource.setURL("jdbc:h2:mem:bookdb;DB_CLOSE_ON_EXIT=FALSE");
//
//        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
//        xaDataSource.setXaDataSource(h2XaDataSource);
//        xaDataSource.setUniqueResourceName(ID + "XaDataSource");
//        xaDataSource.setMaxPoolSize(5);
//        xaDataSource.setPoolSize(2);
//        return xaDataSource;
//    }
//
//    @Bean(ID + "EntityManager")
//    @Autowired
//    public LocalContainerEntityManagerFactoryBean entityManager(JpaVendorAdapter jpaVendorAdapter) {
//        HashMap<String, Object> properties = new HashMap<String, Object>();
//        properties.put("hibernate.transaction.jta.platform", AtomikosJtaPlatform.class.getName());
//        properties.put("javax.persistence.transactionType", "JTA");
//        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
//        entityManager.setJtaDataSource(dataSource());
//        entityManager.setJpaVendorAdapter(jpaVendorAdapter);
//        entityManager.setPackagesToScan("org.jpa.mapper.test." + ID);
//        entityManager.setPersistenceUnitName(ID + "PersistenceUnit");
//        entityManager.setJpaPropertyMap(properties);
//        return entityManager;
//    }
//}