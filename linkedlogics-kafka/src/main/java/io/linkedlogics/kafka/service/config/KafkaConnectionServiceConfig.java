package io.linkedlogics.kafka.service.config;

import java.util.Optional;

import org.apache.kafka.clients.admin.KafkaAdminClient;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("kafka")
public interface KafkaConnectionServiceConfig extends ServiceConfig {

	@Config(key = "bootstrap-servers", description = "Kafka bootstrap server list", required = true)
	public Object getBootstrapServers();
	
	@Config(key = "producer.ack", description = "Kafka ACK config")
	public String getAckConfig(String defaultValue);
	
	@Config(key = "consumer.max-poll-count", description = "Kafka Max poll count")
	public Optional<Integer> getMaxPollCount();
	
	@Config(key = "topic.partitions", description = "Kafka partition count")
	public Integer getPartitions(int defaultValue);
	
	@Config(key = "topic.replication", description = "Kafka replication factor")
	public Integer getReplication(int defaultValue);
	
	@Config(key = "topic.retention", description = "Kafka retention hours")
	public Integer getRetention(int retention);
}
