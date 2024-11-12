package io.linkedlogics.service;

import io.linkedlogics.context.Context;

public interface LoggingService extends LinkedLogicsService {
	void trace(Context context, String msg);
	
	void debug(Context context, String msg);
	
	void info(Context context, String msg);
	
	void warn(Context context, String msg);
	
	void error(Context context, String msg);
	
	void error(Context context, String msg, Throwable error);
	
	void set(Context context);
	
	void remove(Context context);
}
