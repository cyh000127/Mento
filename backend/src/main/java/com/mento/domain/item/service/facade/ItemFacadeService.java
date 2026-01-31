package com.mento.domain.item.service.facade;

import org.springframework.stereotype.Service;

import com.mento.domain.item.converter.ItemConverter;
import com.mento.domain.item.dto.common.ItemInfoResDto;
import com.mento.domain.item.dto.request.UserItemAddReqDto;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.factory.ItemFactory;
import com.mento.domain.item.service.command.ItemCommandService;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.service.query.ProductQueryService;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemFacadeService {

	private final ItemCommandService commandService;
	private final ProductQueryService productQueryService;
	private final UserQueryService userQueryService;

	private final ItemFactory itemFactory;

	public ItemInfoResDto addUserItem(final Long userId, final UserItemAddReqDto reqDto) {
		Product product = productQueryService.findById(reqDto.productId());
		User user = userQueryService.findById(userId);

		Item item = itemFactory.createItem(user, product);
		Item savedItem = commandService.saveItem(item);

		return ItemConverter.toItemInfoResDto(savedItem);
	}
}
