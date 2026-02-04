package com.mento.domain.skin_analysis.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.mento.common.entity.BaseEntity;
import com.mento.domain.skin_analysis.dto.response.SkinAnalysisDetailResDto.SkinDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "skin_anlyses")
public class SkinAnalysis extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "skin_analysis_id")
	private Long id;

	@Column(name = "user_id")
	private Long userId;

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