package com.b2international.snowowl.bbl.rest;

import com.b2international.snowowl.core.rest.AbstractRestService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @since 7.5
 */
@Api(value = "Metrics", tags = { "metrics" })
@RestController
public class MetricsRestService extends AbstractRestService {

	@Autowired
	private MeterRegistry registry;

	@ApiOperation(
			value = "Retrieve monitoring data about Snow Owl",
			notes = "Retrive monitoring data about Snow Owl which is parsable by a Prometheus monitoring system.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", response = String.class),
	})
	@GetMapping(value = "/metrics", produces = {AbstractRestService.TEXT_MEDIA_TYPE})
	public String getMetrics() {
		if (registry instanceof PrometheusMeterRegistry) {
			return ((PrometheusMeterRegistry) registry).scrape();
		} else {
			return "";
		}
	}
}