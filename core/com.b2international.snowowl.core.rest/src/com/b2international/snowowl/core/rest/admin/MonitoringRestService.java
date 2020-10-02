/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.core.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.rest.AbstractRestService;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 7.0
 */
@Tag(name = "monitoring", description="Monitoring")
@RestController
public class MonitoringRestService extends AbstractRestService {
	
	@Autowired
	private MeterRegistry registry;
	
	@Operation(
		summary = "Retrieve monitoring data about Snow Owl",
		description = "Retrive monitoring data about Snow Owl which is parsable by a Prometheus monitoring system."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@GetMapping(value = "/stats", produces = { AbstractRestService.TEXT_MEDIA_TYPE })
	public String getMetrics() {
		if (registry instanceof PrometheusMeterRegistry) {
			return ((PrometheusMeterRegistry) registry).scrape();
		} else {
			return "";
		}
	}

}
