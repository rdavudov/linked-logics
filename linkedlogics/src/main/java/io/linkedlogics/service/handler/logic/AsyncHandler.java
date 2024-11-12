package io.linkedlogics.service.handler.logic;

import static io.linkedlogics.context.ContextLog.log;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.LogicDefinition;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncHandler extends LogicHandler {
	
	public AsyncHandler() {

	}
	
	public AsyncHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		LogicDefinition logic = findLogic(context.getLogicId(), context.getLogicVersion());
		
		if (!logic.isReturnAsync()) {
			super.handle(context, result);	
		} else {
			log(context).handler(this).logic(logic).message("logic is async waiting for callback").info();
		}
	}
}
