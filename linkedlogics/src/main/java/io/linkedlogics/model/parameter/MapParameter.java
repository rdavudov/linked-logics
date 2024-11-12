package io.linkedlogics.model.parameter;

import lombok.Getter;

@Getter
public class MapParameter extends InputParameter {
	protected Class<?> keyType;
	protected Class<?> valueType;
	
	public MapParameter(String name, Class<?> type, Class<?> keyType, Class<?> valueType, boolean required, boolean returned) {
		super(name, type, required, returned);
		this.keyType = keyType;
		this.valueType = valueType;
	}
}
