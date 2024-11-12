package io.linkedlogics.test;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.LogicService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.test.asserts.AssertContext;
import io.linkedlogics.test.asserts.AssertInputs;
import io.linkedlogics.test.service.TestLogicService;
import io.linkedlogics.test.service.TestLogicService.MockedLogicDefinition;

public class LinkedLogicsTest {
	public static MockedLogicDefinition mockLogic(String logicId) {
		TestLogicService logicService = (TestLogicService) ServiceLocator.getInstance().getLogicService();
		return logicService.mock(logicId, LogicService.LATEST_VERSION);
	}
	
	public static MockedLogicDefinition mockLogic(String logicId, int logicVersion) {
		TestLogicService logicService = (TestLogicService) ServiceLocator.getInstance().getLogicService();
		return logicService.mock(logicId, logicVersion);
	}
	
	public static AssertInputs assertInputs() {
		return new AssertInputs();
	}
	
	public static AssertContext assertContext() {
		return new AssertContext();
	}
	
	public static AssertContext assertContext(Context context) {
		return new AssertContext(context);
	}
}
