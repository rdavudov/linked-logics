package io.linkedlogics.jdbc.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.ServiceLocator;

public class ContextRepository {
	public static final String TABLE = "ll_context";

	private static final String INSERT = "INSERT INTO " + TABLE + " (id, id_key, parent_id, status, version, process_id, "
			+ "process_version, created_at, updated_at, finished_at, expires_at, data) "
			+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE = "UPDATE " + TABLE + " SET status = ?, version = ?, updated_at = ?, finished_at = ?, expires_at = ?, data = ? where id = ? and version = ?";
	private static final String SELECT = "SELECT data, version FROM " + TABLE + " WHERE id = ?";
	private static final String DELETE = "DELETE FROM " + TABLE + " WHERE id = ? and version = ?";

	protected DataSourceTransactionManager transactionManager;
	protected JdbcTemplate jdbcTemplate;

	public ContextRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.transactionManager = new DataSourceTransactionManager(dataSource);
	}

	public void create(Context context) throws Exception {
		ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
		int result = jdbcTemplate.update(INSERT, 
				new Object[]{context.getId(), context.getKey(), context.getParentId(), context.getStatus().name(), context.getVersion(), context.getProcessId(),
						context.getProcessVersion(), context.getCreatedAt(), context.getUpdatedAt(), context.getFinishedAt(), context.getExpiresAt(),
						mapper.writeValueAsString(context)},
				new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR,
						Types.INTEGER, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,
						Types.LONGVARCHAR});
		if (result == 0) {
			throw new RuntimeException("optimistic lock failed");
		}
	}

	public void update(Context context) throws Exception {
		ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
		int result = jdbcTemplate.update(UPDATE, 
				new Object[]{context.getStatus().name(), context.getVersion() + 1, context.getUpdatedAt(), context.getFinishedAt(), context.getExpiresAt(),
						mapper.writeValueAsString(context), context.getId(), context.getVersion()},
				new int[]{Types.VARCHAR, Types.INTEGER, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,
						Types.LONGVARCHAR, Types.VARCHAR, Types.INTEGER,});
		if (result == 0) {
			throw new RuntimeException("optimistic lock failed");
		}
	}

	public Optional<Context> get(String id) throws Exception {
		try {
			Context context = jdbcTemplate.queryForObject(SELECT, new RowMapper<Context>() {
				@Override
				public Context mapRow(ResultSet rs, int rowNum) throws SQLException {
					String data = rs.getString(1);
					int version = rs.getInt(2);
					
					ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
					try {
						Context context = mapper.readValue(data, Context.class);
						context.setVersion(version);
						return context;
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}, new Object[] {id});
			
			return Optional.of(context);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	public void delete(String id, int version) throws Exception {
		jdbcTemplate.update(DELETE, new Object[] {id, version}, new int[]{Types.VARCHAR, Types.INTEGER});
	}
}
