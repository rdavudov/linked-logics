package io.linkedlogics.service;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface EvaluatorService extends LinkedLogicsService {
	Object evaluate(String expression, Map<String, Object> params);
	
	default Object evaluateScript(String expression, String id, Map<String, Object> params) {
		return evaluate(expression, params);
	}
	
	default Optional<String> checkSyntax(String expression) {
		return Optional.empty();
	}
	
	default String toParseableJson(Object value) throws JsonProcessingException {
		return "(" + ServiceLocator.getInstance().getMapperService().getMapper().writeValueAsString(value) + ")";
	}
}
