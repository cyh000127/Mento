package com.mento.domain.auth.service.command;

import com.mento.common.auth.dto.Token;
import com.mento.domain.auth.dto.request.MentorLoginReqDto;

public interface MentorAuthCommandService {
	Token login(MentorLoginReqDto reqDto);
}
