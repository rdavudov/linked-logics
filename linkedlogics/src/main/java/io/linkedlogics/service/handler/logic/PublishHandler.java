package io.linkedlogics.service.handler.logic;

import static io.linkedlogics.context.ContextLog.log;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.model.process.ScriptLogicDefinition;
import io.linkedlogics.model.process.SingleLogicDefinition;
import io.linkedlogics.service.EvaluatorService;
import io.linkedlogics.service.SchedulerService;
import io.linkedlogics.service.SchedulerService.Schedule;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.TriggerService;
import io.linkedlogics.service.handler.process.HandlerResult;
import io.linkedlogics.service.local.LocalProcessorService;
import io.linkedlogics.service.local.config.LocalProcessorServiceConfig;
import io.linkedlogics.service.task.ScriptTask;
import io.linkedlogics.service.task.StartTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublishHandler extends LogicHandler {

	public PublishHandler() {

	}
	
	public PublishHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		HandlerResult handlerResult = (HandlerResult) result;
		if (handlerResult.isEndOfCandidates()) {
			log(context).handler(this).context().message("execution " + (context.getError() == null ? "finished" : "failed")).debug();
			finishContext(context);
		} else if (handlerResult.getSelectedLogic().isPresent()) {
			continueContext(context, handlerResult);
		} else {
			log(context).handler(this).context().message("execution paused").debug();
			pauseContext(context);
		}
		
		super.handle(context, result);
	}

	@Override
	public void handleError(Context context, Throwable error) {
		super.handleError(context, error);
	}
	
	private void continueContext(Context context, HandlerResult handlerResult) {
		if (handlerResult.getSelectedLogic().get() instanceof SingleLogicDefinition) {
			continueLogic(context, handlerResult);
		} else if (handlerResult.getSelectedLogic().get() instanceof ScriptLogicDefinition) {
			continueScript(context, handlerResult);
		} else {
			throw new RuntimeException("logic must implement SingleLogicDefinition or ScriptLogicDefinition");
		}
	}

	private void finishContext(Context context) {
		ContextFlow.finish("").result(context.getStatus() == Status.FINISHED ).log(context);
		ServiceLocator.getInstance().getContextService().set(context);
		if (context.isCallback()) {
			ServiceLocator.getInstance().getCallbackService().publish(context);
		}
		
		List<TriggerService.Trigger> triggers = ServiceLocator.getInstance().getTriggerService().get(context.getId());
		if (triggers != null && !triggers.isEmpty()) {
			triggers.stream().forEach(t -> {
				ServiceLocator.getInstance().getProcessorService().process(new StartTask(Context.fromTrigger(context, t)));
			});
		}
	}
	
	private void pauseContext(Context context) {
		timeoutContext(context);
		ServiceLocator.getInstance().getContextService().set(context);
	}
	
	private void timeoutContext(Context context) {	
		if (context.getExpiresAt() == null || context.getExpiresAt().isBefore(OffsetDateTime.now())) {
			int timeout = new ServiceConfiguration().getConfig(LocalProcessorServiceConfig.class).getTimeout();
			context.setExpiresAt(OffsetDateTime.now().plusSeconds(timeout));
		}
		Schedule schedule = new Schedule(context.getId(), context.getLogicId(), context.getLogicPosition(), context.getExpiresAt(), SchedulerService.ScheduleType.TIMEOUT); 
		ServiceLocator.getInstance().getSchedulerService().schedule(schedule);
		context.setExpiresAt(null);
	}
	
	private void continueScript(Context context, HandlerResult handlerResult) {
		ScriptLogicDefinition logic = (ScriptLogicDefinition) handlerResult.getSelectedLogic().get();
		
		context.setLogicScript(logic.getExpression().getExpression());
		context.setLogicPosition(logic.getPosition());
		context.setLogicReturnAs(logic.getReturnAs());
		context.setLogicReturnAsMap(logic.isReturnAsMap());
		context.setSubmittedAt(OffsetDateTime.now());
		context.setInput(getInputs(context, logic));
		context.setOutput(new HashMap<>());
		context.setStatus(Status.STARTED);
		context.setUpdatedAt(OffsetDateTime.now());
		
		String source = Optional.ofNullable(logic.getExpression().getSource()).map(s -> s).orElse("").replaceAll("\n", "");
		source = source.substring(0, Math.min(source.length(), 20));
		if (source.length() == 20) {
			source = source + "...";
		}
		
		ContextFlow.script(logic.getPosition()).name(logic.getName()).result(Boolean.TRUE).message(source.substring(0, Math.min(source.length(), 30))).log(context);
		log(context).handler(this).message("execution continues with script " + source  + "]").debug();
		
		ServiceLocator.getInstance().getContextService().set(context);
		ServiceLocator.getInstance().getProcessorService().process(new ScriptTask(context));
	}

	private void continueLogic(Context context, HandlerResult handlerResult) {
		SingleLogicDefinition logic = (SingleLogicDefinition) handlerResult.getSelectedLogic().get();
		
		context.setLogicId(logic.getLogicId());
		context.setLogicVersion(logic.getLogicVersion());
		context.setLogicPosition(logic.getPosition());
		context.setLogicReturnAs(logic.getReturnAs());
		context.setLogicScript(null);
		context.setSubmittedAt(OffsetDateTime.now());
		context.setApplication(logic.getApplication());
		context.setInput(getInputs(context, logic));
		context.setOutput(null);
		context.setStatus(Status.STARTED);
		context.setUpdatedAt(OffsetDateTime.now());
		
		timeoutContext(context);
		ContextFlow.logic(logic.getPosition()).name(logic.getName()).result(Boolean.TRUE).message(String.format("%s[%d]", logic.getLogicId(), logic.getLogicVersion())).log(context);
		log(context).handler(this).logic(logic).message("execution continues with logic " + logic.getLogicId() + "[" + logic.getLogicVersion() + "]").debug();
		
		ServiceLocator.getInstance().getContextService().set(context);
		
		boolean localBypass = new ServiceConfiguration().getConfig(LocalProcessorServiceConfig.class).getBypass(true);
		
		if (context.getApplication() == null || (localBypass && context.getApplication().equals(LinkedLogics.getApplicationName()))) {
			ServiceLocator.getInstance().getConsumerService().consume(Context.forPublish(context));
		} else {
			ServiceLocator.getInstance().getPublisherService().publish(Context.forPublish(context));
		}
	}

	private Map<String, Object> getInputs(Context context, BaseLogicDefinition logic) {
		final EvaluatorService evaluator = ServiceLocator.getInstance().getEvaluatorService();
		Map<String, Object> inputs = new HashMap<>();
		logic.getInputs().entrySet().stream().forEach(e -> {
			if (e.getValue() instanceof ExpressionLogicDefinition) {
				inputs.put(e.getKey(), evaluator.evaluate(((ExpressionLogicDefinition) e.getValue()).getExpression(), context.getParams()));
			} else {
				inputs.put(e.getKey(), e.getValue());
			}
		});
		return inputs;
	}
}
