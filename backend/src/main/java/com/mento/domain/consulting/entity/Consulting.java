package com.mento.domain.consulting.entity;

import java.util.List;

import com.mento.common.entity.BaseEntity;
import com.mento.domain.consulting.converter.ChatLogListConverter;
import com.mento.domain.consulting.vo.ChatLogEntryVo;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "consultings")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Consulting extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "room_id", nullable = false, unique = true)
	private String roomId;

	@Column(name = "chat_logs", columnDefinition = "longtext")
	@Convert(converter = ChatLogListConverter.class)
	private List<ChatLogEntryVo> chatLogs;

	public void updateChatLogs(final List<ChatLogEntryVo> chatLogs) {
		this.chatLogs = chatLogs;
	}
}
