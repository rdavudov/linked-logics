package io.linkedlogics.service.local;

import java.util.Map;
import java.util.Optional;

import org.codehaus.groovy.control.CompilationFailedException;

import com.fasterxml.jackson.core.JsonProcessingException;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.linkedlogics.service.EvaluatorService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalEvaluatorServiceConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalEvaluatorService implements EvaluatorService {
	private LocalEvaluatorServiceConfig config = new ServiceConfiguration().getConfig(LocalEvaluatorServiceConfig.class);
	
	@Override
	public Object evaluate(String expression, Map<String, Object> params) {
		Binding binding = new Binding();
		params.entrySet().forEach(e -> {
			binding.setVariable(e.getKey(), e.getValue());
		});
		GroovyShell groovyShell = new GroovyShell(binding);
		try {
			return groovyShell.evaluate(expression);
		} finally {
			groovyShell.resetLoadedClasses();
		}
	}

	@Override
	public Optional<String> checkSyntax(String expression) {
		if (!config.getCheckSyntax(true)) {
			return Optional.empty();
		}
		GroovyShell groovyShell = new GroovyShell();
		try {
			groovyShell.parse(expression);
			return Optional.empty();
		} catch (CompilationFailedException e) {
			return Optional.of(e.getMessage());
		} finally {
			groovyShell.resetLoadedClasses();
		}
	}
	
	@Override
	public String toParseableJson(Object value) throws JsonProcessingException {
		return "new groovy.json.JsonSlurper().parseText('" + ServiceLocator.getInstance().getMapperService().getMapper().writeValueAsString(value) + "')";
	}
}
