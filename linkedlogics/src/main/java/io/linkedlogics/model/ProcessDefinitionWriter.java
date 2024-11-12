package io.linkedlogics.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.linkedlogics.LinkedLogicsBuilder;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.BranchLogicDefinition;
import io.linkedlogics.model.process.DelayLogicDefinition;
import io.linkedlogics.model.process.ErrorLogicDefinition;
import io.linkedlogics.model.process.ExitLogicDefinition;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.model.process.FailLogicDefinition;
import io.linkedlogics.model.process.ForkLogicDefinition;
import io.linkedlogics.model.process.GroupLogicDefinition;
import io.linkedlogics.model.process.JoinLogicDefinition;
import io.linkedlogics.model.process.JumpLogicDefinition;
import io.linkedlogics.model.process.LabelLogicDefinition;
import io.linkedlogics.model.process.LogLogicDefinition;
import io.linkedlogics.model.process.LoopLogicDefinition;
import io.linkedlogics.model.process.ProcessLogicDefinition;
import io.linkedlogics.model.process.RetryLogicDefinition;
import io.linkedlogics.model.process.SavepointLogicDefinition;
import io.linkedlogics.model.process.ScriptLogicDefinition;
import io.linkedlogics.model.process.SingleLogicDefinition;
import io.linkedlogics.model.process.TimeoutLogicDefinition;
import io.linkedlogics.model.process.VerifyLogicDefinition;
import io.linkedlogics.service.LogicService;
import io.linkedlogics.service.ServiceLocator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessDefinitionWriter {
	private static String TAB = "    ";
	private static String LINE = "\n";
	private static String BUILD = ".build()";
	private ProcessDefinition process;

	public String write() {
		StringBuilder builder = new StringBuilder("import static io.linkedlogics.LinkedLogicsBuilder.*;\n\n");
		write(builder, process);
		return builder.toString();
	}

	private void write(StringBuilder builder, ProcessDefinition process) {
		builder.append("return createProcess(\"").append(process.getId()).append("\", ").append(process.getVersion()).append(")");
		builder.append(TAB);
		if (process.getInputs() != null && !process.getInputs().isEmpty()) {
			write(builder.append("\n"), process.getInputs(), true);
		}

		process.getLogics().forEach(l -> {
			builder.append(LINE).append(TAB).append(".add(");
			write(builder, l);
			builder.append(")");
		});

		builder.append(LINE).append(BUILD).append(";");
	}

	private void write(StringBuilder builder, BaseLogicDefinition logic) {
		if (logic instanceof SingleLogicDefinition) {
			SingleLogicDefinition single = (SingleLogicDefinition) logic;
			write(builder, single);

			if (single.getApplication() != null) {
				builder.append(".application(\"").append(single.getApplication()).append("\")");
			}

			if (logic.getInputs() != null && !logic.getInputs().isEmpty()) {
				write(builder, logic.getInputs(), true);
			}

			if (single.getReturnAs() != null) {
				builder.append(".returnAs(\"").append(single.getReturnAs()).append("\")");
			}

			if (single.getCompensationLogic() != null) {
				builder.append(".compensate(");
				write(builder, single.getCompensationLogic());
				builder.append(")");
			}
		} else if (logic instanceof ProcessLogicDefinition) {
			write(builder, (ProcessLogicDefinition) logic);
			if (logic.getInputs() != null && !logic.getInputs().isEmpty()) {
				write(builder, logic.getInputs(), true);
			}
		} else if (logic instanceof BranchLogicDefinition) {
			write(builder, (BranchLogicDefinition) logic);
			if (logic.getInputs() != null && !logic.getInputs().isEmpty()) {
				write(builder, logic.getInputs(), true);
			}
		} else if (logic instanceof ExitLogicDefinition) {
			write(builder, (ExitLogicDefinition) logic);
		} else if (logic instanceof FailLogicDefinition) {
			write(builder, (FailLogicDefinition) logic);
		} else if (logic instanceof LoopLogicDefinition) {
			write(builder, (LoopLogicDefinition) logic);
			if (logic.getInputs() != null && !logic.getInputs().isEmpty()) {
				write(builder, logic.getInputs(), true);
			}
		} else if (logic instanceof GroupLogicDefinition) {
			write(builder, (GroupLogicDefinition) logic);
			if (logic.getInputs() != null && !logic.getInputs().isEmpty()) {
				write(builder, logic.getInputs(), true);
			}
		} else if (logic instanceof JumpLogicDefinition) {
			write(builder, (JumpLogicDefinition) logic);
		} else if (logic instanceof SavepointLogicDefinition) {
			write(builder, (SavepointLogicDefinition) logic);
		} else if (logic instanceof LogLogicDefinition) {
			write(builder, (LogLogicDefinition) logic);
			if (logic.getInputs() != null && !logic.getInputs().isEmpty()) {
				write(builder, logic.getInputs(), true);
			}
		} else if (logic instanceof VerifyLogicDefinition) {
			write(builder, (VerifyLogicDefinition) logic);
		} else if (logic instanceof ScriptLogicDefinition) {
			write(builder, (ScriptLogicDefinition) logic);
			if (logic.getInputs() != null && !logic.getInputs().isEmpty()) {
				write(builder, logic.getInputs(), true);
			}
		} else {
			builder.append("MISSING TYPE ").append(logic.getType());
		}

		if (logic.getName() != null) {
			builder.append(".name(\"").append(logic.getName()).append("\")");
		}
		
		if (logic.getDesc() != null) {
			builder.append(".desc(\"").append(logic.getDesc()).append("\")");
		}
		
		if (logic.getOutputs() != null && !logic.getOutputs().isEmpty()) {
			write(builder, logic.getOutputs(), false);
		}
		
		if (logic.getForced()) {
			builder.append(".forced()");
		}

		if (logic.getDelay() != null) {
			write(builder, logic.getDelay());
		}

		if (logic.getFork() != null) {
			write(builder, logic.getFork());
		}

		if (logic.getJoin() != null) {
			write(builder, logic.getJoin());
		}

		if (logic.getDisabled()) {
			builder.append(".disabled()");
		}

		if (logic.getLabel() != null) {
			write(builder, logic.getLabel());
		}

		if (logic.getTimeout() != null) {
			write(builder, logic.getTimeout());
		}

		if (logic.getRetry() != null) {
			write(builder, logic.getRetry());
		}

		if (logic.getErrors() != null && logic.getErrors().size() > 0) {
			write(builder, logic.getErrors());
		}
	}

	private void write(StringBuilder builder, RetryLogicDefinition retry) {
		builder.append(".retry(retry(max(").append(retry.getMaxRetries()).append("), seconds(").append(retry.getSeconds()).append("))");

		if (retry.isExclude()) {
			builder.append(".exclude()");
		}

		if (retry.isExclude()) {
			builder.append(".codes(").append(retry.getErrorCodeSet().stream().map(i -> String.valueOf(i)).collect(Collectors.joining(", ")));
			builder.append(")");
		}

		if (retry.getErrorMessageSet().size() > 0) {
			builder.append(".messages(");
			builder.append(retry.getErrorMessageSet().stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(", ")));
			builder.append(")");
		}

		builder.append(")");
	}

	private void write(StringBuilder builder, DelayLogicDefinition delay) {
		builder.append(".delayed(seconds(").append(delay.getSeconds()).append("))");
	}

	private void write(StringBuilder builder, ExitLogicDefinition exit) {
		builder.append("exit()");
	}

	private void write(StringBuilder builder, JumpLogicDefinition jump) {
		builder.append("jump(");
		if (jump.getTargetLabel() != null) {
			builder.append("\"").append(jump.getTargetLabel()).append("\"");
		} else if (jump.getTargetExpr() != null) {
			builder.append("expr(").append(escape(jump.getTargetExpr().getExpression())).append(")");
		}
		builder.append(")");
	}

	private void write(StringBuilder builder, SavepointLogicDefinition jump) {
		builder.append("savepoint()");
	}
	
	private void write(StringBuilder builder, LogLogicDefinition log) {
		builder.append("log(\"").append(log.getMessage()).append("\").level(").append(log.getLevel().toString()).append(")");
		
	}

	private void write(StringBuilder builder, VerifyLogicDefinition verify) {
		builder.append("verify(when(").append(escape(verify.getExpression().getExpression())).append("))");
		if (verify.getErrorCode() != null) {
			builder.append(".elseFailWithCode(").append(verify.getErrorCode()).append(")");

			if (verify.getErrorMessage() != null) {
				builder.append(".andMessage(\"").append(verify.getErrorMessage()).append("\")");
			}
		} else if (verify.getErrorMessage() != null) {
			builder.append(".elseFailWithMessage(\"").append(verify.getErrorMessage()).append("\")");
		}
	}

	private void write(StringBuilder builder, FailLogicDefinition fail) {
		builder.append("fail()");
		if (fail.getErrorCode() != null) {
			builder.append(".withCode(").append(fail.getErrorCode()).append(")");

			if (fail.getErrorMessage() != null) {
				builder.append(".andMessage(\"").append(fail.getErrorMessage()).append("\")");
			}
		} else if (fail.getErrorMessage() != null) {
			builder.append(".withMessage(\"").append(fail.getErrorMessage()).append("\")");
		}
	}

	private void write(StringBuilder builder, ForkLogicDefinition fork) {
		builder.append(".fork(").append(fork.getKey().map(k -> "\"" + k + "\"").orElse("")).append(")");
	}

	private void write(StringBuilder builder, JoinLogicDefinition join) {
		builder.append(".join(").append(Arrays.stream(join.getKey()).map(m -> "\"" + m + "\"").collect(Collectors.joining(", "))).append(")");
	}

	private void write(StringBuilder builder, TimeoutLogicDefinition timeout) {
		builder.append(".timeout(seconds(").append(timeout.getSeconds()).append("))");
	}

	private void write(StringBuilder builder, LabelLogicDefinition label) {
		builder.append(".label(\"").append(label.getLabel()).append("\")");
	}

	private void write(StringBuilder builder, List<ErrorLogicDefinition> errors) {
		builder.append(".handle(");

		builder.append(errors.stream().map(error -> {
			StringBuilder errorBuilder = new StringBuilder("error()");
			
			if (!error.getErrorCodeSet().isEmpty()) {
				errorBuilder.append(".withCodes(").append(error.getErrorCodeSet().stream().map(i -> String.valueOf(i)).collect(Collectors.joining(", "))).append(")");

				if (!error.getErrorMessageSet().isEmpty()) {
					errorBuilder.append(".orMessages(");
					errorBuilder.append(error.getErrorMessageSet().stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(", ")));
					errorBuilder.append(")");
				}
			} else if (!error.getErrorMessageSet().isEmpty()) {
				errorBuilder.append(".withMessages(");
				errorBuilder.append(error.getErrorMessageSet().stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(", ")));
				errorBuilder.append(")");
			}

			if (error.isThrowAgain()) {
				errorBuilder.append(".throwAgain(");
				if (error.getThrowErrorCode() != null) {
					errorBuilder.append(error.getThrowErrorCode()).append(", \"").append(error.getThrowErrorMessage()).append("\"");
				}
				errorBuilder.append(")");
			}

			if (error.getErrorLogic() != null) {
				errorBuilder.append(".using(");
				write(errorBuilder, error.getErrorLogic());
				errorBuilder.append(")");
			}
			
			return errorBuilder.toString();
		}).collect(Collectors.joining(",")));
		
		builder.append(")");
	}

	private void write(StringBuilder builder, BranchLogicDefinition branch) {
		builder.append("branch(when(").append(escape(branch.getExpression().getExpression())).append("), ");
		write(builder, branch.getLeftLogic());
		if (branch.getRightLogic() != null) {
			builder.append(", ");
			write(builder, branch.getRightLogic());
		}
		builder.append(")");
	}

	private void write(StringBuilder builder, ProcessLogicDefinition process) {
		builder.append("process(\"").append(process.getProcessId()).append("\", ").append(process.getVersion()).append(")");
	}

	private void write(StringBuilder builder, LoopLogicDefinition loop) {
		builder.append("loop(when(").append(escape(loop.getExpression().getExpression())).append("), ");

		for (int i = 0; i < loop.getLogics().size(); i++) {
			if (i > 0) {
				builder.append(", ");
			}
			write(builder, loop.getLogics().get(i));
		}

		builder.append(")");
	}
	
	private void write(StringBuilder builder, GroupLogicDefinition group) {
		builder.append("group(");

		for (int i = 0; i < group.getLogics().size(); i++) {
			if (i > 0) {
				builder.append(", ");
			}
			write(builder, group.getLogics().get(i));
		}

		builder.append(")");
	}

	private void write(StringBuilder builder, SingleLogicDefinition logic) {
		builder.append("logic(\"").append(logic.getLogicId()).append("\"");
		if (logic.getLogicVersion() != LogicService.LATEST_VERSION) {
			builder.append(", ").append(logic.getLogicVersion());
		}
		builder.append(")");
	}
	
	private void write(StringBuilder builder, ScriptLogicDefinition logic) {
		String escaped = escape(logic.getExpression().getExpression());
		builder.append("script(").append(escaped).append(")");
		if (logic.getReturnAs() != null) {
			builder.append(".returnAs(\"").append(logic.getReturnAs()).append("\")");
		} else if (logic.isReturnAsMap()) {
			builder.append(".returnAsMap()");
		}
	}
	
	private String escape(String expr) {
		return "decode(\"" + LinkedLogicsBuilder.encode(expr) + "\")";
	}

	private void write(StringBuilder builder, Map<String, Object> params, boolean isInput) {
		if (params != null && !params.isEmpty()) {
			if (isInput) {
				builder.append(".inputs(");
			} else {
				builder.append(".outputs(");
			}
			builder.append(params.entrySet().stream().map(e -> {
				StringBuilder input = new StringBuilder();
				input.append("\"").append(e.getKey()).append("\", ");
				if (e.getValue() instanceof ExpressionLogicDefinition) {
					input.append("expr(").append(escape(((ExpressionLogicDefinition) e.getValue()).getExpression())).append(")");
				} else if (e.getValue() instanceof Map || e.getValue() instanceof List || e.getValue() instanceof Set) { 
					try {
						input.append("expr(").append(escape(ServiceLocator.getInstance().getEvaluatorService().toParseableJson(e.getValue()))).append(")");
					} catch (JsonProcessingException ex) {
						throw new RuntimeException(ex);
					}
				} else if (e.getValue() instanceof String) { 
					input.append("\"").append(e.getValue()).append("\"");
				} else {
					input.append(e.getValue());
				}
				return input.toString();
			}).collect(Collectors.joining(", ")));
			builder.append(")");
		}
	}
}
