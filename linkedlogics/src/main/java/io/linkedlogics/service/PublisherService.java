package io.linkedlogics.service;

import io.linkedlogics.context.Context;

public interface PublisherService extends LinkedLogicsService {
	
	void publish(Context context);
}
