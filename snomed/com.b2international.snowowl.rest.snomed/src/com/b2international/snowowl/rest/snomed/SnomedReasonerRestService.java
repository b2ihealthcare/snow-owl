/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.rest.snomed;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.rest.AbstractRestService;
import com.b2international.snowowl.rest.util.DeferredResults;
import com.b2international.snowowl.snomed.reasoner.domain.ReasonerExtensions;
import com.b2international.snowowl.snomed.reasoner.request.ClassificationRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 6.16
 */
@Tag(name = "reasoners", description="Reasoners")
@Controller
@RequestMapping(value="#{snomedApiBaseUrl}/reasoners", produces={ AbstractRestService.JSON_MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE })
public class SnomedReasonerRestService {
	
	@Operation(
		summary="Retrieve reasoner id-s from the running Snow Owl instance.", 
		description="Retrieve reasoner id-s from the running Snow Owl instance.")
//	@ApiResponses({
//		@ApiResponse(code = 200, message = "OK"),
//	})
	@GetMapping
	public @ResponseBody DeferredResult<ReasonerExtensions> getReasoners() {
		return DeferredResults.wrap(ClassificationRequests.prepareSearchReasonerExtensions()
			.buildAsync()
			.execute(ApplicationContext.getServiceForClass(IEventBus.class)));
	}

}
