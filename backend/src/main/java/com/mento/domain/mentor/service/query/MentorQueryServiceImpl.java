package com.mento.domain.mentor.service.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.mentor.repository.MentorTypeRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorQueryServiceImpl implements MentorTypeQueryService {

	private final MentorTypeRepository mentorTypeRepository;

	@Override
	public List<MentorType> findAll() {
		List<MentorType> mentorTypes = mentorTypeRepository.findAll();
		log.info("[MentorType] 멘토 타입 조회 완료: {}건", mentorTypes.size());
		return mentorTypes;
	}
}
