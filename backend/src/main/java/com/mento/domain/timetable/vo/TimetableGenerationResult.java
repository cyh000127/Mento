package com.mento.domain.timetable.vo;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.mento.domain.timetable.entity.Timetable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TimetableGenerationResult {

	private final List<Timetable> timetables;

	public static TimetableGenerationResult of(final List<Timetable> timetables) {
		return new TimetableGenerationResult(timetables);
	}

	public static TimetableGenerationResult empty() {
		return new TimetableGenerationResult(List.of());
	}

	public boolean isEmpty() {
		return CollectionUtils.isEmpty(timetables);
	}

	public int size() {
		return timetables.size();
	}
}
