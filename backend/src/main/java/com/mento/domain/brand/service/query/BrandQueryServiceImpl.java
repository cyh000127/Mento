package com.mento.domain.brand.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.brand.entity.Brand;
import com.mento.domain.brand.exception.BrandException;
import com.mento.domain.brand.repository.BrandRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandQueryServiceImpl implements BrandQueryService {

	private final BrandRepository brandRepository;

	@Override
	public Brand getBrand(final Long brandId) {
		return brandRepository.findById(brandId)
			.orElseThrow(() -> new BrandException(ErrorCode.BRAND_NOT_FOUND));
	}
}
