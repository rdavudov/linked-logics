package io.linkedlogics.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.linkedlogics.service.LimitService.Interval;
import io.linkedlogics.service.local.LocalLimitService;

public class LimitServiceTests {

	private LocalLimitService limitService;

	@BeforeEach
	public void setUp() {
		limitService = new LocalLimitService();
		limitService.start();
	}

	@AfterEach
	public void cleanUp() {
		Optional.of(limitService).ifPresent(s -> s.stop());
	}

	@Test
	public void shouldLimitSuccess() {
		int threads = 5;
		String key = "KEY";
		int limit = 50;
		int checksPerThread = 20;
		OffsetDateTime timestamp = OffsetDateTime.now();

		AtomicInteger success = new AtomicInteger();
		AtomicInteger fail = new AtomicInteger();
		CyclicBarrier barrier = new CyclicBarrier(threads);

		Thread[] threadArray = new Thread[threads];
		IntStream.range(0, threads).forEach(i -> {
			threadArray[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						barrier.await();
						for (int j = 0; j < checksPerThread; j++) {
							boolean result = limitService.increment(key, timestamp, Interval.SECOND, limit, 1);
							if (result) {
								success.incrementAndGet();
							} else {
								fail.incrementAndGet();
							}
						}
					} catch (InterruptedException|BrokenBarrierException e) { }
				}
			});

			threadArray[i].start();
		});

		for (int k = 0; k < threadArray.length; k++) {
			try {
				threadArray[k].join();
			} catch (InterruptedException e) { }
		}

		assertThat(success.get()).isEqualTo(limit);
		assertThat(fail.get()).isEqualTo(threads * checksPerThread - limit);

	}

	@Test
	public void shouldLimitSuccessInSeconds() {
		int iterations = 3;
		int threads = 5;
		String key = "KEY";
		int limit = 50;
		int checksPerThread = 20;

		AtomicInteger success = new AtomicInteger();
		AtomicInteger fail = new AtomicInteger();
		CyclicBarrier barrier = new CyclicBarrier(threads);

		Thread[] threadArray = new Thread[threads];
		IntStream.range(0, threads).forEach(i -> {
			threadArray[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						barrier.await();
						for (int k = 0; k < iterations; k++) {
							for (int j = 0; j < checksPerThread; j++) {
								boolean result = limitService.increment(key, OffsetDateTime.now(), Interval.SECOND, limit, 1);
								if (result) {
									success.incrementAndGet();
								} else {
									fail.incrementAndGet();
								}
							}

							Thread.sleep(1000);
						}

					} catch (InterruptedException|BrokenBarrierException e) { }
				}
			});

			threadArray[i].start();
		});

		for (int k = 0; k < threadArray.length; k++) {
			try {
				threadArray[k].join();
			} catch (InterruptedException e) { }
		}

		assertThat(success.get()).isEqualTo(iterations*limit);
		assertThat(fail.get()).isEqualTo(threads*checksPerThread*iterations - iterations*limit);

	}

	@Test
	public void testTimeToWait() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		LocalDateTime localDateTime = LocalDateTime.parse("2023-01-01 12:05:37.200", formatter);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
		
		assertThat(limitService.getTimeToWait(offsetDateTime, Interval.SECOND)).isEqualTo(800);
		assertThat(limitService.getTimeToWait(offsetDateTime, Interval.MINUTE)).isEqualTo(800 + 22 * 1000);
		assertThat(limitService.getTimeToWait(offsetDateTime, Interval.HOUR)).isEqualTo(800 + 22 * 1000 + 54 * 60 * 1000);
		assertThat(limitService.getTimeToWait(offsetDateTime, Interval.DAY)).isEqualTo(800 + 22 * 1000 + 54 * 60 * 1000 + 11 * 3600000);
	}
}
