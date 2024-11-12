package io.linkedlogics.service;

import java.time.OffsetDateTime;

public interface MetricService extends LinkedLogicsService {
	
	void set(String key, long value, OffsetDateTime timestamp);
}
