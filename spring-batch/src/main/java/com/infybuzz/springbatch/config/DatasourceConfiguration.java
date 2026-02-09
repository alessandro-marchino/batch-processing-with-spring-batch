package com.infybuzz.springbatch.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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

	@Bean
	@Qualifier("university")
	@ConfigurationProperties("spring.datasource.university")
	DataSourceProperties universityDataSourceProperties() {
		return new DataSourceProperties();
	}
	@Bean
	@Qualifier("university")
    DataSource universityDataSource(@Qualifier("university") DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}

	@Bean
	@Qualifier("postgres")
	@ConfigurationProperties("spring.datasource.postgres")
	DataSourceProperties postgresDataSourceProperties() {
		return new DataSourceProperties();
	}
	@Bean
	@Qualifier("postgres")
    DataSource postgresDataSource(@Qualifier("postgres") DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}
}
