package com.mento.domain.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReservationDraftReqDto(
	@NotNull(message = "슬롯 ID는 필수입니다")
	Long slotId
) {
}
