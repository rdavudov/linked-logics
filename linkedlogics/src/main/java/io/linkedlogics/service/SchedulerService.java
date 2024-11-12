package io.linkedlogics.service;

import java.time.OffsetDateTime;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.task.DelayedTask;
import io.linkedlogics.service.task.RetryTask;
import io.linkedlogics.service.task.TimeoutTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface SchedulerService extends LinkedLogicsService {
	void schedule(Schedule schedule);
	
	default void handle(Schedule schedule) {
		if (schedule.getType() == ScheduleType.DELAY) {
			ServiceLocator.getInstance().getProcessorService().process(new DelayedTask(Context.fromSchedule(schedule)));
		} else if (schedule.getType() == ScheduleType.RETRY) {
			ServiceLocator.getInstance().getProcessorService().process(new RetryTask(Context.fromSchedule(schedule)));
		} else if (schedule.getType() == ScheduleType.TIMEOUT) {
			ServiceLocator.getInstance().getProcessorService().process(new TimeoutTask(Context.fromSchedule(schedule)));
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Schedule {
		private String contextId;
		private String logicId;
		private String logicPosition;
		private OffsetDateTime expiresAt;
		private ScheduleType type;
	}
	
	public enum ScheduleType {
		TIMEOUT,
		RETRY,
		DELAY;
	}
}