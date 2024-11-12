package io.linkedlogics.jdbc.entity;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
	private Long id;
	private String queue;
	private String payload;
	private OffsetDateTime createdAt;
	private String consumedBy;
}
