package io.linkedlogics.spring.bean;

import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Logic;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringLogicConfigurer implements BeanPostProcessor {

	private final SpringServiceStarter starter;
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Arrays.stream(bean.getClass().getMethods()).filter(m -> m.getAnnotation(Logic.class) != null).findAny().ifPresent(m -> starter.addLogicBean(beanName));
		Arrays.stream(bean.getClass().getDeclaredMethods()).filter(m -> m.getAnnotation(Logic.class) != null).findAny().ifPresent(m -> starter.addLogicBean(beanName));
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (starter.checkLogicBean(beanName)) {
			LinkedLogics.registerLogic(bean);
		}
		
		return bean;
	}
}
