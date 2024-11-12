package io.linkedlogics.spring.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.service.ServiceConfigurer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringServiceStarter implements ApplicationRunner {

	private final Set<String> beansContainingProcess = new HashSet<>();
	private final Set<String> beansContainingLogic = new HashSet<>();
	
	@Autowired
	private final List<ServiceConfigurer> configurers;
	
	public void addLogicBean(String bean) {
		beansContainingLogic.add(bean);
	}
	
	public void addProcessBean(String bean) {
		beansContainingProcess.add(bean);
	}
	
	public boolean checkLogicBean(String bean) {
		return beansContainingLogic.contains(bean);
	}
	
	public boolean checkProcessBean(String bean) {
		return beansContainingProcess.contains(bean);
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		configurers.forEach(LinkedLogics::configure);
		LinkedLogics.launch();
	}
}
