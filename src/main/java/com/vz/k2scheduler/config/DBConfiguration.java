package com.vz.k2scheduler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages="com.vz.k2scheduler.repository")
public class DBConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public DataSource getH2Datasource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(env.getProperty("datasource.h2.driverClass"));
        dataSourceBuilder.url(env.getProperty("datasource.h2.url"));
        dataSourceBuilder.username(env.getProperty("datasource.h2.username"));
        dataSourceBuilder.password(env.getProperty("datasource.h2.password"));
        return dataSourceBuilder.build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.H2);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(getH2Datasource());
        em.setPackagesToScan("com.vz.k2scheduler.model");
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("jpa.h2.hibernate.ddl-auto"));
        properties.setProperty("hibernate.dialect", env.getProperty("jpa.h2.database-platform"));
        properties.setProperty("hibernate.show_sql", env.getProperty("jpa.h2.show-sql"));
        properties.setProperty("hibernate.format_sql", env.getProperty("jpa.h2.format-sql"));
        return properties;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }



}
