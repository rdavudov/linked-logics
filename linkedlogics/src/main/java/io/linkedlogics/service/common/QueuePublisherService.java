package io.linkedlogics.service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.PublisherService;
import io.linkedlogics.service.QueueService;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueuePublisherService implements PublisherService {

	@Override
	public void publish(Context context) {
		QueueService queueService = ServiceLocator.getInstance().getQueueService();
		ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
		try {
			queueService.offer(context.getApplication(), mapper.writeValueAsString(context));
		} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
