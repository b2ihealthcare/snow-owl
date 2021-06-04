/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.validation.ApiValidation;
import com.b2international.snowowl.core.Repositories;
import com.b2international.snowowl.core.RepositoryInfo;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.request.SearchResourceRequest.Sort;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.core.rest.AbstractRestService;
import com.b2international.snowowl.core.rest.RestApiError;
import com.b2international.snowowl.eventbus.IEventBus;

import io.swagger.annotations.*;

/**
 * @since 1.0
 */
@Api(value = "CodeSystem", description="Code Systems", tags = { "code-systems" })
@RestController
@RequestMapping(value = "/codesystems") 
public class CodeSystemRestService extends AbstractRestService {

	@Autowired
	private CodeSystemService codeSystemService;
	
	@ApiOperation(
		value="Retrieve Code Systems", 
		notes="Returns a collection resource containing all/filtered registered Code Systems."
			+ "<p>Results are always sorted by repositoryUuid first, sort keys only apply per repository."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; availableUpgrades() &ndash; a list of possible code system URIs that can be used as an 'extensionOf' property"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = CodeSystems.class),
		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@GetMapping(produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public Promise<CodeSystems> searchByGet(final CodeSystemRestSearch params) {
		checkArgument(params.getSearchAfter() == null, "Parameter 'searchAfter' is not supported for code system search.");
		
		final IEventBus bus = getBus();
		
		return RepositoryRequests.prepareSearch()
			.all()
			.buildAsync()
			.execute(bus)
			.thenWith(repos -> searchByGet(params, repos, bus));
	}
	
	private Promise<CodeSystems> searchByGet(CodeSystemRestSearch params, Repositories repos, IEventBus bus) {
		final List<Promise<CodeSystems>> codeSystemsByRepository = repos.stream()
			.map(RepositoryInfo::id)
			.map(id -> searchByGet(params, id, bus))
			.collect(Collectors.toList());
		
		if (codeSystemsByRepository.isEmpty()) {
			return Promise.immediate(new CodeSystems(List.of(), null, params.getLimit(), 0));
		}
		
		return Promise.all(codeSystemsByRepository)
			.then(results -> {
				final List<CodeSystem> allCodeSystems = results.stream()
					.flatMap(r -> ((CodeSystems) r).stream())
					// XXX: search-time sort order within a repository should be preserved by the sorter below
					.sorted(Comparator.comparing(CodeSystem::getRepositoryId))
					.limit(params.getLimit())
					.collect(Collectors.toList());
				
				final int total = results.stream()
					.mapToInt(r -> ((CodeSystems) r).getTotal())
					.sum();
				
				return new CodeSystems(allCodeSystems, null, params.getLimit(), total);
			});
	}
	
	private Promise<CodeSystems> searchByGet(CodeSystemRestSearch params, String repositoryId, IEventBus bus) {
		List<Sort> sortBy = extractSortFields(params.getSort());
		if (sortBy.isEmpty()) {
			sortBy = List.of(SortField.ascending(CodeSystemEntry.Fields.SHORT_NAME));
		}

		return CodeSystemRequests.prepareSearchCodeSystem()
				.filterByIds(params.getId())
				.setLimit(params.getLimit())
				.setExpand(params.getExpand())
				.sortBy(sortBy)
				// .setSearchAfter(...) is not applied; we are searching in multiple repositories
				.build(repositoryId)
				.execute(bus);
	}

	@ApiOperation(
		value="Retrieve Code Systems", 
		notes="Returns a collection resource containing all/filtered registered Code Systems."
		    + "<p>Results are always sorted by repositoryUuid first, sort keys only apply per repository."
			+ "<p>The following properties can be expanded:"
			+ "<p>"
			+ "&bull; availableUpgrades() &ndash; a list of possible code system URIs that can be used as an 'extensionOf' property"
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK", response = CodeSystems.class),
		@ApiResponse(code = 400, message = "Invalid search config", response = RestApiError.class),
		@ApiResponse(code = 404, message = "Branch not found", response = RestApiError.class)
	})
	@PostMapping(value="/search", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public @ResponseBody Promise<CodeSystems> searchByPost(
			@RequestBody(required = false)
			final CodeSystemRestSearch params) {
		return searchByGet(params);
	}

	@ApiOperation(
			value="Retrieve code system by short name or OID",
			notes="Returns generic information about a single code system with the specified short name or OID.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 404, message = "Code system not found", response = RestApiError.class)
	})
	@GetMapping(value = "/{shortNameOrOid}", produces = { AbstractRestService.JSON_MEDIA_TYPE })
	public CodeSystem read(
			@ApiParam(value="The code system identifier (short name or OID)")
			@PathVariable(value="shortNameOrOid") final String shortNameOrOId) {
		return codeSystemService.getCodeSystemById(shortNameOrOId);
	}
	
	@ApiOperation(
			value="Create a code system",
			notes="Create a new Code System with the given parameters")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Created", response = Void.class),
		@ApiResponse(code = 400, message = "Code System already exists in the system", response = RestApiError.class)
	})
	@PostMapping(consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Void> create(
			@RequestBody
			final CodeSystem codeSystem,
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {

		ApiValidation.checkInput(codeSystem);
		
		final String commitComment = String.format("Created new Code System %s", codeSystem.getShortName());
		
		if (codeSystem.getUpgradeOf() != null) {
			throw new BadRequestException("'upgradeOf' property cannot be set through code system create API");
		}
		
		final String shortName = CodeSystemRequests
				.prepareNewCodeSystem()
				.setBranchPath(codeSystem.getBranchPath())
				.setCitation(codeSystem.getCitation())
				.setIconPath(codeSystem.getIconPath())
				.setLanguage(codeSystem.getPrimaryLanguage())
				.setLink(codeSystem.getOrganizationLink())
				.setName(codeSystem.getName())
				.setOid(codeSystem.getOid())
				.setRepositoryId(codeSystem.getRepositoryId())
				.setShortName(codeSystem.getShortName())
				.setTerminologyId(codeSystem.getTerminologyId())
				.setExtensionOf(codeSystem.getExtensionOf())
				.setLocales(codeSystem.getLocales())
				.setAdditionalProperties(codeSystem.getAdditionalProperties())
				.commit()
				.setAuthor(author)
				.setCommitComment(commitComment)
				.build(codeSystem.getRepositoryId(), IBranchPath.MAIN_BRANCH)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES)
				.getResultAs(String.class);
		
		return ResponseEntity.created(getResourceLocationURI(shortName)).build();
	}
	
	@ApiOperation(
			value="Update a code system",
			notes="Update a Code System with the given parameters")
	@ApiResponses({
		@ApiResponse(code = 204, message = "No content", response = Void.class),
		@ApiResponse(code = 400, message = "Code System cannot be updated", response = RestApiError.class)
	})
	@PutMapping(value = "/{shortNameOrOid}", consumes = { AbstractRestService.JSON_MEDIA_TYPE })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(
			@ApiParam(value="The code system identifier (short name or OID)")
			@PathVariable(value="shortNameOrOid") 
			final String shortNameOrOId,
			
			@RequestBody
			final CodeSystem codeSystem,
			
			@RequestHeader(value = X_AUTHOR, required = false)
			final String author) {
		validateUpdateInput(shortNameOrOId, codeSystem.getRepositoryId());
		final String commitComment = String.format("Updated Code System %s", shortNameOrOId);
		
		if (codeSystem.getUpgradeOf() != null) {
			LoggerFactory.getLogger(getClass()).warn("'upgradeOf' property update support is only present in version 7.x and will be dropped with version 8.0.");
		}
		
		CodeSystemRequests
				.prepareUpdateCodeSystem(shortNameOrOId)
				.setName(codeSystem.getName())
				.setBranchPath(codeSystem.getBranchPath())
				.setCitation(codeSystem.getCitation())
				.setIconPath(codeSystem.getIconPath())
				.setLanguage(codeSystem.getPrimaryLanguage())
				.setLink(codeSystem.getOrganizationLink())
				.setLocales(codeSystem.getLocales())
				.setAdditionalProperties(codeSystem.getAdditionalProperties())
				.setExtensionOf(codeSystem.getExtensionOf())
				.build(codeSystem.getRepositoryId(), IBranchPath.MAIN_BRANCH, author, commitComment)
				.execute(getBus())
				.getSync(COMMIT_TIMEOUT, TimeUnit.MINUTES);
	}
	
	private void validateUpdateInput(final String shortNameOrOId, final String repositoryUuid) {
		if (StringUtils.isEmpty(shortNameOrOId)) {
			throw new BadRequestException("Unique ID cannot be empty for Code System update.");
		} else if (StringUtils.isEmpty(repositoryUuid)) {
			throw new BadRequestException("Repository ID cannot be empty for Code System update.");
		}
	}
}
