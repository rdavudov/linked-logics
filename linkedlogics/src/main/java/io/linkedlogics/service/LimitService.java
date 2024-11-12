package io.linkedlogics.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import lombok.Getter;

public interface LimitService extends LinkedLogicsService {

	@Getter
	public enum Interval {
		SECOND("yyyyMMddHHmmss"),
		MINUTE("yyyyMMddHHmm"),
		HOUR("yyyyMMddHH"),
		DAY("yyyyMMdd");
		
		private DateTimeFormatter formatter;
		
		private Interval(String format) {
			formatter = DateTimeFormatter.ofPattern(format);
		}
	}
	
	void reset(String key, OffsetDateTime timestamp, Interval interval);
	
	boolean check(String key, OffsetDateTime timestamp, Interval interval, long limit);
	
	boolean increment(String key, OffsetDateTime timestamp, Interval interval, long limit, long increment);
	
	default String getKey(String key, OffsetDateTime timestamp, Interval interval) {
		return key + ":" + interval.getFormatter().format(timestamp);
	}
	
	default Long getTimeToWait(OffsetDateTime timestamp, Interval interval) {
		OffsetDateTime nextTimestamp = timestamp;
		if (interval == Interval.SECOND) {
			nextTimestamp = timestamp.truncatedTo(ChronoUnit.SECONDS).withNano(0).plusSeconds(1);
		} else if (interval == Interval.MINUTE) {
			nextTimestamp = timestamp.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
		} else if (interval == Interval.HOUR) {
			nextTimestamp = timestamp.truncatedTo(ChronoUnit.HOURS).plusHours(1);
		} else if (interval == Interval.DAY) {
			nextTimestamp = timestamp.truncatedTo(ChronoUnit.DAYS).plusDays(1);
		}
		
		return Duration.between(timestamp, nextTimestamp).toMillis();
	}
}
