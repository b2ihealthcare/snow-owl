/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.impl.codesystem;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.b2international.snowowl.api.codesystem.ICodeSystemService;
import com.b2international.snowowl.api.codesystem.ICodeSystemVersionService;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystem;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystemVersion;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystemVersionProperties;
import com.b2international.snowowl.api.impl.codesystem.domain.CodeSystemVersion;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ApiError;
import com.b2international.snowowl.core.exceptions.ApiErrorException;
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

public class CodeSystemVersionServiceImpl implements ICodeSystemVersionService {

	private static final Function<CodeSystemVersionEntry, ICodeSystemVersion> CODE_SYSTEM_VERSION_CONVERTER = (input) -> {
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

	private static final Ordering<ICodeSystemVersion> VERSION_ID_ORDERING = Ordering.natural().onResultOf(new Function<ICodeSystemVersion, String>() {
		@Override
		public String apply(final ICodeSystemVersion input) {
			return input.getVersion();
		}
	});

	@Override
	public List<ICodeSystemVersion> getCodeSystemVersions(final String shortName) {
		checkNotNull(shortName, "Short name may not be null.");
		final ICodeSystem codeSystem = codeSystems.getCodeSystemById(shortName);
		final Collection<CodeSystemVersionEntry> versions = getCodeSystemVersions(shortName, codeSystem.getRepositoryUuid()); 
		return toSortedCodeSystemVersionList(versions);
	}
	
	@Resource
	private ICodeSystemService codeSystems;

	@Override
	public ICodeSystemVersion getCodeSystemVersionById(final String shortName, final String versionId) {
		checkNotNull(shortName, "Short name may not be null.");
		checkNotNull(versionId, "Version identifier may not be null.");
		final ICodeSystem codeSystem = codeSystems.getCodeSystemById(shortName);
		
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
	
	@Override
	public ICodeSystemVersion createVersion(String shortName, ICodeSystemVersionProperties properties) {
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

//	private void configureVersion(final ICodeSystem codeSystem, final ICodeSystemVersionProperties properties,
//			final VersioningService versioningService) {
//		versioningService.configureDescription(properties.getDescription());
//		versioningService.configureParentBranchPath(codeSystem.getBranchPath());
//		versioningService.configureCodeSystemShortName(codeSystem.getShortName());
//		
//		final IStatus dateResult = versioningService.configureEffectiveTime(properties.getEffectiveDate());
//		if (!dateResult.isOK()) {
//			throw new BadRequestException("The specified %s effective time is invalid. %s", properties.getEffectiveDate(), dateResult.getMessage());
//		}
//
//		final IStatus versionResult = versioningService.configureNewVersionId(properties.getVersion(), false);
//		if (!versionResult.isOK()) {
//			throw new AlreadyExistsException("Version", properties.getVersion());
//		}
//		
//		// FIXME remove hard coded SNOMED CT store value, versioning should get the repositoryId from the API
//		final String repositoryId = "snomedStore";
//		
//		final String versionBranch = codeSystem.getBranchPath() + "/" + properties.getVersion();
//		
//		try {
//			RepositoryRequests
//				.branching()
//				.prepareGet(versionBranch)
//				.build(repositoryId)
//				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
//				.getSync();
//			throw new ConflictException("An existing branch with path '%s' conflicts with the specified version identifier.", versionBranch);
//		} catch (NotFoundException expected) {
//			// fall-through
//		}
//	}

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

	private List<ICodeSystemVersion> toSortedCodeSystemVersionList(final Collection<CodeSystemVersionEntry> sourceCodeSystemVersions) {
		final Collection<ICodeSystemVersion> targetCodeSystemVersions = Collections2.transform(sourceCodeSystemVersions, CODE_SYSTEM_VERSION_CONVERTER);
		return VERSION_ID_ORDERING.immutableSortedCopy(targetCodeSystemVersions);
	}

}
