package com.mento.domain.timetable.service.command.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.repository.TimetableSlotRepository;
import com.mento.domain.timetable.service.command.TimetableSlotCommandService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableSlotCommandServiceImpl implements TimetableSlotCommandService {

	private final TimetableSlotRepository timetableSlotRepository;

	@Override
	public List<TimetableSlot> saveAll(final List<TimetableSlot> slots) {
		List<TimetableSlot> savedSlots = timetableSlotRepository.saveAll(slots);
		log.info("[TimetableSlot] 슬롯 저장 완료 {개수: {}}", savedSlots.size());
		return savedSlots;
	}
}
