package io.linkedlogics.service.local;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.LoggingService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalLoggingService implements LoggingService {

	@Override
	public void trace(Context context, String msg) {
		log.trace(msg);
	}

	@Override
	public void debug(Context context, String msg) {
		log.debug(msg);
	}

	@Override
	public void info(Context context, String msg) {
		log.info(msg);
	}

	@Override
	public void warn(Context context, String msg) {
		log.warn(msg);
	}

	@Override
	public void error(Context context, String msg) {
		log.error(msg);
	}
	
	@Override
	public void error(Context context, String msg, Throwable error) {
		log.error(msg, error);
	}

	@Override
	public void set(Context context) {
		
	}

	@Override
	public void remove(Context context) {
		
	}
}
