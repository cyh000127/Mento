package com.mento.domain.item.dto.common;

import com.mento.domain.user.entity.User;

import lombok.Builder;

@Builder
public record ExpiringItemCountDto(
	User user,
	Long itemCount
) {
}
