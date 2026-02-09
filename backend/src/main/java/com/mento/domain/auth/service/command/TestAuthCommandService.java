package com.mento.domain.auth.service.command;

import com.mento.common.auth.dto.Token;
import com.mento.domain.auth.dto.request.TestLoginReqDto;

import jakarta.servlet.http.HttpServletResponse;

public interface TestAuthCommandService {
	Token login(TestLoginReqDto reqDto, HttpServletResponse response);
}
