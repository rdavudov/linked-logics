package io.linkedlogics.test.asserts;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.test.service.TestLogicService;

public class AssertCaptured {
	private String capturedAs;
	
	public AssertCaptured(String capturedAs) {
		this.capturedAs = capturedAs;
	}
	
	public void when(String expression) {
		TestLogicService logicService = (TestLogicService) ServiceLocator.getInstance().getLogicService();
		List<Map<String, Object>> caputredList = logicService.getCaptured(capturedAs).orElseThrow(() -> new IllegalArgumentException(capturedAs + " is not captured"));
		Optional<Map<String, Object>> matchedInput = caputredList.stream().filter(i -> (Boolean) ServiceLocator.getInstance().getEvaluatorService().evaluate(expression, i)).findAny();
		assert matchedInput.isPresent() : expression + " has failed with inputs \n " + caputredList.stream().map(m -> m.toString()).distinct().collect(Collectors.joining("\n"));
	}
}
