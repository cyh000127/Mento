package com.mento.domain.item.service.command;

import com.mento.domain.item.entity.ItemHistory;

public interface ItemHistoryCommandService {

	ItemHistory saveHistory(ItemHistory history);
}
