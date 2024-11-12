package io.linkedlogics.service;

import io.linkedlogics.service.task.LinkedLogicsTask;

public interface ProcessorService extends LinkedLogicsService {
	void process(LinkedLogicsTask task);
}
