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
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.core.uri.CodeSystemURI;

import io.swagger.annotations.*;

/**
 * @since 7.17
 */
@Api(value = "CodeSystemUpgrade", description="Code System Upgrade", tags = { "code-system-upgrade" })
@RestController
@RequestMapping(value = "/upgrade") 
public class CodeSystemUpgradeRestService extends AbstractRestService {

	@ApiOperation(
			value="Start a Code System dependency upgrade (EXPERIMENTAL)",
			notes="Starts the upgrade process of a Code System to a newer extensionOf Code System dependency than the current extensionOf."
			)
	@ApiResponses({
		@ApiResponse(code = 201, message = "Upgrade code system created", response = Void.class),
		@ApiResponse(code = 400, message = "Code System cannot be upgraded", response = RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public Promise<ResponseEntity<Void>> upgrade(
			@RequestBody
			final CodeSystemUpgradeRestInput body) {
		final CodeSystem codeSystem = CodeSystemRequests.prepareSearchAllCodeSystems()
				.filterById(body.getUpgradeOf().getCodeSystem())
				.buildAsync()
				.execute(getBus())
				.getSync(1, TimeUnit.MINUTES)
				.first()
				.orElseThrow(() -> new NotFoundException("Code System", body.getUpgradeOf().getCodeSystem()));

		final UriComponentsBuilder uriBuilder = createURIBuilder();

		return CodeSystemRequests.prepareUpgrade(body.getUpgradeOf(), body.getExtensionOf())
				.setCodeSystemId(body.getCodeSystemId())
				.build(codeSystem.getRepositoryId())
				.execute(getBus())
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
