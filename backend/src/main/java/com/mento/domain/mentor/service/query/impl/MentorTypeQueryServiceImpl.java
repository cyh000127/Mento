package com.mento.domain.mentor.service.query.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.mentor.exception.MentortTypeException;
import com.mento.domain.mentor.repository.MentorTypeRepository;
import com.mento.domain.mentor.service.query.MentorTypeQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorTypeQueryServiceImpl implements MentorTypeQueryService {

	private final MentorTypeRepository mentorTypeRepository;

	@Override
	public MentorType findById(final Long id) {
		return mentorTypeRepository.findById(id)
			.orElseThrow(() -> new MentortTypeException(ErrorCode.MENTOR_TYPE_NOT_FOUND));
	}

	@Override
	public List<MentorType> findAll() {
		List<MentorType> mentorTypes = mentorTypeRepository.findAll();
		log.info("[MentorType] 멘토 타입 조회 완료: {}건", mentorTypes.size());
		return mentorTypes;
	}
}
