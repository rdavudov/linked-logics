package io.linkedlogics.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.linkedlogics.jdbc.service.config.JdbcConnectionServiceConfig;
import io.linkedlogics.service.LinkedLogicsService;
import io.linkedlogics.service.config.ServiceConfiguration;

public class JdbcConnectionService implements LinkedLogicsService {
	private static HikariDataSource dataSource;
	private JdbcConnectionServiceConfig config = new ServiceConfiguration().getConfig(JdbcConnectionServiceConfig.class);

	public DataSource getDataSource() {
		if (dataSource != null) {
			return dataSource;
		} else {
			synchronized (JdbcConnectionService.class) {
				if (dataSource != null) {
					return dataSource;
				} else {
					dataSource = initDataSource();
					return dataSource;
				}
			}
		}
	}
	
	public Connection getConnection() throws SQLException {
		return getDataSource().getConnection();
	}

	protected HikariDataSource initDataSource() {
		HikariConfig poolConfig = new HikariConfig();

		poolConfig.setJdbcUrl(config.getUrl());
		poolConfig.setUsername(config.getUsername());
		poolConfig.setPassword(config.getPassword());

		config.getPoolMin().ifPresent(poolConfig::setMinimumIdle);
		config.getPoolMax().ifPresent(poolConfig::setMaximumPoolSize);

		poolConfig.addDataSourceProperty("cachePrepStmts" , "true");
		poolConfig.addDataSourceProperty("prepStmtCacheSize" , "250");
		poolConfig.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");
		poolConfig.setDriverClassName(config.getDriver());
		return new HikariDataSource(poolConfig);
	}

}
