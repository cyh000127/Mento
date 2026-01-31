package com.mento.domain.item.service.query;

import com.mento.domain.item.entity.Item;

public interface ItemQueryService {

	Item findById(Long itemId);
}
