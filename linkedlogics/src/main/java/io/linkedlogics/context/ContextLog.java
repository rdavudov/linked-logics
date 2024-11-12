package io.linkedlogics.context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.model.LogicDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.SingleLogicDefinition;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ContextLog {
	private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private Context context;
	
	private String timestamp;
	private String handler;
	private String contextId;
	private String contextKey;
	private String parentId;
	private String process;
	private String logic;
	private String logicPosition;
	private String logicReturnAs;
	private Boolean logicReturnMap;
	private Map<String, Object> inputs = new HashMap<>();
	private Map<String, Object> outputs = new HashMap<>();
	private String message;
	private Integer errorCode;
	private String errorMessage;
	private ErrorType errorType;
	private String errorStackTrace;
	private String step;
	private String candidate;
	
	private ContextLog(Context context) {
		this.context = context;
	}
	
	public static ContextLog log(Context context) {
		return new ContextLog(context);
	}
	
	public ContextLog timestamp() {
		this.timestamp = formatter.format(context.getCreatedAt());
		return this;
	}
	
	public ContextLog handler(Object handler) {
		this.handler = handler.getClass().getSimpleName();
		return this;
	}
	
	public ContextLog context() {
		this.contextId = context.getId();
		this.contextKey = context.getKey();
		this.parentId = context.getParentId();
		return this;
	}
	
	public ContextLog process() {
		this.process = context.getProcessId() + "[" + context.getProcessVersion() + "]";
		return this;
	}
	
	public ContextLog logic() {
		this.logic = context.getLogicId() + "[" + context.getLogicVersion() + "]";
		this.logicPosition = context.getLogicPosition();
		this.logicReturnAs = context.getLogicReturnAs();
		return this;
	}
	
	public ContextLog logic(LogicDefinition logic) {
		this.logic = logic.getId() + "[" + logic.getVersion() + "]";
		this.logicPosition = context.getLogicPosition();
		this.logicReturnAs = logic.getReturnAs();
		this.logicReturnMap = logic.isReturnMap() ? Boolean.TRUE : null;
		return this;
	}
	
	public ContextLog logic(BaseLogicDefinition logic) {
		this.logicPosition = logic.getPosition();
		return this;
	}
	
	public ContextLog logic(SingleLogicDefinition logic) {
		this.logic = logic.getLogicId() + "[" + logic.getLogicVersion() + "]";
		this.logicPosition = context.getLogicPosition();
		return this;
	}
	
	public ContextLog inputs() {
		this.inputs = context.getInput();
		return this;
	}
	
	public ContextLog inputs(Map<String, Object> inputs) {
		this.inputs = inputs;
		return this;
	}
	
	public ContextLog outputs() {
		this.outputs = context.getOutput();
		return this;
	}
	
	public ContextLog outputs(Map<String, Object> outputs) {
		this.outputs = outputs;
		return this;
	}
	
	public ContextLog message(String message) {
		this.message = message;
		return this;
	}
	
	public ContextLog error(String message) {
		this.errorCode = context.getError().getCode();
		this.errorMessage = context.getError().getMessage();
		this.errorType = context.getError().getType();
		this.message = message;
		return this;
	}
	
	public ContextLog error(Throwable e) {
		this.errorCode = context.getError().getCode();
		this.errorMessage = context.getError().getMessage();
		this.errorType = context.getError().getType();
		this.message = e.getLocalizedMessage();
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		this.errorStackTrace = sw.toString();
		
		return this;
	}
	
	public ContextLog step(String step) {
		this.step = step;
		return this;
	}
	
	public ContextLog candidate(String candidate) {
		this.candidate = candidate;
		return this;
	}
	
	private String getLog() {
		StringBuilder log = new StringBuilder();
		if (timestamp != null) log.append(", timestamp={").append(timestamp).append("}");
		if (step != null) log.append(", step={").append(step).append("}");
		if (handler != null) log.append(", handler={").append(handler).append("}");
		if (candidate != null) log.append(", candidate={").append(candidate).append("}");
		if (message != null) log.append(", message={").append(message).append("}");
		if (contextId != null) log.append(", contextId={").append(contextId).append("}");
		if (contextKey != null) log.append(", contextKey={").append(contextKey).append("}");
		if (parentId != null) log.append(", parentId={").append(parentId).append("}");
		if (process != null) log.append(", process={").append(process).append("}");
		if (logic != null) log.append(", logic={").append(logic).append("}");
		if (logicPosition != null) log.append(", logicPosition={").append(logicPosition).append("}");
		if (logicReturnAs != null) log.append(", logicReturnAs={").append(logicReturnAs).append("}");
		if (logicReturnMap != null) log.append(", logicReturnMap={").append(logicReturnMap).append("}");
		if (inputs != null && !inputs.isEmpty()) log.append(", inputs={").append(inputs).append("}");
		if (outputs != null && !outputs.isEmpty()) log.append(", outputs={").append(outputs).append("}");
		if (errorCode != null) log.append(", errorCode={").append(errorCode).append("}");
		if (errorMessage != null) log.append(", errorMessage={").append(errorMessage).append("}");
		if (errorType != null) log.append(", errorType={").append(errorType).append("}");
		if (errorStackTrace != null) log.append(", errorStackTrace={").append(errorStackTrace).append("}");
		
		return log.toString().substring(2);
	}
	
	public void trace() {
		if (log.isTraceEnabled()) {
			log.trace(getLog());
		}
	}
	
	public void debug() {
		if (log.isDebugEnabled()) {
			log.debug(getLog());
		}
	}
	
	public void info() {
		if (log.isInfoEnabled()) {
			log.info(getLog());
		}
	}
	
	public void warn() {
		if (log.isWarnEnabled()) {
			log.warn(getLog());
		}
	}
	
	public void error() {
		if (log.isErrorEnabled()) {
			log.error(getLog());
		}
	}
}
