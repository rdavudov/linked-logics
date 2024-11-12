package io.linkedlogics.jdbc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.LinkedLogicsRegister;


@ExtendWith(LinkedLogicsExtension.class)
@LinkedLogicsRegister(serviceConfigurerClasses = JdbcServiceConfigurer.class)
public class ProcessServiceTests {
	
	@Test
	public void shouldRefreshProcesses() {
		JdbcProcessService service = (JdbcProcessService) ServiceLocator.getInstance().getProcessService();
		service.refreshProcesses();
	}
}
