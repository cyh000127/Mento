package com.mento.domain.mentor.service.query;

import java.util.List;

import com.mento.domain.mentor.entity.MentorType;

public interface MentorTypeQueryService {

	MentorType findById(Long id);

	List<MentorType> findAll();
}