/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.ApiErrorException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystemVersion;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionProperties;
import com.b2international.snowowl.core.codesystem.CodeSystemVersions;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.core.jobs.JobRequests;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.request.SearchResourceRequest.SortField;
import com.b2international.snowowl.eventbus.IEventBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.inject.Provider;

/**
 * @since 7.1
 */
@Component
public class CodeSystemVersionService {

	@Autowired
	private CodeSystemService codeSystems;
	
	@Autowired
	private Provider<IEventBus> bus;

	/**
	 * Lists all released code system versions for a single code system with the specified short name, if it exists.
	 * 
	 * @param shortName the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * 
	 * @return the requested code system's released versions, ordered by version ID
	 * 
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 */
	public CodeSystemVersions getCodeSystemVersions(final String shortName) {
		checkNotNull(shortName, "Short name may not be null.");
		final CodeSystem codeSystem = codeSystems.getCodeSystemById(shortName);
		return getCodeSystemVersions(shortName, codeSystem.getRepositoryId()); 
	}

	/**
	 * Retrieves a single released code system version for the specified code system short name and version identifier, if it exists.
	 * 
	 * @param shortName the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * @param versionId   the code system version identifier to look for, eg. "{@code 2014-07-31}" (may not be {@code null})
	 * 
	 * @return the requested code system version
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 */
	public CodeSystemVersion getCodeSystemVersionById(final String shortName, final String versionId) {
		checkNotNull(shortName, "Short name may not be null.");
		checkNotNull(versionId, "Version identifier may not be null.");
		final CodeSystem codeSystem = codeSystems.getCodeSystemById(shortName);
		
		final CodeSystemVersions versions = CodeSystemRequests
				.prepareSearchCodeSystemVersion()
				.all()
				.filterByCodeSystemShortName(shortName)
				.filterByVersionId(versionId)
				.build(codeSystem.getRepositoryId())
				.execute(bus.get())
				.getSync(1, TimeUnit.MINUTES);
		
		final CodeSystemVersion version = Iterables.getOnlyElement(versions, null);
		if (version == null) {
			throw new CodeSystemVersionNotFoundException(versionId);
		} else {
			return version;
		}
	}

	/**
	 * Creates a new version in terminology denoted by the given shortName parameter using the given {@link ICodeSystemVersionProperties} as base
	 * properties.
	 * 
	 * @param shortName  the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * @param properties the base properties of the code system version to create
	 * 
	 * @return the newly created code system version, as returned by {@link #getCodeSystemVersionById(String, String)}
	 */
	public CodeSystemVersion createVersion(String shortName, CodeSystemVersionProperties properties, boolean force) {
		String jobId = CodeSystemRequests.prepareNewCodeSystemVersion()
				.setCodeSystemShortName(shortName)
				.setVersionId(properties.getVersion())
				.setDescription(properties.getDescription())
				.setEffectiveTime(properties.getEffectiveDate())
				.setForce(force)
				.buildAsync()
				.runAsJobWithRestart(CodeSystemRequests.versionJobKey(shortName), String.format("Creating version '%s/%s'", shortName, properties.getVersion()))
				.execute(bus.get())
				.getSync(1, TimeUnit.MINUTES);
		
		RemoteJobEntry job = JobRequests.waitForJob(bus.get(), jobId, 500);
		
		if (job.isSuccessful()) {
			return getCodeSystemVersionById(shortName, properties.getVersion());
		} else if (!Strings.isNullOrEmpty(job.getResult())) {
			ApiError error = job.getResultAs(ApplicationContext.getServiceForClass(ObjectMapper.class), ApiError.class);
			throw new ApiErrorException(error);
		} else {
			throw new SnowowlRuntimeException("Version creation failed.");
		}
	}

	private CodeSystemVersions getCodeSystemVersions(final String shortName, final String repositoryId) {
		return CodeSystemRequests
				.prepareSearchCodeSystemVersion()
				.all()
				.filterByCodeSystemShortName(shortName)
				.sortBy(SortField.ascending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build(repositoryId)
				.execute(bus.get())
				.getSync(1, TimeUnit.MINUTES);
	}
	
}
