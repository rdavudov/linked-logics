package io.linkedlogics.service.local;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.PublisherService;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalPublisherService implements PublisherService {

	@Override
	public void publish(Context context) {
		try {
			ServiceLocator.getInstance().getConsumerService().consume(ServiceLocator.getInstance().getMapperService().clone(context));
		} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
