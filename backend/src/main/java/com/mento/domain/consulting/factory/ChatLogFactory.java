package com.mento.domain.consulting.factory;

import org.springframework.stereotype.Component;

import com.mento.domain.consulting.dto.request.ConsultingChatLogSaveReqDto;
import com.mento.domain.consulting.vo.ChatLogEntryVo;

@Component
public class ChatLogFactory {

	public ChatLogEntryVo createChatLogEntry(final ConsultingChatLogSaveReqDto reqDto) {
		return ChatLogEntryVo.builder()
			.role(reqDto.role())
			.content(reqDto.content())
			.build();
	}
}
