package com.mento.domain.mentor.service.query;

import com.mento.domain.mentor.entity.Mentor;

public interface MentorQueryService {

	Mentor findById(Long id);

	Mentor findRandomMentorByTypeId(Long id);
}