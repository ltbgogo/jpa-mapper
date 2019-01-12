//package org.jpa.mapper.test.user;
//
//import javax.persistence.EntityManager;
//import javax.sql.DataSource;
//
//import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import static org.jpa.mapper.test.user.UserConfig.ID;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(basePackageClasses = UserRepo.class, entityManagerFactoryRef = ID + "Emf", transactionManagerRef = ID + "Tm")
//public class UserConfig {
//
//    static final String ID = "user";
//
//    //配置数据源
//    @Primary
//    @Bean(ID + "Ds")
//    @ConfigurationProperties(prefix = "spring.datasource." + ID)
//    public DataSource ds() {
//        return DataSourceBuilder.create().build();
//    }
//
//    //配置实体管理器
//    @Primary
//    @Bean(ID + "Em")
//    public EntityManager em(EntityManagerFactoryBuilder builder) {
//        return emf(builder).getObject().createEntityManager();
//    }
//
//    //配置实体管理器工厂
//    @Primary
//    @Bean(ID + "Emf")
//    public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder) {
//        return builder.dataSource(ds()).packages(User.class).persistenceUnit(ID + "Pu").build();
//    }
//
//    //配置事务管理器
//    @Primary
//    @Bean(ID + "Tm")
//    public PlatformTransactionManager userTm(EntityManagerFactoryBuilder builder) {
//        return new JpaTransactionManager(emf(builder).getObject());
//    }
//}