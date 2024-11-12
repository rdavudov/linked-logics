package io.linkedlogics.service.handler.logic;

import static io.linkedlogics.context.ContextLog.log;

import java.util.Map;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.LogicDefinition;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OutputHandler extends LogicHandler {
	
	public OutputHandler() {

	}
	
	public OutputHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		LogicDefinition logic = findLogic(context.getLogicId(), context.getLogicVersion());
		
		if (logic.getMethod().getReturnType() != Void.class && result != null) {
			String returnAs = context.getLogicReturnAs() != null ? context.getLogicReturnAs() : logic.getReturnAs();
			
			if (result instanceof Map && logic.isReturnMap()) {
				context.getOutput().putAll((Map) result);
			} else if (returnAs != null) {
				context.getOutput().put(returnAs, result);
			}	
		}
		
		super.handle(context, null);
	}
}
