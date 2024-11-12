package io.linkedlogics.service.task;

import static io.linkedlogics.context.ContextLog.log;

import org.slf4j.MDC;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.context.Context;
import io.linkedlogics.service.ProcessorService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.handler.logic.ErrorHandler;
import io.linkedlogics.service.handler.logic.LogicHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public abstract class LinkedLogicsTask implements Runnable {
	protected Context context;
	protected LogicHandler handler;
	@Setter
	protected ProcessorService service;
	
	public LinkedLogicsTask(Context context, LogicHandler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		try {
			setMdc(context);
			ServiceLocator.getInstance().getAsyncService().setContextId(context.getId());
			log(context).handler(this).message("executing task " + this.getClass().getSimpleName()).debug();
			handle();
		} catch (Throwable e) {
			new ErrorHandler().handleError(context, e);
		} finally {
			ServiceLocator.getInstance().getAsyncService().unsetContextId();
			unsetMdc();
		}
	}
	
	protected abstract void handle() ;
	
	private void setMdc(Context context) {
		MDC.put("contextId", context.getParentId() != null ? context.getParentId() : context.getId());
		MDC.put("contextKey", context.getKey());
		MDC.put("application", LinkedLogics.getApplicationName());
		MDC.put("instance", LinkedLogics.getInstanceName());
	}
	
	private void unsetMdc() {
		MDC.remove("contextId");
		MDC.remove("contextKey");
		MDC.remove("application");
		MDC.remove("instance");
	}
}
