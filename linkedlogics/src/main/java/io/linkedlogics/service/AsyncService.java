package io.linkedlogics.service;

import java.util.Optional;

import io.linkedlogics.context.Context;

public interface AsyncService extends LinkedLogicsService {
	void set(Context context);
	
	Optional<Context> remove(String contextId);
	
	void asyncCallback(String contextId, Object result);
	
	void asyncCallerror(String contextId, Throwable error);
	
	void setContextId(String contextId);
	
	void unsetContextId();
	
	String getContextId();
}
