package io.linkedlogics.jdbc.repository;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.transaction.TransactionStatus;

import io.linkedlogics.jdbc.entity.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueRepository extends MessageRepository {
	private static final String TABLE = "ll_queue";
	
	private static final String INSERT = "INSERT INTO " + TABLE + " (queue, payload, created_at) VALUES(?, ?, ?)";
	private static final String UPDATE = "UPDATE " + TABLE + " SET consumed_by = ? WHERE id IN "
			+ "(SELECT id FROM " +  TABLE + " WHERE queue = ? AND consumed_by IS NULL ORDER BY created_at LIMIT 1)"; 
	private static final String SELECT = "SELECT id, queue, payload, created_at, consumed_by FROM " + TABLE + " WHERE queue = ? AND consumed_by = ? LIMIT 1";
	private static final String DELETE = "DELETE FROM " + TABLE + " WHERE id = ?"; 

	public QueueRepository(DataSource dataSource) {
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
			int result = jdbcTemplate.update(UPDATE, consumer, queue);
			if (result > 0) {
				List<Message> list = jdbcTemplate.query(SELECT, new Object[] {queue, consumer}, new int[] {Types.VARCHAR, Types.VARCHAR}, new MessageRowMapper());
				if (list.size() > 0) {
					message = list.get(0);
					jdbcTemplate.update(DELETE, new Object[] {message.getId()});
				}
			}
			transactionManager.commit(txStatus);
			return Optional.ofNullable(message == null ? null : message.getPayload());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			transactionManager.rollback(txStatus);
		}
		return Optional.empty();
	}
}
