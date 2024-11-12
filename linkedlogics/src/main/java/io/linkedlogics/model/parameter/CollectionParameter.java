package io.linkedlogics.model.parameter;

import lombok.Getter;

@Getter
public class CollectionParameter extends InputParameter {
	protected Class<?> genericType;
	
	public CollectionParameter(String name, Class<?> type, Class<?> genericType, boolean required, boolean returned) {
		super(name, type, required, returned);
		this.genericType = genericType;
	}
}
