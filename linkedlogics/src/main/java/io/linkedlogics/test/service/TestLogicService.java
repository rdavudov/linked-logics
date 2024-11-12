package io.linkedlogics.test.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.linkedlogics.annotation.Input;
import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.exception.LogicException;
import io.linkedlogics.model.LogicDefinition;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalLogicService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class TestLogicService extends LocalLogicService {
	private Map<String, List<MockedLogicDefinition>> mockedDefinitions = new HashMap<>();
	private Map<String, List<Map<String, Object>>> capturedInputs = new HashMap<>(); 
	
	@Override
	public Optional<LogicDefinition> getLogic(String logicId, int version) {
		if (this.mockedDefinitions.containsKey(getLogicKey(logicId, version))) {
			try {
				return Optional.of(new LogicDefinition(logicId, version, null, false, true, null, MockedLogic.class.getDeclaredMethod("returnObject", Map.class), new MockedLogic(logicId, version)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return super.getLogic(logicId, version);
		}
	}

	public Optional<List<Map<String, Object>>> getCaptured(String capturedAs) {
		return Optional.ofNullable(capturedInputs.get(capturedAs));
	}
	
	public MockedLogicDefinition mock(String id, int version) {
		return new MockedLogicDefinition(id, version);
	}
	
	@AllArgsConstructor
	public class MockedLogic {
		private String id;
		private int version;
		
		public Object returnObject(@Input Map<String, Object> input) throws Throwable {
			MockedLogicDefinition definition = mockedDefinitions.getOrDefault(getLogicKey(id, version), List.of()).stream()
				.filter(m -> m.getWhen() == null || (Boolean) ServiceLocator.getInstance().getEvaluatorService().evaluate(m.getWhen().getExpression(), input))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("no mock has matched"));
			
			if (definition.getCaptureAs() != null) {
				List<Map<String, Object>> capturedList = capturedInputs.getOrDefault(definition.getCaptureAs(), new ArrayList<>());
				capturedList.add(input);
				capturedInputs.put(definition.getCaptureAs(), capturedList);
			}
			
			if (definition.getError() != null) {
				throw definition.getError();
			}
			
			if (definition.getReturnAs() != null) {
				return Map.of(definition.getReturnAs(), definition.getResult());
			}
			
			return definition.getResult();
		}
	}
	
	@Getter
	public class MockedLogicDefinition {
		private String id;
		private int version; 
		private Object result;
		private Throwable error;
		private ExpressionLogicDefinition when;
		private String returnAs;
		private String captureAs;
		
		public MockedLogicDefinition(String id, int version) {
			this.id = id;
			this.version = version;
		}
		
		public MockedLogicDefinition when(String expression) {
			when = new ExpressionLogicDefinition(expression);
			return this;
		}
		
		public MockedLogicDefinition returnAs(String returnAs) {
			this.returnAs = returnAs;
			return this;
		}
		
		public MockedLogicDefinition captureAs(String captureAs) {
			this.captureAs = captureAs;
			return this;
		}
		
		public void thenReturn(Object result) {
			this.result = result;
			mock();
		}
		
		public void thenThrowError(Throwable error) {
			this.error = error;
			mock();
		}
		
		public void thenThrowError(int code, String message) {
			this.error = new LogicException(code, message, ErrorType.TEMPORARY);
			mock();
		}
		
		public void thenThrowError(int code, String message, ErrorType errorType) {
			this.error = new LogicException(code, message, errorType);
			mock();
		}
		
		private void mock() {
			List<MockedLogicDefinition> mockedList = mockedDefinitions.getOrDefault(getLogicKey(id, version), new ArrayList<>());
			mockedList.add(this);
			mockedDefinitions.put(getLogicKey(id, version), mockedList);
		}
	}
}
