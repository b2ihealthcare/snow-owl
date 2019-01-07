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
package com.b2international.snowowl.api.rest.codesystem;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.b2international.commons.exceptions.ApiError;
import com.b2international.commons.exceptions.ApiErrorException;
import com.b2international.snowowl.api.rest.codesystem.domain.CodeSystem;
import com.b2international.snowowl.api.rest.codesystem.domain.CodeSystemVersion;
import com.b2international.snowowl.api.rest.codesystem.domain.CodeSystemVersionProperties;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.remotejobs.RemoteJobEntry;
import com.b2international.snowowl.datastore.request.job.JobRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

/**
 * @since 7.1
 */
public class CodeSystemVersionService {

	private static final Function<CodeSystemVersionEntry, CodeSystemVersion> CODE_SYSTEM_VERSION_CONVERTER = (input) -> {
		final CodeSystemVersion result = new CodeSystemVersion();
		result.setDescription(input.getDescription());
		result.setEffectiveDate(toDate(input.getEffectiveDate()));
		result.setImportDate(toDate(input.getImportDate()));
		result.setLastModificationDate(toDate(input.getLatestUpdateDate()));
		result.setParentBranchPath(input.getParentBranchPath());
		result.setPatched(input.isPatched());
		result.setVersion(input.getVersionId());
		return result;
	};
	
	private static Date toDate(final long timeStamp) {
		return timeStamp >= 0L ? new Date(timeStamp) : null;
	}

	private static final Ordering<CodeSystemVersion> VERSION_ID_ORDERING = Ordering.natural().onResultOf(CodeSystemVersion::getVersion);

	@Autowired
	private CodeSystemService codeSystems;

	/**
	 * Lists all released code system versions for a single code system with the specified short name, if it exists.
	 * 
	 * @param shortName the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * 
	 * @return the requested code system's released versions, ordered by version ID
	 * 
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 */
	public List<CodeSystemVersion> getCodeSystemVersions(final String shortName) {
		checkNotNull(shortName, "Short name may not be null.");
		final CodeSystem codeSystem = codeSystems.getCodeSystemById(shortName);
		final Collection<CodeSystemVersionEntry> versions = getCodeSystemVersions(shortName, codeSystem.getRepositoryUuid()); 
		return toSortedCodeSystemVersionList(versions);
	}

	/**
	 * Retrieves a single released code system version for the specified code system short name and version identifier, if it exists.
	 * 
	 * @param shortName the code system short name to look for, eg. "{@code SNOMEDCT}" (may not be {@code null})
	 * @param version   the code system version identifier to look for, eg. "{@code 2014-07-31}" (may not be {@code null})
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
				.build(codeSystem.getRepositoryUuid())
				.execute(getEventBus())
				.getSync();
		
		final CodeSystemVersionEntry version = Iterables.getOnlyElement(versions, null);
		if (version == null) {
			throw new CodeSystemVersionNotFoundException(versionId);
		} else {
			return CODE_SYSTEM_VERSION_CONVERTER.apply(version);
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
	public CodeSystemVersion createVersion(String shortName, CodeSystemVersionProperties properties) {
		Request<ServiceProvider, Boolean> req = CodeSystemRequests.prepareNewCodeSystemVersion()
				.setCodeSystemShortName(shortName)
				.setVersionId(properties.getVersion())
				.setDescription(properties.getDescription())
				.setEffectiveTime(properties.getEffectiveDate())
				.build();
		
		String jobId = JobRequests.prepareSchedule()
				.setDescription(String.format("Creating version '%s/%s'", shortName, properties.getVersion()))
				.setUser(User.SYSTEM.getUsername())
				.setRequest(req)
				.buildAsync()
				.execute(getEventBus())
				.getSync();
		
		RemoteJobEntry job = null;
		do {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new SnowowlRuntimeException(e);
			}
			
			job = JobRequests.prepareGet(jobId)
					.buildAsync()
					.execute(getEventBus())
					.getSync();
		} while (job == null || !job.isDone());
		
		if (job.isSuccessful()) {
			return getCodeSystemVersionById(shortName, properties.getVersion());
		} else if (!Strings.isNullOrEmpty(job.getResult())) {
			ApiError error = job.getResultAs(ApplicationContext.getServiceForClass(ObjectMapper.class), ApiError.class);
			throw new ApiErrorException(error);
		} else {
			throw new SnowowlRuntimeException("Version creation failed.");
		}
	}

	private Collection<CodeSystemVersionEntry> getCodeSystemVersions(final String shortName, final String repositoryId) {
		return CodeSystemRequests
				.prepareSearchCodeSystemVersion()
				.all()
				.filterByCodeSystemShortName(shortName)
				.build(repositoryId)
				.execute(getEventBus())
				.getSync()
				.getItems();
	}
	
	private IEventBus getEventBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

	private List<CodeSystemVersion> toSortedCodeSystemVersionList(final Collection<CodeSystemVersionEntry> sourceCodeSystemVersions) {
		final Collection<CodeSystemVersion> targetCodeSystemVersions = Collections2.transform(sourceCodeSystemVersions, CODE_SYSTEM_VERSION_CONVERTER);
		return VERSION_ID_ORDERING.immutableSortedCopy(targetCodeSystemVersions);
	}

}
