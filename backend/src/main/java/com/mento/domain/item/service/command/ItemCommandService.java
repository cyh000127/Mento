package com.mento.domain.item.service.command;

import java.time.LocalDate;

import com.mento.domain.item.entity.Item;

public interface ItemCommandService {

	Item saveItem(final Item item);

	int expireOverdueItems(final LocalDate today);
}
