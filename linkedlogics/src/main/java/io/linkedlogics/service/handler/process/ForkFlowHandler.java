package io.linkedlogics.service.handler.process;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;
import io.linkedlogics.service.task.StartTask;

public class ForkFlowHandler extends ProcessFlowHandler {
	public ForkFlowHandler() {

	}
	
	public ForkFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get().getFork() != null) {
			
			if (ifNotAlreadyForked(candidatePosition, context)) {
				Context forkContext = forkContext(context, candidatePosition);
				ServiceLocator.getInstance().getContextService().set(forkContext);
				
				if (candidate.get().getFork().getKey().isPresent()) {
					context.getJoinMap().put(candidate.get().getFork().getKey().get(), forkContext.getId());
				}
				trace(context, "forking with id " + forkContext.getId(), candidatePosition, Flow.RESET);
				
				ServiceLocator.getInstance().getProcessorService().process(new StartTask(forkContext));
				ContextFlow.fork(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("fork is successful with key " + candidate.get().getFork().getKey().orElse("NO_KEY")).log(context);
				return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
			}
			trace(context, "forked execution", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		} else {
			trace(context, "no fork", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
	
	private boolean ifNotAlreadyForked(String candidatePosition, Context context) {
		return !candidatePosition.equals(context.getStartPosition());
	}
	
	private Context forkContext(Context parent, String candidatePosition) {
		Context forked = new Context(UUID.randomUUID().toString(), parent.getKey(), parent.getProcessId(), parent.getProcessVersion(), cloneParams(parent.getParams()), parent.getOrigin());
		forked.setParentId(parent.getId());
		forked.setStartPosition(candidatePosition);
		forked.setEndPosition(adjacentLogicPosition(candidatePosition));
		forked.setLogicId(null);
		forked.setApplication(parent.getApplication());
		return forked;
	}
	
	private Map<String, Object> cloneParams(Map<String, Object> params) {
		try {
			ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
			return (Map<String, Object>) mapper.readValue(mapper.writeValueAsString(params), Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
