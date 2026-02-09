package com.mento.domain.mentor.entity;

import java.util.ArrayList;
import java.util.List;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.domain.mentor.exception.MentortTypeException;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "mentor_types")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorType extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "type_id")
	private Long id;

	@Column(name = "type_name", nullable = false, length = 50)
	private String typeName;

	@Column(name = "price", nullable = false)
	private Integer price;

	@Column(name = "description")
	private String description;

	@Builder.Default
	@OneToMany(mappedBy = "mentorType", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<User> users = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "mentorType", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TimetableSlot> slots = new ArrayList<>();

	public void addSlot(final TimetableSlot slot) {
		if (slot == null) {
			throw new MentortTypeException(ErrorCode.MISSING_SLOT);
		}
		this.slots.add(slot);
		if (slot.getMentorType() != this) {
			slot.assignMentorType(this);
		}
	}

	public void removeSlot(final TimetableSlot slot) {
		if (slot == null) {
			return;
		}
		this.slots.remove(slot);
	}

	public void addMentor(final User user) {
		if (user == null) {
			throw new MentortTypeException(ErrorCode.MISSING_MENTOR);
		}
		this.users.add(user);
		if (user.getMentorType() != this) {
			user.assignMentorType(this);
		}
	}
}
