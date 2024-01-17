/*
 * Copyright 2022-2023 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.CoreApiConfig;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.4.0
 */
@Tag(description="Administration", name = CoreApiConfig.ADMINISTRATION)
@Controller
@RequestMapping(value = "/toolings") 
public class ConfiguredToolingsRestService extends AbstractRestService {

	@Operation(
		summary = "Provides list of available tooling ids",
		description = "Provides a set of all available toolings ids configured on the server."
	)
	@RequestMapping(method = { RequestMethod.GET }, produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Toolings toolings() {
		return new Toolings(
				TerminologyRegistry.INSTANCE.getTerminologies()
					.stream()
					.filter(t -> !TerminologyRegistry.UNSPECIFIED.equals(t))
					.collect(Collectors.toList()));
	}
}
