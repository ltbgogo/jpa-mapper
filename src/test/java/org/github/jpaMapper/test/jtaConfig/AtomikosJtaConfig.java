//package org.jpa.mapper.test.jtaConfig;
//
//import javax.transaction.TransactionManager;
//import javax.transaction.UserTransaction;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.orm.jpa.EntityManagerFactoryUtils;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.JpaVendorAdapter;
//import org.springframework.orm.jpa.vendor.Database;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.jta.JtaTransactionManager;
//
//import com.atomikos.icatch.jta.UserTransactionImp;
//import com.atomikos.icatch.jta.UserTransactionManager;
//
//@Configuration
//public class AtomikosJtaConfig {
//
//    @Bean
//    public UserTransaction userTransaction() throws Throwable {
//        UserTransactionImp userTransactionImp = new UserTransactionImp();
//        userTransactionImp.setTransactionTimeout(5);
//        return userTransactionImp;
//    }
//
//    @Bean(initMethod = "init", destroyMethod = "close")
//    public UserTransactionManager atomikosTransactionManager() throws Throwable {
//        UserTransactionManager userTransactionManager = new UserTransactionManager();
//        userTransactionManager.setForceShutdown(false);
//        AtomikosJtaPlatform.transactionManager = userTransactionManager;
//        return userTransactionManager;
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager() throws Throwable {
//        UserTransaction userTransaction = userTransaction();
//        AtomikosJtaPlatform.userTransaction = userTransaction;
//        UserTransactionManager atomikosTransactionManager = atomikosTransactionManager();
//        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction, atomikosTransactionManager);
//        return jtaTransactionManager;
//    }
//
//    @Bean
//    public JpaVendorAdapter jpaVendorAdapter() {
//        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
//        hibernateJpaVendorAdapter.setShowSql(true);
//        hibernateJpaVendorAdapter.setGenerateDdl(true);
//        hibernateJpaVendorAdapter.setDatabase(Database.H2);
//        return hibernateJpaVendorAdapter;
//    }
//}