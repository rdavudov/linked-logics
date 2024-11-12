package io.linkedlogics.service;

import io.linkedlogics.LinkedLogicsCallback;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.Status;

public interface CallbackService extends LinkedLogicsService {
	void set(String contextId, LinkedLogicsCallback callback);
	
	void publish(Context context);
	
	default void callback(Context context, LinkedLogicsCallback callback) {
		if (context.getStatus() == Status.FINISHED) {
			callback.onSuccess(context);
		} else if (context.getStatus() == Status.FAILED){
			callback.onFailure(context, context.getError());
		} else if (context.getStatus() == Status.CANCELLED) {
			callback.onCancellation(context);
		}
	}
}
