package io.linkedlogics.redis.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.linkedlogics.redis.extension.RedisExtension;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.SchedulerService.Schedule;
import io.linkedlogics.service.SchedulerService.ScheduleType;
import io.linkedlogics.service.common.QueueSchedulerService;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.LinkedLogicsRegister;
import redis.embedded.RedisServer;

@ExtendWith({LinkedLogicsExtension.class, RedisExtension.class})
@LinkedLogicsRegister(serviceConfigurerClasses = RedisServiceConfigurer.class, serviceClasses = SchedulerServiceTests.TestSchedulerService.class)
public class SchedulerServiceTests {

	@Test
	public void shouldSchedulingSuccessful() {
		long start = System.currentTimeMillis();
		ServiceLocator.getInstance().getSchedulerService().schedule(new Schedule("id", "LOGIC_3sec", "1", OffsetDateTime.now().plusSeconds(3), ScheduleType.DELAY));
		ServiceLocator.getInstance().getSchedulerService().schedule(new Schedule("id", "LOGIC_10sec", "1", OffsetDateTime.now().plusSeconds(10), ScheduleType.DELAY));
		ServiceLocator.getInstance().getSchedulerService().schedule(new Schedule("id", "LOGIC_40sec", "1", OffsetDateTime.now().plusSeconds(40), ScheduleType.DELAY));
		ServiceLocator.getInstance().getSchedulerService().schedule(new Schedule("id", "LOGIC_1min_10sec", "1", OffsetDateTime.now().plusSeconds(70), ScheduleType.DELAY));
	
		while (TestSchedulerService.getTimestamps().size() < 4) {
			try { Thread.sleep(100); } catch (InterruptedException e) { }
		}
		
		List<Long> timestamps = TestSchedulerService.getTimestamps();
		
		assertThat((timestamps.get(0) - start) / 1000).isGreaterThanOrEqualTo(2);
		assertThat((timestamps.get(1) - start) / 1000).isGreaterThanOrEqualTo(9);
		assertThat((timestamps.get(2) - start) / 1000).isGreaterThanOrEqualTo(39);
		assertThat((timestamps.get(3) - start) / 1000).isGreaterThanOrEqualTo(69);
	}
	
	public static class TestSchedulerService extends QueueSchedulerService {
		private static List<Long> timestamps;

		public TestSchedulerService() {
			timestamps = new ArrayList<>();
		}

		@Override
		public void handle(Schedule schedule) {
			timestamps.add(System.currentTimeMillis());
		}

		public static List<Long> getTimestamps() {
			return timestamps;
		}
	}
}
