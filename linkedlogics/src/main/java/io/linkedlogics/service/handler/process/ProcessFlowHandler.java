package io.linkedlogics.service.handler.process;

import static io.linkedlogics.context.ContextLog.log;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.helper.LogicPositioner;
import io.linkedlogics.service.handler.logic.ProcessHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ProcessFlowHandler {
	protected enum Flow {
		CONTINUE("->"),
		RESET("<<"),
		TERMINATE("=#");
		
		String symbol;
		
		private Flow(String symbol) {
			this.symbol = symbol;
		}
		
		public String getSymbol() {
			return this.symbol;
		}
	}
	
	protected Optional<ProcessFlowHandler> nextHandler;
	protected Integer order;
	
	public ProcessFlowHandler() {
		this.nextHandler = Optional.empty();
	}
	
	public ProcessFlowHandler(ProcessFlowHandler nextHandler) {
		this.nextHandler = Optional.of(nextHandler);
	}
	
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		return nextHandler.map(h -> h.handle(candidate, candidatePosition, context)).orElse(HandlerResult.selectCandidate(candidate));
	}
	
	public static String adjacentLogicPosition(String position) {
		if (position.endsWith(LogicPositioner.COMPENSATE)) {
			return position;
		}
		
		String[] positions = position.split("\\.");
		positions[positions.length - 1] = String.valueOf(Integer.parseInt(clearLogicPosition(positions[positions.length - 1])) + 1);
		return Arrays.stream(positions).collect(Collectors.joining("."));
	}
	
	public static String parentLogicPosition(String position) {
		String[] positions = position.split("\\.");
		return Arrays.stream(positions).limit(positions.length - 1).collect(Collectors.joining("."));
	}
	
	public static String clearLogicPosition(String position) {
		return position.replaceAll("[^0-9]", "");
	}
	
	public static boolean compareLogicPosition(String p1, String p2) {
		String[] positions1 = p1.split("\\.");
		String[] positions2 = p2.split("\\.");
		
		int index = 0;
		while (index < positions1.length && index < positions2.length) {
			int position1 = index < positions1.length ? Integer.parseInt(positions1[index]) : 0;
			int position2 = index < positions2.length ? Integer.parseInt(positions2[index]) : 0;
			
			if (position1 < position2) {
				return true;
			} else if (position1 > position2) {
				return false;
			} else {
				index++;
			}
		}
		
		return true;
	}
	
	protected void trace(Context context, String message, String candidate, Flow flow) {
		log(context).handler(this)
			.step(String.format("%2d.%s", getOrder(), flow.getSymbol()))
			.candidate(candidate)
			.message(message).trace();
	}
	
	public void setOrder(int order) {
		this.order = order;
		nextHandler.ifPresent(h -> h.setOrder(order + 1));
	}
	
	public int getOrder() {
		return this.order;
	}
}