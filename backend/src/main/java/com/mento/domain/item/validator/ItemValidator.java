package com.mento.domain.item.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.mento.common.error.ErrorCode;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.exception.ItemException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ItemValidator {

	public void validate(final Long userId, final Item item) {
		validateItemBelongsToUser(userId, item);
	}

	private void validateItemBelongsToUser(final Long userId, final Item item) {
		if (!Objects.equals(userId, item.getUser().getId())) {
			throw new ItemException(ErrorCode.ITEM_ACCESS_DENIED);
		}
	}
}
