package com.infybuzz.springbatch.config;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class DatasourceConfiguration {

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource")
	DataSourceProperties defaultDataSourceProperties() {
		return new DataSourceProperties();
	}
	@Bean
	@Primary
    DataSource defaultDataSource(DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}

	@Bean(defaultCandidate = false)
	@Qualifier("university")
	@ConfigurationProperties("spring.datasource.university")
	DataSourceProperties universityDataSourceProperties() {
		return new DataSourceProperties();
	}
	@Bean(defaultCandidate = false)
	@Qualifier("university")
    DataSource universityDataSource(@Qualifier("university") DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}

	@Bean(defaultCandidate = false)
	@Qualifier("postgres")
	@ConfigurationProperties("spring.datasource.postgres")
	DataSourceProperties postgresDataSourceProperties() {
		return new DataSourceProperties();
	}
	@Bean(defaultCandidate = false)
	@Qualifier("postgres")
    DataSource postgresDataSource(@Qualifier("postgres") DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}

	@Bean(defaultCandidate = false)
	@Qualifier("postgres")
	EntityManagerFactory postgresEntityManagerFactory(@Qualifier("postgres") DataSource datasource) {
		LocalContainerEntityManagerFactoryBean lem = new LocalContainerEntityManagerFactoryBean();
		lem.setDataSource(datasource);
		lem.setPackagesToScan("com.infybuzz.springbatch.entity.postgresql");
		lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		lem.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		lem.afterPropertiesSet();

		return lem.getObject();
	}

	@Bean(defaultCandidate = false)
	@Qualifier("university")
	EntityManagerFactory universityEntityManagerFactory(@Qualifier("university") DataSource datasource) {
		LocalContainerEntityManagerFactoryBean lem = new LocalContainerEntityManagerFactoryBean();
		lem.setDataSource(datasource);
		lem.setPackagesToScan("com.infybuzz.springbatch.entity.mysql");
		lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		lem.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		lem.afterPropertiesSet();

		return lem.getObject();
	}

	@Bean
	@Primary
	JpaTransactionManager jpaTransactionManager(@Qualifier("university") EntityManagerFactory entityManagerFactory, @Qualifier("university") DataSource dataSource) {
		JpaTransactionManager jtm = new JpaTransactionManager();
		jtm.setDataSource(dataSource);
		jtm.setEntityManagerFactory(entityManagerFactory);
		return jtm;
	}
}
