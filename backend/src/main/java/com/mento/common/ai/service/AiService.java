package com.mento.common.ai.service;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.io.Resource;

public interface AiService<T> {

	T execute(Resource system, PromptTemplate promptTemplate, BeanOutputConverter<T> converter);
}