package io.linkedlogics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface MapperService extends LinkedLogicsService {
	public ObjectMapper getMapper() ;
	
	@SuppressWarnings("unchecked")
	default <O> O clone(O object) throws JsonProcessingException {
		return (O) getMapper().readValue(getMapper().writeValueAsString(object), object.getClass());
	}
}
