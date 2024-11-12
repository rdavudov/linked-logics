package io.linkedlogics.service;

import java.util.Optional;

import io.linkedlogics.model.ProcessDefinition;

public interface ProcessService extends LinkedLogicsService {
	public static final int LATEST_VERSION = -1;
	
	Optional<ProcessDefinition> getProcess(String processId);
	
	Optional<ProcessDefinition> getProcess(String processId, int version);
	
	void addProcess(ProcessDefinition process);
}
