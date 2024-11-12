package io.linkedlogics.jdbc.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class LimitRepository {
	public static final String TABLE = "ll_limit";

	private static final String INSERT = "INSERT INTO " + TABLE + " (id, expires_at, counter) "
			+ "VALUES(?, ?, ?)";

	private static final String UPDATE = "UPDATE " + TABLE + " SET counter = counter + ? where id = ? and counter + ? <= ?";
	private static final String SELECT = "SELECT counter FROM " + TABLE + " WHERE id = ?";
	private static final String DELETE = "DELETE FROM " + TABLE + " WHERE key = ?";
	private static final String DELETE_EXPIRED = "DELETE FROM " + TABLE + " WHERE expires_at < ?";

	protected DataSourceTransactionManager transactionManager;
	protected JdbcTemplate jdbcTemplate;

	public LimitRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.transactionManager = new DataSourceTransactionManager(dataSource);
	}

	public long create(String key, OffsetDateTime expiresAt, Long limit, Long increment) {
		try {
			int result = jdbcTemplate.update(INSERT, 
					new Object[]{key, expiresAt, increment},
					new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.INTEGER});
			return result;
		} catch (DataAccessException e) {
			return update(key, expiresAt, limit, increment);
		}
	}

	public long update(String key, OffsetDateTime expiresAt, Long limit, Long increment) {
		try {
			int result = jdbcTemplate.update(UPDATE, 
					new Object[]{increment, key, increment, limit},
					new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.INTEGER});
			return result;
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Optional<Long> get(String key) {
		try {
			Long counter = jdbcTemplate.queryForObject(SELECT, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getLong(1);
				}
			}, new Object[] {key});
			
			return Optional.of(counter);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	public void delete(String key) {
		jdbcTemplate.update(DELETE, new Object[] {key}, new int[]{Types.VARCHAR});
	}
	
	public void deleteExpired(OffsetDateTime timestamp) {
		jdbcTemplate.update(DELETE_EXPIRED, new Object[] {timestamp}, new int[]{Types.TIMESTAMP});
	}
}
