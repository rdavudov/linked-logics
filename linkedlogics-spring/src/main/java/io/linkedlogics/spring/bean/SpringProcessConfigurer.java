package io.linkedlogics.spring.bean;

import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.model.ProcessDefinition;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringProcessConfigurer implements BeanPostProcessor {

	private final SpringServiceStarter starter;
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Arrays.stream(bean.getClass().getMethods()).filter(m -> ProcessDefinition.class.isAssignableFrom(m.getReturnType())).findAny().ifPresent(m -> starter.addProcessBean(beanName));
		Arrays.stream(bean.getClass().getDeclaredMethods()).filter(m -> ProcessDefinition.class.isAssignableFrom(m.getReturnType())).findAny().ifPresent(m -> starter.addProcessBean(beanName));
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (starter.checkProcessBean(beanName)) {
			LinkedLogics.registerProcess(bean);
		}
		
		return bean;
	}
}
