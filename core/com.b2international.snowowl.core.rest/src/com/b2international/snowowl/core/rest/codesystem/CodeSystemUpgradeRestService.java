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
import com.b2international.snowowl.eventbus.IEventBus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
	
	@Operation(
		summary="Synchronize upgrade codesystem with the original codesystem (EXPERIMENTAL)",
		description="Synchronize any changes on the original code system with the upgrade code system."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Upgrade code system synchronized"),
		@ApiResponse(responseCode = "400", description = "Code system could not be synchronized with the downstream code system")
	})
	@PostMapping(value = "/sync/", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void sync(
			@RequestBody
			final CodeSystemUpgradeSyncRestInput body) {
		final String codeSystemId = body.getCodeSystemId();
		final ResourceURI source = body.getSource();
		
		final CodeSystem codeSystem = CodeSystemRequests.prepareSearchCodeSystem()
			.filterById(codeSystemId)
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES)
			.first()
			.orElseThrow(() -> new NotFoundException("Code System", codeSystemId));
		
		 CodeSystemRequests.prepareUpgradeSynchronization(codeSystem.getResourceURI(), source)
			.buildAsync()
			.execute(getBus());
	}
	
	@Operation(
		summary="Complete a codesystem upgrade (EXPERIMENTAL)",
		description="Completes the upgrade process of a Code System to the newer extensionOf Code System dependency."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "Code system upgrade completed"),
		@ApiResponse(responseCode = "400", description = "Code System upgrade cannot be completed")
	})
	@PostMapping("/{id}/complete")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void complete(
			@Parameter(description = "Code System identifier", required = true)
			@PathVariable("id") 
			final String codeSystemId) {
		
		CodeSystemRequests.prepareSearchCodeSystem()
			.filterById(codeSystemId)
			.buildAsync()
			.execute(getBus())
			.getSync(1, TimeUnit.MINUTES)
			.first()
			.orElseThrow(() -> new NotFoundException("Code System", codeSystemId));
		
		CodeSystemRequests.prepareComplete(codeSystemId)
			.buildAsync()
			.execute(getBus());		
	}
}
