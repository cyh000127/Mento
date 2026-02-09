package com.mento.domain.consulting.vo;

import java.io.Serializable;

import lombok.Builder;

@Builder
public record ChatLogEntryVo(
	String role,
	String content
) implements Serializable {
}
