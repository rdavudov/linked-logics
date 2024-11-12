 package io.linkedlogics.service.handler.logic;

import static io.linkedlogics.context.ContextLog.log;

import java.time.OffsetDateTime;
import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.handler.process.BranchFlowHandler;
import io.linkedlogics.service.handler.process.CleanupFlowHandler;
import io.linkedlogics.service.handler.process.CompensateFlowHandler;
import io.linkedlogics.service.handler.process.DelayFlowHandler;
import io.linkedlogics.service.handler.process.DisabledFlowHandler;
import io.linkedlogics.service.handler.process.EmptyCandidateFlowHandler;
import io.linkedlogics.service.handler.process.ErrorFlowHandler;
import io.linkedlogics.service.handler.process.ExitFlowHandler;
import io.linkedlogics.service.handler.process.FailFlowHandler;
import io.linkedlogics.service.handler.process.ForcedFlowHandler;
import io.linkedlogics.service.handler.process.ForkFlowHandler;
import io.linkedlogics.service.handler.process.GroupFlowHandler;
import io.linkedlogics.service.handler.process.HandlerResult;
import io.linkedlogics.service.handler.process.JoinFlowHandler;
import io.linkedlogics.service.handler.process.JumpFlowHandler;
import io.linkedlogics.service.handler.process.LogFlowHandler;
import io.linkedlogics.service.handler.process.LoopFlowHandler;
import io.linkedlogics.service.handler.process.OutputFlowHandler;
import io.linkedlogics.service.handler.process.ProcessFlowHandler;
import io.linkedlogics.service.handler.process.RetryFlowHandler;
import io.linkedlogics.service.handler.process.SavepointFlowHandler;
import io.linkedlogics.service.handler.process.SuccessFlowHandler;
import io.linkedlogics.service.handler.process.TimeoutFlowHandler;
import io.linkedlogics.service.handler.process.VerifyFlowHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessHandler extends LogicHandler {
	private ProcessFlowHandler waitingFlowHandler = new SuccessFlowHandler(new OutputFlowHandler());
	private ProcessFlowHandler lastFlowHandler = new SuccessFlowHandler(new OutputFlowHandler(new CleanupFlowHandler()));
	private ProcessFlowHandler nextFlowHandler = new EmptyCandidateFlowHandler(new ForcedFlowHandler(new DisabledFlowHandler(new DelayFlowHandler(new VerifyFlowHandler(new SavepointFlowHandler(new JumpFlowHandler(new ExitFlowHandler(new FailFlowHandler(new TimeoutFlowHandler(new JoinFlowHandler(new ForkFlowHandler(new LoopFlowHandler(new GroupFlowHandler(new BranchFlowHandler(new LogFlowHandler())))))))))))))));
	private ProcessFlowHandler errorFlowHandler = new RetryFlowHandler(new ErrorFlowHandler(new CompensateFlowHandler()));

	public ProcessHandler() {
		setHandlersOrder();
	}
	
	public ProcessHandler(LogicHandler handler) {
		super(handler);
		setHandlersOrder();
	}
	
	private void setHandlersOrder() {
		waitingFlowHandler.setOrder(1);
		lastFlowHandler.setOrder(1);
		nextFlowHandler.setOrder(1);
		errorFlowHandler.setOrder(1);
	}

	@Override
	public void handle(Context context, Object result) {
		Optional<Context> maybeContext = ServiceLocator.getInstance().getContextService().get(context.getId());
		
		if (maybeContext.isPresent()) {
			Context fullContext = maybeContext.get();
			transfer(context, fullContext);
			HandlerResult handlerResult = handle(fullContext);
			
			if (handlerResult.isEndOfCandidates()) {
				fullContext.setStatus(fullContext.getError() == null ? Status.FINISHED : Status.FAILED);
				fullContext.setFinishedAt(OffsetDateTime.now());
			}
			super.handle(fullContext, handlerResult);
		} else {
			super.handleError(context, new RuntimeException("context not found"));
		}
	}
	
	@Override
	public void handleError(Context context, Throwable error) {
		handle(context, null);
	}

	private HandlerResult handle(Context context) {
		ProcessDefinition process = findProcess(context.getProcessId(), context.getProcessVersion());
		
		HandlerResult result = handleLastLogic(context, process);
		while (result.getSelectedLogic().isEmpty()) {
			if (result.getNextCandidatePosition().get().equals(context.getEndPosition())) {
				return closeContext(context);
			}
			
			Optional<BaseLogicDefinition> nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
			
			if (nextLogic.isPresent() && context.getError() != null) {
				result = errorFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
				
				if (result.getNextCandidatePosition().isPresent()) {
					nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
				} else if (result.getSelectedLogic().isPresent()) {
					nextLogic = result.getSelectedLogic();
					result = HandlerResult.nextCandidate(nextLogic.get().getPosition());
				} else {
					return result;
				}
			}
			result = nextFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
			if (result.getNextCandidatePosition().isEmpty()) {
				return result;
			}
		}
		
		if (result.getSelectedLogic().isPresent()) {
			return result;
		}
		
		return HandlerResult.noCandidate();
	}
	
	private HandlerResult closeContext(Context context) {
		return HandlerResult.endOfCandidates();
	}
	
	private HandlerResult handleLastLogic(Context context, ProcessDefinition process) {
		HandlerResult result = null;
		
		if (context.getStatus() == Status.INITIAL) {
			log(context).handler(this).process().inputs(context.getParams()).message("starting context").debug();
			ContextFlow.start(context.getStartPosition()).name(String.format("%s[%d]", context.getProcessId(), context.getProcessVersion())).log(context);
			result = HandlerResult.nextCandidate(context.getStartPosition());
		} else if (context.getStatus() == Status.WAITING) {
			log(context).handler(this).message("resuming context for join").debug();
			result = HandlerResult.nextCandidate(context.getLogicPosition());
			Optional<BaseLogicDefinition> nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
			waitingFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
		} else if (context.getStatus() == Status.SCHEDULED) {
			log(context).handler(this).message("resuming context after delay").debug();
			result = HandlerResult.nextCandidate(context.getLogicPosition());
		} else {
			result = HandlerResult.nextCandidate(context.getLogicPosition());
			while (result.getNextCandidatePosition().isPresent()) {
				Optional<BaseLogicDefinition> nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
				result = lastFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
				if (result.getSelectedLogic().isPresent()) {
					break;
				} 
			}
			
			if (context.getError() != null) {
				result = HandlerResult.nextCandidate(context.getLogicPosition());
			} else {
				result = HandlerResult.nextCandidate(ProcessFlowHandler.adjacentLogicPosition(context.getLogicPosition()));
			}
		}

		return result;
	}
	
	public void transfer(Context logicContext, Context context) {
		context.setLogicId(logicContext.getLogicId());
		context.setLogicVersion(logicContext.getLogicVersion());
		context.setLogicPosition(logicContext.getLogicPosition());
		context.setOutput(logicContext.getOutput());
		context.setError(logicContext.getError() != null ? logicContext.getError() : context.getError());
		context.setExecutedAt(logicContext.getExecutedAt());
	}
}
