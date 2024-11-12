package io.linkedlogics.jdbc.service;

import io.linkedlogics.service.ServiceConfigurer;
import io.linkedlogics.service.common.QueueSchedulerService;

public class JdbcServiceConfigurer extends ServiceConfigurer {
	public JdbcServiceConfigurer() {
		configure(new JdbcProcessService());
		configure(new JdbcContextService());
		configure(new JdbcQueueService());
		configure(new JdbcTopicService());
		configure(new QueueSchedulerService());
//		configure(new QueueCallbackService());
		configure(new JdbcTriggerService());
		configure(new JdbcLimitService());
	}
}
