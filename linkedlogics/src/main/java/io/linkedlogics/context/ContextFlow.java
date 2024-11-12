package io.linkedlogics.context;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.swing.text.DateFormatter;

import io.linkedlogics.service.ServiceLocator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@NoArgsConstructor
public class ContextFlow {
	public enum Type {
		 START,
		 FINISH,
		 LOGIC,
		 PUBLISH,
		 SCRIPT,
		 GROUP,
		 BRANCH,
		 VERIFY,
		 DELAY,
		 RETRY,
		 LOG,
		 COMPENSATE,
		 ERROR,
		 FAIL,
		 EXIT,
		 FORK,
		 JOIN,
		 JUMP,
		 LOOP,
		 SAVEPOINT;
	}
	
	private OffsetDateTime executedAt;
	private Type type;
	private String position;
	private String name;
	private Boolean result;
	private String message;
	private Long duration;
	
	private ContextFlow(Type type, String position) {
		this.type = type;
		this.position = position;
		this.executedAt = OffsetDateTime.now();
	}
	
	public static ContextFlow start(String position) {
		return new ContextFlow(Type.START, position);
	}
	
	public static ContextFlow finish(String position) {
		return new ContextFlow(Type.FINISH, position);
	}
	
	public static ContextFlow logic(String position) {
		return new ContextFlow(Type.LOGIC, position);
	}
	
	public static ContextFlow publish(String position) {
		return new ContextFlow(Type.PUBLISH, position);
	}
	
	public static ContextFlow script(String position) {
		return new ContextFlow(Type.SCRIPT, position);
	}
	
	public static ContextFlow group(String position) {
		return new ContextFlow(Type.GROUP, position);
	}
	
	public static ContextFlow branch(String position) {
		return new ContextFlow(Type.BRANCH, position);
	}
	
	public static ContextFlow verify(String position) {
		return new ContextFlow(Type.VERIFY, position);
	}
	
	public static ContextFlow delay(String position) {
		return new ContextFlow(Type.DELAY, position);
	}
	
	public static ContextFlow retry(String position) {
		return new ContextFlow(Type.RETRY, position);
	}
	
	public static ContextFlow log(String position) {
		return new ContextFlow(Type.LOG, position);
	}
	
	public static ContextFlow compensate(String position) {
		return new ContextFlow(Type.COMPENSATE, position);
	}
	
	public static ContextFlow error(String position) {
		return new ContextFlow(Type.ERROR, position);
	}
	
	public static ContextFlow fail(String position) {
		return new ContextFlow(Type.FAIL, position);
	}
	
	public static ContextFlow exit(String position) {
		return new ContextFlow(Type.EXIT, position);
	}
	
	public static ContextFlow fork(String position) {
		return new ContextFlow(Type.FORK, position);
	}
	
	public static ContextFlow join(String position) {
		return new ContextFlow(Type.JOIN, position);
	}
	
	public static ContextFlow jump(String position) {
		return new ContextFlow(Type.JUMP, position);
	}
	
	public static ContextFlow loop(String position) {
		return new ContextFlow(Type.LOOP, position);
	}
	
	public static ContextFlow savepoint(String position) {
		return new ContextFlow(Type.SAVEPOINT, position);
	}
	
	public ContextFlow name(String name) {
		this.name = name;
		return this;
	}
	
	public ContextFlow result(Boolean result) {
		this.result = result;
		return this;
	}
	
	public ContextFlow message(String message) {
		this.message = message;
		return this;
	}
	
	public ContextFlow duration(Long duration) {
		this.duration = duration;
		return this;
	}

	public String getLog() {
		return String.format("FLOW %-12s %s%s, result={%b}, message={%s}", type.name(), position, name == null ? "" : "[" + name + "]", result == null ? "" : result, message == null ? "" : message);
	}
	
	public void log(Context context) {
		context.getExecList().add(this);
		ServiceLocator.getInstance().getLoggingService().info(context, toString());
	}
	
	public String toString() {
		return String.format("FLOW %-12s %s%s, result={%b}, message={%s}", type.name(), position, name == null ? "" : "[" + name + "]", result == null ? "" : result, message == null ? "" : message);
	}
}
