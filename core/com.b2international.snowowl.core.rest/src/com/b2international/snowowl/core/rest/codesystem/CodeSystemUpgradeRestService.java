/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.codesystem;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.eventbus.IEventBus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @since 7.17
 */
@Tag(description = "CodeSystems", name = "codesystems")
@RestController
@RequestMapping(value = "/upgrade") 
public class CodeSystemUpgradeRestService extends AbstractRestService {

	@Operation(
		summary="Start a Code System dependency upgrade (EXPERIMENTAL)",
		description="Starts the upgrade process of a Code System to a newer extensionOf Code System dependency than the current extensionOf."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Upgrade created"),
		@ApiResponse(responseCode = "400", description = "Code System cannot be upgraded")
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public Promise<ResponseEntity<Void>> upgrade(
			@RequestBody
			final UpgradeRestInput body) {
		
		final UriComponentsBuilder uriBuilder = createURIBuilder();
		final ResourceURI upgradeOf = new ResourceURI(body.getUpgradeOf());
		final IEventBus bus = getBus();
		return CodeSystemRequests.prepareGetCodeSystem(upgradeOf.getResourceId()) // TODO move this to generic resource controller
			.buildAsync()
			.execute(bus)
			.thenWith(codeSystem -> {
				return CodeSystemRequests.prepareUpgrade(upgradeOf, new ResourceURI(body.getExtensionOf()))
						.setResourceId(body.getCodeSystemId())
						.buildAsync()
						.execute(bus);
			})
			.then(upgradeCodeSystemId -> {
				return ResponseEntity.created(uriBuilder.pathSegment(upgradeCodeSystemId).build().toUri()).build();
			});
	}
	
	@ApiOperation(
			value="Synchronize upgrade codesystem with the original codesystem (EXPERIMENTAL)",
			notes="Synchronize any changes on the original code system with the upgrade code system."
			)
	@ApiResponses({
		@ApiResponse(code = 204, message = "Upgrade code system synchronized"),
		@ApiResponse(code = 400, message = "Code system could not be synchronized with the downstream code system", response = RestApiError.class)
	})
	@PostMapping(value = "/sync/", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void sync(
			@RequestBody
			final CodeSystemUpgradeSyncRestInput body) {
		final String codeSystemId = body.getCodeSystemId();
		final CodeSystemURI source = body.getSource();
		
		final CodeSystem codeSystem = CodeSystemRequests.prepareSearchAllCodeSystems()
				.filterById(codeSystemId)
				.buildAsync()
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES)
				.first()
				.orElseThrow(() -> new NotFoundException("Code System", codeSystemId));
		
		 CodeSystemRequests.prepareUpgradeSynchronization(codeSystem.getCodeSystemURI(), source)
				.build(codeSystem.getRepositoryId())
				.execute(getBus());
	}
	
	@ApiOperation(
			value="Complete a codesystem upgrade (EXPERIMENTAL)",
			notes="Completes the upgrade process of a Code System to the newer extensionOf Code System dependency."
			)
	@ApiResponses({
		@ApiResponse(code = 204, message = "Code system upgrade completed"),
		@ApiResponse(code = 400, message = "Code System upgrade cannot be completed", response = RestApiError.class)
	})
	@PostMapping("/{id}/complete")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void complete(
			@ApiParam(value = "Code System identifier", required = true)
			@PathVariable("id") 
			final String codeSystemId) {
		final CodeSystem codeSystem = CodeSystemRequests.prepareSearchAllCodeSystems()
				.filterById(codeSystemId)
				.buildAsync()
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES)
				.first()
				.orElseThrow(() -> new NotFoundException("Code System", codeSystemId));
		
		CodeSystemRequests.prepareComplete(codeSystemId)
				.build(codeSystem.getRepositoryId())
				.execute(getBus());		
	}
	
}
