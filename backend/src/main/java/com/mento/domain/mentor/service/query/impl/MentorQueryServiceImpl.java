package com.mento.domain.mentor.service.query.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.mentor.entity.Mentor;
import com.mento.domain.mentor.exception.MentorException;
import com.mento.domain.mentor.repository.MentorRepository;
import com.mento.domain.mentor.service.query.MentorQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorQueryServiceImpl implements MentorQueryService {

	private final MentorRepository mentorRepository;

	@Override
	public Mentor findById(final Long id) {
		return mentorRepository.findById(id)
			.orElseThrow(() -> new MentorException(ErrorCode.MENTOR_NOT_FOUND));
	}
}