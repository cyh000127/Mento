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

	private static final int DEFAULT_PAGE = 0;
	private static final int DEFAULT_SIZE = 10;
	private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt", "id");

	public Pageable getPageableOrDefault(final int page, final int size) {
		return PageRequest.of(page, size);
	}

	public Pageable getPageableOrDefault(
		final Integer page,
		final Integer size,
		final Sort sort
	) {
		int actualPage = page != null ? page : DEFAULT_PAGE;
		int actualSize = size != null ? size : DEFAULT_SIZE;
		Sort actualSort = sort != null ? sort : DEFAULT_SORT;
		return PageRequest.of(actualPage, actualSize, actualSort);
	}

	public Pageable getPageableOrDefault(final Integer page, final Integer size) {
		int actualPage = page != null ? page : DEFAULT_PAGE;
		int actualSize = size != null ? size : DEFAULT_SIZE;
		return PageRequest.of(actualPage, actualSize);
	}

	public int getPageOrDefault(final Integer page) {
		return page != null ? page : DEFAULT_PAGE;
	}

	public int getSizeOrDefault(final Integer size) {
		return size != null ? size : DEFAULT_SIZE;
	}

	public <T> Page<T> toPage(final List<T> content, final Pageable pageable, final long total) {
		return new PageImpl<>(content, pageable, total);
	}
}