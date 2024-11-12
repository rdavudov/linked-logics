package io.linkedlogics.service.handler.logic;

import static io.linkedlogics.context.ContextLog.log;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScriptHandler extends LogicHandler {

	public ScriptHandler() {

	}
	
	public ScriptHandler(LogicHandler handler) {
		super(handler);
	}
	
	@Override
	public void handle(Context context, Object result) {
		context.setExecutedAt(OffsetDateTime.now());
		try {
			String id = String.format("%s_%d_%s", context.getProcessId(), context.getProcessVersion(), context.getLogicPosition());
			
			Map<String, Object> scriptParams = new HashMap<>(context.getParams());
			scriptParams.putAll(context.getInput());
			
			Object scriptResult = null;
			if (context.getLogicReturnAs() != null) {
				scriptResult = ServiceLocator.getInstance().getEvaluatorService().evaluate(context.getLogicScript(), scriptParams);
				context.getOutput().put(context.getLogicReturnAs(), scriptResult);
			} else if (context.isLogicReturnAsMap()) {
				scriptResult = ServiceLocator.getInstance().getEvaluatorService().evaluateScript(context.getLogicScript(), id, scriptParams);
				context.getOutput().putAll((Map) scriptResult);
			}
			
			context.setExecutedIn(Duration.between(context.getExecutedAt(), OffsetDateTime.now()).toMillis());
//			ContextFlow.script(context.getLogicPosition()).result(Boolean.TRUE).message(id).duration(context.getExecutedIn()).log(context);
			
			super.handle(context, scriptResult);	
		} catch (Exception e) {
			context.setExecutedIn(Duration.between(context.getExecutedAt(), OffsetDateTime.now()).toMillis());
//			ContextFlow.script(context.getLogicPosition()).result(Boolean.FALSE).duration(context.getExecutedIn()).log(context);
			super.handleError(context, e);
		}
	}
}
