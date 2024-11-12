package io.linkedlogics.service;

import io.linkedlogics.context.Context;

public interface TrackingService extends LinkedLogicsService {
	void track(Context context);
}
