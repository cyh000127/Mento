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
import com.mento.domain.item.dto.response.ItemPageResDto;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.service.query.ItemQueryService;
import com.mento.domain.item.validator.ItemValidator;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.service.query.ReservationQueryService;
import com.mento.domain.user.converter.UserConverter;
import com.mento.domain.user.dto.request.UserItemsReqDto;
import com.mento.domain.user.dto.request.UserUpdateReqDto;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.command.UserCommandService;
import com.mento.domain.user.service.query.UserQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFacadeService {

	private final UserQueryService userQueryService;
	private final UserCommandService userCommandService;
	private final ItemQueryService itemQueryService;
	private final ItemValidator itemValidator;
	private final ReservationQueryService reservationQueryService;

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
		Reservation reservation = reservationQueryService.findById(reqDto.reservationId());
		itemValidator.validateMentorAccess(authUser, reservation, userId);

		Pageable pageable = PageUtils.getPageable(reqDto.page(), reqDto.size(), reqDto.sortType().getSort());
		Page<Item> items = itemQueryService.findAllByUserIdWithFilters(userId, reqDto.status(), reqDto.category(),
			reqDto.isFavorite(), pageable);

		return items.map(ItemConverter::toItemPageResDto);
	}
}
