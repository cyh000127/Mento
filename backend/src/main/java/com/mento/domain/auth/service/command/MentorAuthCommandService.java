package com.mento.domain.auth.service.command;

import com.mento.common.auth.dto.Token;
import com.mento.domain.auth.dto.request.MentorLoginReqDto;

import jakarta.servlet.http.HttpServletResponse;

public interface MentorAuthCommandService {
	Token login(MentorLoginReqDto reqDto, HttpServletResponse response);
}
