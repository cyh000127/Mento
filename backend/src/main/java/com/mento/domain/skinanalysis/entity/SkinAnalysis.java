package com.mento.domain.skinanalysis.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.mento.common.entity.BaseEntity;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto.SkinDetails;
import com.mento.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "skin_anlyses")
public class SkinAnalysis extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "skin_analysis_id")
	private Long id;

	@OneToOne(mappedBy = "skinAnalysis", fetch = FetchType.LAZY)
	private User user;

	@Column(name = "total_score")
	private Integer totalScore;

	@Column(name = "total_grade")
	private Integer totalGrade;

	@Column(name = "skin_type_summary")
	private String skinTypeSummary;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "analysis_details", columnDefinition = "json")
	private SkinDetails analysisDetails;

}