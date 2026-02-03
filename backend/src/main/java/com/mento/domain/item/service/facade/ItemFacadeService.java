package com.mento.domain.item.service.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.item.converter.ItemConverter;
import com.mento.domain.item.dto.common.ItemInfoResDto;
import com.mento.domain.item.dto.request.ItemHistoryReqDto;
import com.mento.domain.item.dto.request.ItemSearchReqDto;
import com.mento.domain.item.dto.request.UserItemAddReqDto;
import com.mento.domain.item.dto.response.ItemHistoryResDto;
import com.mento.domain.item.dto.response.ItemInfoDetailResDto;
import com.mento.domain.item.dto.response.ItemPageResDto;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.entity.ItemHistory;
import com.mento.domain.item.enums.ItemHistoryAction;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.enums.SortType;
import com.mento.domain.item.factory.ItemFactory;
import com.mento.domain.item.factory.ItemHistoryFactory;
import com.mento.domain.item.service.command.ItemCommandService;
import com.mento.domain.item.service.command.ItemHistoryCommandService;
import com.mento.domain.item.service.query.ItemHistoryQueryService;
import com.mento.domain.item.service.query.ItemQueryService;
import com.mento.domain.item.validator.ItemValidator;
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
	private final ItemQueryService itemQueryService;
	private final ItemHistoryQueryService itemHistoryQueryService;
	private final ItemHistoryCommandService itemHistoryCommandService;
	private final ProductQueryService productQueryService;
	private final UserQueryService userQueryService;

	private final ItemFactory itemFactory;
	private final ItemHistoryFactory itemHistoryFactory;
	private final ItemValidator itemValidator;

	@Transactional
	public ItemInfoResDto addUserItem(final Long userId, final UserItemAddReqDto reqDto) {
		Product product = productQueryService.findById(reqDto.productId());
		User user = userQueryService.findById(userId);

		Item item = itemFactory.createItem(user, product, ItemStatus.OWNED);
		Item savedItem = commandService.saveItem(item);

		saveItemHistory(user, product, ItemHistoryAction.CREATED);

		return ItemConverter.toItemInfoResDto(savedItem);
	}

	@Transactional
	public ItemInfoResDto updateStatus(final Long userId, final Long itemId, final ItemStatus status) {
		Item item = findAndValidateUserItem(userId, itemId);

		item.updateStatus(status);

		return ItemConverter.toItemInfoResDto(item);
	}

	@Transactional
	public ItemInfoResDto toggleFavorite(final Long userId, final Long itemId) {
		Item item = findAndValidateUserItem(userId, itemId);

		item.toggleFavorite();

		return ItemConverter.toItemInfoResDto(item);
	}

	@Transactional
	public void deleteItem(final Long userId, final Long itemId) {
		Item item = findAndValidateUserItem(userId, itemId);
		item.withDraw();

		saveItemHistory(item.getUser(), item.getProduct(), ItemHistoryAction.DELETED);
	}

	@Transactional(readOnly = true)
	public Page<ItemPageResDto> findAllItemsByUserId(
		final Long userId,
		final ItemSearchReqDto reqDto
	) {
		Pageable pageable = PageRequest.of(reqDto.page(), reqDto.size(), SortType.from(reqDto.sort()).getSort());

		Page<Item> items = itemQueryService.findAllByUserIdWithFilters(
			userId, reqDto.status(), reqDto.category(), reqDto.isFavorite(), pageable
		);

		return items.map(ItemConverter::toItemPageResDto);
	}

	@Transactional(readOnly = true)
	public ItemInfoDetailResDto findByIdWithDetail(final Long userId, final Long itemId) {
		Item item = itemQueryService.findByIdWithDetail(itemId);
		itemValidator.validate(userId, item);
		return ItemConverter.toItemInfoDetailResDto(item);
	}

	@Transactional(readOnly = true)
	public Page<ItemHistoryResDto> getItemHistories(final Long userId, final ItemHistoryReqDto reqDto) {
		Pageable pageable = PageRequest.of(
			reqDto.page(),
			reqDto.size(),
			Sort.by(Sort.Direction.DESC, "createdAt")
		);

		Page<ItemHistory> histories = itemHistoryQueryService.findAllByUserIdWithFilters(
			userId,
			reqDto.productId(),
			reqDto.startDate(),
			reqDto.endDate(),
			pageable
		);

		return histories.map(ItemConverter::toItemHistoryResDto);
	}

	private Item findAndValidateUserItem(final Long userId, final Long itemId) {
		Item item = itemQueryService.findById(itemId);
		itemValidator.validate(userId, item);
		return item;
	}

	private void saveItemHistory(final User user, final Product product, final ItemHistoryAction action) {
		ItemHistory history = itemHistoryFactory.createHistory(user, product, action);
		itemHistoryCommandService.saveHistory(history);
	}
}
