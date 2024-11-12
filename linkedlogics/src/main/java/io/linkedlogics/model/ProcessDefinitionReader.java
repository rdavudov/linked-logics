package io.linkedlogics.model;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessDefinitionReader {
	private String process;

	public ProcessDefinition read() {
		Binding binding = new Binding();
		GroovyShell groovyShell = new GroovyShell(binding);
		Object result = groovyShell.evaluate(process);
		groovyShell.resetLoadedClasses();
		return (ProcessDefinition) result;
	}
}
