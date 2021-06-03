/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.domain.ListCollectionResource;
import com.google.common.collect.ImmutableMap;

import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;

/**
 * @since 7.3
 */
@ApiIgnore
@RestController
@RequestMapping(value = "/apis", produces = { AbstractRestService.JSON_MEDIA_TYPE })
public class SnowOwlApiRestService extends AbstractRestService {

	@Autowired
	private DocumentationCache documentCache;
	
	@GetMapping
	public CollectionResource<Map<String, Object>> get() {
		List<Map<String, Object>> items = documentCache.all().values().stream()
				.sorted((d1, d2) -> d1.getResourceListing().getInfo().getTitle().compareTo(d2.getResourceListing().getInfo().getTitle()))
				.map(this::toApiDoc)
				.collect(Collectors.toList());
		return ListCollectionResource.of(items);
	}
	
	private Map<String, Object> toApiDoc(Documentation doc) {
		return ImmutableMap.of(
			"id", doc.getGroupName(),
			"title", doc.getResourceListing().getInfo().getTitle()
		);
	}
	
}
