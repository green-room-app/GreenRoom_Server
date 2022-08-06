//package com.greenroom.modulecommon.config;
//
//import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableJpaRepositories(
//        basePackages = "com.greenroom.modulecommon.repository",
//        entityManagerFactoryRef = "entityManager"
//)
//public class MysqlConfig {
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.hikari")
//    public DataSource apiDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.jpa")
//    public JpaProperties jpaProperties() {
//        return new JpaProperties();
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManager(EntityManagerFactoryBuilder builder,
//                                                                DataSource dataSource,
//                                                                JpaProperties jpaProperties) {
//        return builder
//                .dataSource(dataSource)
//                .properties(jpaProperties.getProperties())
//                .packages("com.greenroom.modulecommon.entity")
//                .persistenceUnit("default")
//                .build();
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory.getObject());
//        return transactionManager;
//    }
//}
