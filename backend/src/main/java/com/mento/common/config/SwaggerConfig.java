package com.mento.common.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.mento.common.constant.BackDomain;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenApi() {

		SecurityScheme accessTokenAuth = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name("Authorization");

		SecurityRequirement securityRequirement = new SecurityRequirement()
			.addList("accessTokenAuth")
			.addList("refreshTokenAuth");

		List<Server> servers = Arrays.stream(BackDomain.values())
			.map(domain -> new Server()
				.url(domain.getUrl())
				.description(domain.getDescription()))
			.toList();

		return new OpenAPI()
			.info(new Info().title("Mento API")
				.description("Mento API 서버")
				.version("v1.0"))
			.components(new Components()
				.addSecuritySchemes("accessTokenAuth", accessTokenAuth))
			.security(Collections.singletonList(securityRequirement))
			.servers(servers);
	}

	@Bean
	public GroupedOpenApi productionApi() {
		return GroupedOpenApi.builder()
			.group("1. Production APIs")
			.displayName("Production APIs (Requires Auth)")
			.pathsToMatch("/api/**")
			.pathsToExclude("/test/**")
			.build();
	}

	@Bean
	public GroupedOpenApi testApi() {
		return GroupedOpenApi.builder()
			.group("2. Test APIs")
			.displayName("Test APIs (No Auth Required)")
			.pathsToMatch("/test/**")
			.build();
	}

	@Bean
	@Primary
	public SwaggerUiConfigProperties swaggerUiConfigProperties(SwaggerUiConfigProperties props) {
		props.setPersistAuthorization(true);
		return props;
	}
}