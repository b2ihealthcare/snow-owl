/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.*;

import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedMrcmTypeRequest.ATTRIBUTE_TYPE;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 8.8.0
 */
@Tag(description="Rules", name = "rules")
@RestController
public class SnomedMrcmRestService extends AbstractRestService {
	
	@Operation(
		summary="Retrieve MRCM attribute domain type refset members filtered by domain and attribute type", 
		description="Retrieve MRCM relationship type rules ")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "400", description = "Bad Request"),
		@ApiResponse(responseCode = "404", description = "Not found"),
	})
	@GetMapping(value = "/{path:**}/types", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<SnomedReferenceSetMembers> getApplicableTypes(
			@Parameter(description = "The resource path", required = true)
			@PathVariable(name ="path")
			final String path,
			
			@Parameter(description = "The attribute types to include in the results", schema = @Schema(allowableValues = "all,object,data", defaultValue = "all"))
			@RequestParam(name = "attributeType", defaultValue = "all")
			final String attributeType,
			
			@Parameter(name = "selfIds", description = "Set of concept ids that might form the domain of the rule", required = false)
			@RequestParam(name = "selfIds", required = false)
			final Set<String> selfIds,
			
			@Parameter(name = "parentIds", description = "Set of parent/ancestor ids that might form the domain of the rule", required = false)
			@RequestParam(name = "parentIds", required = false)
			final Set<String> parentIds,
			
			@Parameter(name = "refsetIds", description = "Set of reference set ids that might form the domain of the rule", required = false)
			@RequestParam(name = "refsetIds", required = false)
			final Set<String> refsetIds,
			
			@Parameter(name = "moduleIds", description = "List of applicable modules refining the scope of returned rules", required = false)
			@RequestParam(name = "moduleIds", required = false)
			final List<String> moduleIds
			) {
		
		return SnomedRequests.prepareGetMrcmTypeRules()
			.setAttributeType(ATTRIBUTE_TYPE.getByNameIgnoreCase(attributeType))
			.setModuleIds(moduleIds)
			.setParentIds(parentIds)
			.setRefSetIds(refsetIds)
			.setSelfIds(selfIds)
			.build(path)
			.execute(getBus());
	}
	
	@Operation(
			summary="Retrieve MRCM attribute domain range refset members filtered by domain and attribute type", 
			description="Retrieve MRCM relationship range rules ")
		@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not found"),
		})
		@GetMapping(value = "/{path:**}/ranges", produces = { AbstractRestService.JSON_MEDIA_TYPE })
		public @ResponseBody Promise<SnomedReferenceSetMembers> getApplicableRanges(
				@Parameter(description = "The resource path", required = true)
				@PathVariable(name ="path")
				final String path,
				
				@Parameter(description = "The attribute types to include in the results", schema = @Schema(allowableValues = "all,object,data", defaultValue = "all"))
				@RequestParam(name = "attributeType", defaultValue = "all")
				final String attributeType,
				
				@Parameter(name = "selfIds", description = "Set of concept ids that might form the domain of the rule", required = false)
				@RequestParam(name = "selfIds", required = false)
				final Set<String> selfIds,
				
				@Parameter(name = "parentIds", description = "Set of parent/ancestor ids that might form the domain of the rule", required = false)
				@RequestParam(name = "parentIds", required = false)
				final Set<String> parentIds,
				
				@Parameter(name = "refsetIds", description = "Set of reference set ids that might form the domain of the rule", required = false)
				@RequestParam(name = "refsetIds", required = false)
				final Set<String> refsetIds,
				
				@Parameter(name = "moduleIds", description = "List of applicable modules refining the scope of returned rules", required = false)
				@RequestParam(name = "moduleIds", required = false)
				final List<String> moduleIds
				) {
			
			return SnomedRequests.prepareGetMrcmRangeRules()
				.setAttributeType(ATTRIBUTE_TYPE.getByNameIgnoreCase(attributeType))
				.setModuleIds(moduleIds)
				.setParentIds(parentIds)
				.setRefSetIds(refsetIds)
				.setSelfIds(selfIds)
				.build(path)
				.execute(getBus());
		}

}
