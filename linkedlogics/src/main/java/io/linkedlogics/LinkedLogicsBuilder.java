package io.linkedlogics;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Base64;

import io.linkedlogics.model.ProcessDefinition.ProcessBuilder;
import io.linkedlogics.model.process.BaseLogicDefinition.BaseLogicBuilder;
import io.linkedlogics.model.process.BranchLogicDefinition.BranchLogicBuilder;
import io.linkedlogics.model.process.ErrorLogicDefinition;
import io.linkedlogics.model.process.ErrorLogicDefinition.ErrorLogicBuilder;
import io.linkedlogics.model.process.ExitLogicDefinition.ExitLogicBuilder;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.model.process.FailLogicDefinition.FailLogicBuilder;
import io.linkedlogics.model.process.GroupLogicDefinition.GroupLogicBuilder;
import io.linkedlogics.model.process.JumpLogicDefinition.JumpLogicBuilder;
import io.linkedlogics.model.process.LogLogicDefinition.Level;
import io.linkedlogics.model.process.LogLogicDefinition.LogLogicBuilder;
import io.linkedlogics.model.process.LoopLogicDefinition.LoopLogicBuilder;
import io.linkedlogics.model.process.ProcessLogicDefinition.ProcessLogicBuilder;
import io.linkedlogics.model.process.RetryLogicDefinition;
import io.linkedlogics.model.process.SavepointLogicDefinition.SavepointLogicBuilder;
import io.linkedlogics.model.process.ScriptLogicDefinition.ScriptLogicBuilder;
import io.linkedlogics.model.process.SingleLogicDefinition.SingleLogicBuilder;
import io.linkedlogics.model.process.VerifyLogicDefinition.VerifyLogicBuilder;
import io.linkedlogics.service.LogicService;
import io.linkedlogics.service.ProcessService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LinkedLogicsBuilder {
	public static final Level TRACE = Level.TRACE;
	public static final Level DEBUG = Level.DEBUG;
	public static final Level INFO = Level.INFO;
	public static final Level WARN = Level.WARN;
	public static final Level ERROR = Level.ERROR;
	
	public static ProcessBuilder createProcess(String id, int version) {
		return new ProcessBuilder().id(id).version(version);
	}
	
	public static ProcessBuilder createProcess(String id) {
		return new ProcessBuilder().id(id).version(ProcessService.LATEST_VERSION);
	}
	
	public static SingleLogicBuilder logic(String id, int version) {
		return new SingleLogicBuilder().id(id).version(version);
	}
	
	public static SingleLogicBuilder logic(String id) {
		return new SingleLogicBuilder().id(id).version(LogicService.LATEST_VERSION);
	}
	
	public static GroupLogicBuilder group(BaseLogicBuilder<?, ?>... logics) {
		return new GroupLogicBuilder(logics);
	}
	
	public static LoopLogicBuilder loop(ExpressionLogicDefinition expression, BaseLogicBuilder<?, ?>... logics) {
		return new LoopLogicBuilder(expression, logics);
	}
	
	public static ProcessLogicBuilder process(String processId, int version) {
		return new ProcessLogicBuilder(processId, version);
	}
	
	public static BranchLogicBuilder branch(ExpressionLogicDefinition expression, BaseLogicBuilder<?, ?> leftBranch, BaseLogicBuilder<?, ?> rightBranch) {
		return new BranchLogicBuilder(expression, leftBranch, rightBranch);
	}
	
	public static BranchLogicBuilder branch(ExpressionLogicDefinition expression, BaseLogicBuilder<?, ?> leftBranch) {
		return new BranchLogicBuilder(expression, leftBranch, null);
	}
	
	public static VerifyLogicBuilder verify(ExpressionLogicDefinition expression) {
		return new VerifyLogicBuilder(expression);
	}
	
	public static SavepointLogicBuilder savepoint() {
		return new SavepointLogicBuilder();
	}
	
	public static ErrorLogicDefinition.ErrorLogicBuilder handle(ErrorLogicBuilder error) {
		return error;
	}
	
	public static ErrorLogicDefinition.ErrorLogicBuilder error() {
		return new ErrorLogicBuilder();
	}
	
	public static ErrorLogicDefinition.ErrorLogicBuilder error(BaseLogicBuilder<?, ?> errorLogic) {
		return new ErrorLogicBuilder().using(errorLogic);
	}
	
	public static RetryLogicDefinition.RetryLogicBuilder retry(int maxRetries, int delay) {
		return new RetryLogicDefinition.RetryLogicBuilder(maxRetries, delay);
	}
	
	public static ExpressionLogicDefinition expr(String expression) {
		return new ExpressionLogicDefinition.ExpressionLogicDefinitionBuilder(expression).source(expression).build();
	}
	
	public static ExpressionLogicDefinition when(String expression) {
		return expr(expression);
	}
	
	public static ExpressionLogicDefinition var(String expression) {
		return expr(expression);
	}
	
	public static ExpressionLogicDefinition file(String file) {
		try (InputStream in = LinkedLogics.class.getClassLoader().getResourceAsStream(file)) {
			return new ExpressionLogicDefinition.ExpressionLogicDefinitionBuilder(new String(in.readAllBytes())).source(file).build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Integer max(Integer i) {
		return i;
	}
	
	public static Integer seconds(Integer s) {
		return s;
	}
	
	public static JumpLogicBuilder jump(String label) {
		return new JumpLogicBuilder(label);
	}
	
	public static JumpLogicBuilder jump(ExpressionLogicDefinition expr) {
		return new JumpLogicBuilder(expr);
	}
	
	public static ExitLogicBuilder exit() {
		return new ExitLogicBuilder();
	}
	
	public static FailLogicBuilder fail() {
		return new FailLogicBuilder();
	}
	
	public static ScriptLogicBuilder script(String expression) {
		return new ScriptLogicBuilder(expr(expression));
	}
	
	public static ScriptLogicBuilder script(ExpressionLogicDefinition expression) {
		return new ScriptLogicBuilder(expression);
	}
	
	public static LogLogicBuilder log(String message) {
		return new LogLogicBuilder(message);
	}
	
	public static String encode(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes(Charset.forName("UTF-8")));
	}
	
	public static String decode(String s) {
		return new String(Base64.getDecoder().decode(s));
	}
}
