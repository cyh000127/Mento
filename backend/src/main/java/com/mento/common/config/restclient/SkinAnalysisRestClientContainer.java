package com.mento.common.config.restclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SkinAnalysisRestClientContainer {

	public final RestClient restClient;

	public SkinAnalysisRestClientContainer(@Qualifier("skinAnalysisRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	public RestClient get() {
		return restClient;
	}

}
