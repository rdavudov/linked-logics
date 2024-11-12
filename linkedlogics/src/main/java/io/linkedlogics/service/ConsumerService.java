package io.linkedlogics.service;

import io.linkedlogics.context.Context;

public interface ConsumerService extends LinkedLogicsService {
	void consume(Context context);
	
	default int order() {
		return Integer.MAX_VALUE;
	}
}
