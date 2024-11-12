package io.linkedlogics.jdbc.repository;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.transaction.TransactionStatus;

import io.linkedlogics.jdbc.entity.Message;

public class TopicRepository extends MessageRepository {
	private static final String TABLE = "ll_topic";
	
	private static final String INSERT = "INSERT INTO " + TABLE + " (queue, payload, created_at) VALUES(?, ?, ?)";
	private static final String UPDATE = "INSERT INTO " + TABLE + "_consumed (id, created_at, consumed_by, consumed_at) VALUES(?, ?, ?, ?)"; 
	private static final String SELECT = "SELECT id, queue, payload, created_at, consumed_by FROM " + TABLE + 
			" WHERE queue = ? AND id NOT IN (SELECT id FROM " + TABLE + "_consumed WHERE consumed_by = ?) LIMIT 1";
	
	private static final String DELETE = "DELETE FROM "+ TABLE + " WHERE created_at < ?";
	private static final String DELETE_CONSUMED = "DELETE FROM "+ TABLE + "_consumed WHERE created_at < ?";

	public TopicRepository(DataSource dataSource) {
		super(dataSource);
	}
	
	public void set(String queue, String payload) {
		int result = jdbcTemplate.update(INSERT, 
				new Object[]{queue, payload, OffsetDateTime.now()},
				new int[]{Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP});
		if (result == 0) {
			throw new RuntimeException();
		}
	}

	public Optional<String> get(String queue, String consumer) {
		Message message = null;
		TransactionStatus txStatus = transactionManager.getTransaction(getTransactionDefinition());
		try {
			List<Message> list = jdbcTemplate.query(SELECT, new Object[] {queue, consumer}, new int[] {Types.VARCHAR, Types.VARCHAR}, new MessageRowMapper());
			if (list.size() > 0) {
				message = list.get(0);
				jdbcTemplate.update(UPDATE, new Object[] {message.getId(), message.getCreatedAt(), consumer, OffsetDateTime.now()}, new int[] {Types.INTEGER, Types.TIMESTAMP, Types.VARCHAR, Types.TIMESTAMP});
			}
			transactionManager.commit(txStatus);
			return Optional.ofNullable(message == null ? null : message.getPayload());
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(txStatus);
		}
		return Optional.empty();
	}
	
	public Optional<Message> clear(OffsetDateTime expiredAt) {
		Message message = null;
		TransactionStatus txStatus = transactionManager.getTransaction(getTransactionDefinition());
		try {
			int result = jdbcTemplate.update(DELETE, new Object[] {expiredAt});
			jdbcTemplate.update(DELETE_CONSUMED, new Object[] {expiredAt});
			transactionManager.commit(txStatus);
			return Optional.ofNullable(message);
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(txStatus);
		}
		return Optional.empty();
	}
}
