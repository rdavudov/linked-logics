package io.linkedlogics.service;

import java.util.Optional;

public interface TopicService extends LinkedLogicsService {
	void offer(String queue, String payload);
	
	Optional<String> poll(String queue); 
}
