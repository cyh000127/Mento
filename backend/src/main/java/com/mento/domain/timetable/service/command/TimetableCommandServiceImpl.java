package com.mento.domain.timetable.service.command;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.repository.TimetableRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableCommandServiceImpl implements TimetableCommandService {

	private final TimetableRepository timetableRepository;

	@Override
	public List<Timetable> saveAll(final List<Timetable> timetableList) {
		List<Timetable> timetables = timetableRepository.saveAll(timetableList);
		log.info("[Timetable] 타임테이블 생성 완료 {개수: {}}", timetableList.size());
		return timetables;
	}
}