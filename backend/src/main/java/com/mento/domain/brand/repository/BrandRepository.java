package com.mento.domain.brand.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.brand.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
	List<Brand> findByBrandNameContaining(String brandName);
}
