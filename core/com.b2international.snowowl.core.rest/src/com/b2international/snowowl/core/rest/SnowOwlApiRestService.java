/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.ListCollectionResource;

import io.swagger.v3.oas.annotations.Hidden;

/**
 * @since 7.3
 */
@Hidden
@RestController
@RequestMapping(value = "/apis", produces = { AbstractRestService.JSON_MEDIA_TYPE })
public class SnowOwlApiRestService extends AbstractRestService {

	@Autowired
	private ApplicationContext ctx;
	
	@GetMapping
	public CollectionResource<Map<String, Object>> get() {
		List<Map<String, Object>> items = ctx.getBeansOfType(GroupedOpenApi.class)
			.values()
			.stream()
			.sorted((a1, a2) -> a1.getGroup().compareTo(a2.getGroup()))
			.map(this::toApiDoc)
			.collect(Collectors.toList());
		return ListCollectionResource.of(items);
	}
	
	private Map<String, Object> toApiDoc(GroupedOpenApi doc) {
		return Map.of(
			"id", doc.getGroup(),
			"title", doc.getGroup().toUpperCase() + " API"
		);
	}
	
}
