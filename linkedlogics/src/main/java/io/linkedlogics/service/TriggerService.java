package io.linkedlogics.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public interface TriggerService extends LinkedLogicsService {
	void set(String contextId, Trigger trigger);
	
	List<Trigger> get(String contextId);
	
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Trigger {
		private String contextId;
		private String position;
	}
}
