package com.mento.domain.product.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(indexName = "products", createIndex = false)
public class ProductDocument {
	@Id
	private String id;

	@Field(name = "productId", type = FieldType.Long)
	private Long productId;

	@Field(name = "name", type = FieldType.Text)
	private String name;

	@Field(name = "brandName", type = FieldType.Text)
	private String brandName;

	@Field(name = "categoryMedium", type = FieldType.Keyword)
	private String categoryMedium;

	@Field(name = "categorySmall", type = FieldType.Keyword)
	private String categorySmall;

	@Field(name = "price", type = FieldType.Integer)
	private Integer price;

	@Field(name = "imageUrl", type = FieldType.Keyword, index = false)
	private String imageUrl;
}