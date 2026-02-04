package com.mento.domain.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.auth.constant.AuthConstant;
import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.common.util.PageUtils;
import com.mento.domain.item.converter.ItemConverter;
import com.mento.domain.item.dto.common.ItemInfoResDto;
import com.mento.domain.item.dto.response.ItemPageResDto;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.entity.ItemHistory;
import com.mento.domain.item.enums.ItemHistoryAction;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.factory.ItemFactory;
import com.mento.domain.item.factory.ItemHistoryFactory;
import com.mento.domain.item.service.command.ItemCommandService;
import com.mento.domain.item.service.command.ItemHistoryCommandService;
import com.mento.domain.item.service.query.ItemQueryService;
import com.mento.domain.item.validator.ItemValidator;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.service.query.ProductQueryService;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.service.query.ReservationQueryService;
import com.mento.domain.user.converter.UserConverter;
import com.mento.domain.user.dto.request.MentorAddItemReqDto;
import com.mento.domain.user.dto.request.UserItemsReqDto;
import com.mento.domain.user.dto.request.UserUpdateReqDto;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.command.UserCommandService;
import com.mento.domain.user.service.query.UserQueryServiceImpl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFacadeService {

	private final UserQueryServiceImpl userQueryService;
	private final UserCommandService userCommandService;

	private final ItemCommandService itemCommandService;
	private final ItemQueryService itemQueryService;
	private final ItemHistoryCommandService itemHistoryCommandService;
	private final ItemValidator itemValidator;
	private final ItemFactory itemFactory;
	private final ItemHistoryFactory itemHistoryFactory;

	private final ReservationQueryService reservationQueryService;
	private final ProductQueryService productQueryService;

	public UserResDto getUser(final Long id, final AuthenticatedUser authUser) {
		if (AuthConstant.ROLE_USER.equals(authUser.getRole()) && !authUser.getId().equals(id)) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		User user = userQueryService.findById(id);
		return UserConverter.toUserResDto(user);
	}

	public UserResDto updateUser(final AuthenticatedUser authUser, final UserUpdateReqDto reqDto) {
		User user = userCommandService.update(authUser.getId(), reqDto);
		return UserConverter.toUserResDto(user);
	}

	@Transactional(readOnly = true)
	public Page<ItemPageResDto> getAllItemsByUserId(
		final AuthenticatedUser authUser,
		final Long userId,
		final UserItemsReqDto reqDto
	) {
		Reservation reservation = reservationQueryService.findWithDetailsById(reqDto.reservationId());
		itemValidator.validateMentorAccess(authUser, reservation, userId);

		Pageable pageable = PageUtils.getPageableOrDefault(reqDto.page(), reqDto.size());
		Page<Item> items = itemQueryService.findAllByUserId(userId, pageable);

		return items.map(ItemConverter::toItemPageResDto);
	}

	@Transactional
	public ItemInfoResDto addItemToUser(
		final AuthenticatedUser mentor,
		final Long userId,
		final MentorAddItemReqDto reqDto
	) {
		Reservation reservation = reservationQueryService.findById(reqDto.reservationId());
		itemValidator.validateMentorAccess(mentor, reservation, userId);

		Product product = productQueryService.findById(reqDto.productId());
		User user = userQueryService.findById(userId);

		Item item = itemFactory.createItem(user, product, ItemStatus.RECOMMENDED);
		Item savedItem = itemCommandService.saveItem(item);

		saveItemHistory(user, product);

		return ItemConverter.toItemInfoResDto(savedItem);
	}

	private void saveItemHistory(final User user, final Product product) {
		ItemHistory history = itemHistoryFactory.createHistory(user, product, ItemHistoryAction.CREATED);
		itemHistoryCommandService.saveHistory(history);
	}
}
