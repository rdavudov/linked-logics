package io.linkedlogics.test.service;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.local.LocalContextService;

public class TestContextService extends LocalContextService {

	private static String currentContext;
	private static Object object = new Object();
	
	public TestContextService() {
		resetCurrentContext();
	}
	
	@Override
	public void set(Context context) {
		super.set(context);
		
		if (context.getParentId() == null) {
			currentContext = toString(context);
			if (context.isFinished()) {
				unblock();
			}
		}
	}
	
	public static Context getCurrentContext() {
		return fromString(currentContext);
	}
	
	public static void resetCurrentContext() {
		currentContext = null;
	}
	
	public static void blockUntil() {
		synchronized (object) {
			try {
				object.wait();
			} catch (InterruptedException e) {}
		}
	}
	
	public static void blockUntil(long timeout) {
		synchronized (object) {
			try {
				object.wait(timeout);
			} catch (InterruptedException e) {}
		}
	}
	
	public static void unblock() {
		synchronized (object) {
			object.notifyAll();
		}
	}
}
