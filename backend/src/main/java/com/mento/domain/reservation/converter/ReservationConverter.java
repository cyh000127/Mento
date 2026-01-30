package com.mento.domain.reservation.converter;

import java.util.List;

import org.springframework.data.domain.Page;

import com.mento.common.file.dto.FileInfo;
import com.mento.domain.mentor.converter.MentorConverter;
import com.mento.domain.reservation.dto.response.MediaUploadResDto;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.dto.response.ReservationDraftResDto;
import com.mento.domain.reservation.dto.response.ReservationPageInfoDto;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.timetable.converter.TimetableConverter;
import com.mento.domain.user.converter.UserConverter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReservationConverter {

	public MediaUploadResDto toMediaUploadResDto(final Long reservationId, final List<FileInfo> uploadedFiles) {
		return MediaUploadResDto.builder()
			.reservationId(reservationId)
			.uploadedFiles(uploadedFiles)
			.build();
	}

	public ReservationDetailResDto toReservationDetailResDto(final Reservation reservation) {
		return ReservationDetailResDto.builder()
			.reservationId(reservation.getId())
			.userInfo(UserConverter.toUserInfoDto(reservation.getUser()))
			.mentorInfo(MentorConverter.toMentorInfoDto(reservation.getMentor()))
			.mentorTypeInfo(MentorConverter.toMentorTypeInfoDto(reservation.getSlot().getMentorType()))
			.timetableId(reservation.getSlot().getTimetable().getId())
			.scheduledDate(reservation.getSlot().getTimetable().getScheduledDate())
			.scheduledTime(reservation.getSlot().getTimetable().getScheduledTime().toString())
			.reservationStatus(reservation.getStatus().name())
			.createdAt(reservation.getCreatedAt())
			.updatedAt(reservation.getUpdatedAt())
			.build();
	}

	public Page<ReservationPageInfoDto> toReservationPageResDto(final Page<Reservation> reservations) {
		return reservations.map(ReservationConverter::toReservationPageInfoDto);
	}

	public ReservationPageInfoDto toReservationPageInfoDto(final Reservation reservation) {
		return ReservationPageInfoDto.builder()
			.reservationId(reservation.getId())
			.scheduledDate(reservation.getSlot().getTimetable().getScheduledDate())
			.mentorType(MentorConverter.toMentorTypeInfoDto(reservation.getSlot().getMentorType()))
			.status(reservation.getStatus())
			.build();
	}

	public static ReservationDraftResDto toReservationDraftResDto(final Reservation reservation) {
		return ReservationDraftResDto.builder()
			.reservationId(reservation.getId())
			.timetableSlotInfoDto(TimetableConverter.toTimetableSlotInfoDto(reservation.getSlot()))
			.status(reservation.getStatus())
			.expiresAt(reservation.getExpiresAt())
			.build();
	}
}