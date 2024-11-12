package io.linkedlogics.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.linkedlogics.process.ErrorProcess3Tests;
import io.linkedlogics.process.JumpProcess1Tests;
import io.linkedlogics.process.SimpleProcess2Tests;

public class Main2 {
	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(JumpProcess1Tests.scenario1()));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
