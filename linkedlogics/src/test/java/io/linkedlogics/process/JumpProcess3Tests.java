package io.linkedlogics.process;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.linkedlogics.service.handler.process.ProcessFlowHandler;

public class JumpProcess3Tests {

	@ParameterizedTest
	@MethodSource("provideFinderForProcessA")
	public void shouldComparePositions(String position1, String position2, boolean comparisonResult) {
		assertThat(ProcessFlowHandler.compareLogicPosition(position1, position2)).isEqualTo(comparisonResult);
	}
	
	private static Stream<Arguments> provideFinderForProcessA() {
		return Stream.of(
			Arguments.of("1", "2", true),
			Arguments.of("2", "1", false),
			Arguments.of("1.1.1.1", "2", true),
			Arguments.of("1", "2.1.1.2", true),
			Arguments.of("1.1.1.1.3", "1.1.1.1.2", false),
			Arguments.of("1.1.1.1.3", "1.1.1.1", true)
		);
	}
}
