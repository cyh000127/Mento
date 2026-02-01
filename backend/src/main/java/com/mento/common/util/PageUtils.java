package com.mento.common.util;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PageUtils {

	public Pageable getPageable(final int page, final int size) {
		return PageRequest.of(page - 1, size);
	}

	public Pageable getPageable(
		final Integer page,
		final Integer size,
		final Sort sort
	) {
		return PageRequest.of(page, size, sort);
	}

	public <T> Page<T> toPage(final List<T> content, final Pageable pageable, final long total) {
		return new PageImpl<>(content, pageable, total);
	}
}