package io.linkedlogics.jdbc.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import lombok.AllArgsConstructor;
import lombok.Data;

public class ProcessRepository {
	public static final String TABLE = "ll_process";

	private static final String INSERT = "INSERT INTO " + TABLE + " (id, version, created_at, updated_at, builder) "
			+ "VALUES(?, ?, ?, ?, ?)";

	private static final String UPDATE = "UPDATE " + TABLE + " SET builder = ?, updated_at = ? where id = ? and version = ?";
	private static final String SELECT = "SELECT builder FROM " + TABLE + " WHERE id = ? and version = ?";
	private static final String SELECT_ALL = "SELECT id, version, builder FROM " + TABLE;
	private static final String VERSION = "SELECT MAX(version) FROM " + TABLE + " WHERE id = ?";

	protected DataSourceTransactionManager transactionManager;
	protected JdbcTemplate jdbcTemplate;

	public ProcessRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.transactionManager = new DataSourceTransactionManager(dataSource);
	}

	public void createProcess(ProcessEntity process) throws Exception {
		int result = jdbcTemplate.update(INSERT, 
				new Object[]{process.getId(), process.getVersion(), OffsetDateTime.now(), OffsetDateTime.now(), process.getBuilder()},
				new int[]{Types.VARCHAR, Types.INTEGER, Types.TIMESTAMP, Types.TIMESTAMP, Types.LONGVARCHAR});
		if (result == 0) {
			throw new RuntimeException("optimistic lock failed");
		}
	}
	
	public void update(ProcessEntity process) throws Exception {
		int result = jdbcTemplate.update(UPDATE, 
				new Object[]{process.getBuilder(), OffsetDateTime.now(), process.getId(), process.getVersion()},
				new int[]{Types.LONGVARCHAR, Types.TIMESTAMP, Types.VARCHAR, Types.INTEGER});
		if (result == 0) {
			throw new RuntimeException("optimistic lock failed");
		}
	}
	
	public Optional<ProcessEntity> getProcess(String id, int version) throws Exception {
		try {
			ProcessEntity process = jdbcTemplate.queryForObject(SELECT, new RowMapper<ProcessEntity>() {
				@Override
				public ProcessEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
					String builder = rs.getString(1);
					
					return new ProcessEntity(id, version, builder);
				}
			}, new Object[] {id, version});
			return Optional.of(process);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}
	
	public List<ProcessEntity> getProcesses() throws Exception {
		try {
			Stream<ProcessEntity> stream = jdbcTemplate.queryForStream(SELECT_ALL, new RowMapper<ProcessEntity>() {
				@Override
				public ProcessEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
					String id = rs.getString(1);
					int version = rs.getInt(2);
					String builder = rs.getString(3);
					
					return new ProcessEntity(id, version, builder);
				}
			});
			
			return stream.collect(Collectors.toList());
		} catch (EmptyResultDataAccessException e) {
			return List.of();
		}
	}
	
	public Optional<Integer> getMaxVersion(String id) throws Exception {
		try {
			Integer maxVersion = jdbcTemplate.queryForObject(VERSION, Integer.class, new Object[] {id});
			return Optional.of(maxVersion);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}
	
	@AllArgsConstructor
	@Data
	public static class ProcessEntity {
		private String id;
		private int version;
		private String builder;
	}
}
