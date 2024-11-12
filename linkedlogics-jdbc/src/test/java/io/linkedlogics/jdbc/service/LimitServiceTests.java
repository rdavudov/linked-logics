package io.linkedlogics.jdbc.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.linkedlogics.service.LimitService.Interval;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.LinkedLogicsRegister;

@ExtendWith(LinkedLogicsExtension.class)
@LinkedLogicsRegister(serviceConfigurerClasses = JdbcServiceConfigurer.class)
public class LimitServiceTests {

	@Test
	public void shouldLimitSuccess() {
		int threads = 5;
		String key = "KEY1";
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
							boolean result = ServiceLocator.getInstance().getLimitService().increment(key, timestamp, Interval.SECOND, limit, 1);
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
		String key = "KEY2";
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
								boolean result = ServiceLocator.getInstance().getLimitService().increment(key, OffsetDateTime.now(), Interval.SECOND, limit, 1);
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
}
