package com.mento.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}