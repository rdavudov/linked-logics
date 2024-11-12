package io.linkedlogics;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;

public interface LinkedLogicsCallback {
	default void onSuccess(Context context) {
		
	}
	
	default void onFailure(Context context, ContextError error) {
		
	}
	
	default void onCancellation(Context context) {
		
	}
	
	default void onTimeout() {
		
	}
}
