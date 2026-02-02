package com.mento.domain.skin_analysis.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/skin-analysis")
public class SkinAnalysisCommandController {

	@PostMapping
	public void skinAnalyze(List<MultipartFile> files) {
		
	}
}
