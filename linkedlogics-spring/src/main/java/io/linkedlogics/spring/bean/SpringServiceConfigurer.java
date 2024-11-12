package io.linkedlogics.spring.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotationMetadata;

import io.linkedlogics.service.ServiceConfigurer;
import io.linkedlogics.service.ServiceProvider;
import io.linkedlogics.service.local.LocalServiceConfigurer;
import io.linkedlogics.spring.EnableLinkedLogics;
import io.linkedlogics.spring.SpringProfile;

@Configuration
public class SpringServiceConfigurer implements ImportAware {

	@Autowired
	private Environment environment;
	
	protected EnableLinkedLogics annotation;

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		annotation = (EnableLinkedLogics) ((Class) importMetadata.getAnnotations().get(EnableLinkedLogics.class).getSource()).getAnnotationsByType(EnableLinkedLogics.class)[0];
	}
	
	@Bean
	public SpringServiceStarter springServiceStarter(List<ServiceConfigurer> list) {
		return new SpringServiceStarter(list);
	}
	
	@Bean
	public SpringLogicConfigurer springLogicConfigurer(List<ServiceConfigurer> list) {
		return new SpringLogicConfigurer(springServiceStarter(list));
	}
	
	@Bean
	public SpringProcessConfigurer springProcessConfigurer(List<ServiceConfigurer> list) {
		return new SpringProcessConfigurer(springServiceStarter(list));
	}
	
	@Bean
	@Order(0)
	public ServiceConfigurer localServiceConfigurer() {
		return new LocalServiceConfigurer();
	}

	@Bean
	@Order(1)
	public ServiceConfigurer annotatedServiceConfigurer() {
		SpringProfile[] profiles = annotation.value();
		
		SpringProfile selectedProfile = null;
		
		for (SpringProfile profile : profiles) {
			if (profile.profile().length == 0) {
				selectedProfile = profile;
			} else if (environment.acceptsProfiles(Profiles.of(profile.profile()))) {
				selectedProfile = profile;
				break;
			}
		}
		
		if (selectedProfile != null) {
			ServiceConfigurer serviceConfigurer = new ServiceConfigurer();
			getProvider(selectedProfile.processing()).getProcessingServices().forEach(serviceConfigurer::configure);
			getProvider(selectedProfile.evaluating()).getEvaluatingServices().forEach(serviceConfigurer::configure);
			getProvider(selectedProfile.storing()).getStoringServices().forEach(serviceConfigurer::configure);
			getProvider(selectedProfile.scheduling()).getSchedulingServices().forEach(serviceConfigurer::configure);
			getProvider(selectedProfile.messaging()).getMessagingServices().forEach(serviceConfigurer::configure);
			getProvider(selectedProfile.monitoring()).getMonitoringServices().forEach(serviceConfigurer::configure);
			getProvider(selectedProfile.tracking()).getTrackingServices().forEach(serviceConfigurer::configure);
			return serviceConfigurer;
		} else {
			return new ServiceConfigurer();
		}
	}
	
	private ServiceProvider getProvider(Class<? extends ServiceProvider> providerClass) {
		try {
			return (ServiceProvider) providerClass.getConstructor().newInstance() ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
