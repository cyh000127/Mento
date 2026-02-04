package com.mento.common.config.restclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KakaopayRestClientContainer {

	private final RestClient restClient;

	public KakaopayRestClientContainer(@Qualifier("kakaopayRestClientContainer") RestClient restClient) {
		this.restClient = restClient;
	}

	public RestClient get() {
		return restClient;
	}

}
