/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.api.codesystem.ICodeSystemVersionService;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystemVersion;
import com.b2international.snowowl.api.codesystem.domain.ICodeSystemVersionProperties;
import com.b2international.snowowl.api.impl.codesystem.domain.CodeSystemVersion;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.exceptions.LockedException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.CodeSystemVersions;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.TerminologyRegistryService;
import com.b2international.snowowl.datastore.UserBranchPathMap;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.datastore.version.VersioningService;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

public class CodeSystemVersionServiceImpl implements ICodeSystemVersionService {

	private static final Function<? super com.b2international.snowowl.datastore.ICodeSystemVersion, ICodeSystemVersion> CODE_SYSTEM_VERSION_CONVERTER = 
			new Function<com.b2international.snowowl.datastore.ICodeSystemVersion, ICodeSystemVersion>() {

		@Override
		public ICodeSystemVersion apply(final com.b2international.snowowl.datastore.ICodeSystemVersion input) {
			final CodeSystemVersion result = new CodeSystemVersion();
			result.setDescription(input.getDescription());
			result.setEffectiveDate(toDate(input.getEffectiveDate()));
			result.setImportDate(toDate(input.getImportDate()));
			result.setLastModificationDate(toDate(input.getLatestUpdateDate()));
			result.setParentBranchPath(input.getParentBranchPath());
			result.setPatched(input.isPatched());
			result.setVersion(input.getVersionId());
			return result;
		}

		private Date toDate(final long timeStamp) {
			return timeStamp >= 0L ? new Date(timeStamp) : null;
		}
	};

	private static final Ordering<ICodeSystemVersion> VERSION_ID_ORDERING = Ordering.natural().onResultOf(new Function<ICodeSystemVersion, String>() {
		@Override
		public String apply(final ICodeSystemVersion input) {
			return input.getVersion();
		}
	});

	private static TerminologyRegistryService getRegistryService() {
		return ApplicationContext.getServiceForClass(TerminologyRegistryService.class);
	}

	@Override
	public List<ICodeSystemVersion> getCodeSystemVersions(final String shortName) {
		checkNotNull(shortName, "Short name may not be null.");
		
		final ICodeSystem codeSystem = getCodeSystem(shortName);
		if (codeSystem == null) {
			throw new CodeSystemNotFoundException(shortName);
		}
		
		final Collection<com.b2international.snowowl.datastore.ICodeSystemVersion> versions = getCodeSystemVersions(shortName,
				codeSystem.getRepositoryUuid()); 
		return toSortedCodeSystemVersionList(versions);
	}
	
	private ICodeSystem getCodeSystem(final String shortName) {
		return getRegistryService().getCodeSystemByShortName(new UserBranchPathMap(), shortName);
	}

	@Override
	public ICodeSystemVersion getCodeSystemVersionById(final String shortName, final String versionId) {
		checkNotNull(shortName, "Short name may not be null.");
		checkNotNull(versionId, "Version identifier may not be null.");
		
		final ICodeSystem codeSystem = getCodeSystem(shortName);
		if (codeSystem == null) {
			throw new CodeSystemNotFoundException(shortName);
		}
		
		final CodeSystemVersions versions = CodeSystemRequests
				.prepareSearchCodeSystemVersion()
				.filterByCodeSystemShortName(shortName)
				.filterByVersionId(versionId)
				.build(codeSystem.getRepositoryUuid())
				.execute(getEventBus())
				.getSync();
		
		final com.b2international.snowowl.datastore.ICodeSystemVersion version = Iterables.getOnlyElement(versions, null);
		if (version == null) {
			throw new CodeSystemVersionNotFoundException(versionId);
		} else {
			return CODE_SYSTEM_VERSION_CONVERTER.apply(version);
		}
	}
	
	@Override
	public ICodeSystemVersion createVersion(String shortName, ICodeSystemVersionProperties properties) {
		final ICodeSystem codeSystem = getCodeSystem(shortName);
		if (codeSystem == null) {
			throw new CodeSystemNotFoundException(shortName);
		}
		
		final String terminologyId = "com.b2international.snowowl.terminology.snomed";
		final Collection<com.b2international.snowowl.datastore.ICodeSystemVersion> versions = getCodeSystemVersions(shortName, codeSystem.getRepositoryUuid());
		final ImmutableMap<String, Collection<com.b2international.snowowl.datastore.ICodeSystemVersion>> versionsMap = ImmutableMap
				.<String, Collection<com.b2international.snowowl.datastore.ICodeSystemVersion>> builder().put(terminologyId, versions).build();
		final VersioningService versioningService = new VersioningService(terminologyId, versionsMap);
		
		try {
			versioningService.acquireLock();
			configureVersion(codeSystem, properties, versioningService);
			final IStatus result = versioningService.tag();
			
			if (result.isOK()) {
				return getCodeSystemVersionById(shortName, properties.getVersion());
			} else {
				throw new SnowowlRuntimeException("Version creation failed due to " + result.getMessage());
			}
		} catch (SnowowlServiceException e) {
			throw new LockedException(String.format("Cannot create version. %s is locked. Details: %s", shortName, e.getMessage()));
		} finally {
			try {
				versioningService.releaseLock();
			} catch (SnowowlServiceException e) {
				throw new SnowowlRuntimeException("Releasing lock failed: " +  e.getMessage());
			}
		}
	}

	private void configureVersion(final ICodeSystem codeSystem, final ICodeSystemVersionProperties properties,
			final VersioningService versioningService) {
		versioningService.configureDescription(properties.getDescription());
		versioningService.configureParentBranchPath(codeSystem.getBranchPath());
		versioningService.configureCodeSystemShortName(codeSystem.getShortName());
		
		final IStatus dateResult = versioningService.configureEffectiveTime(properties.getEffectiveDate());
		if (!dateResult.isOK()) {
			throw new BadRequestException("The specified %s effective time is invalid. %s", properties.getEffectiveDate(), dateResult.getMessage());
		}

		final IStatus versionResult = versioningService.configureNewVersionId(properties.getVersion(), false);
		if (!versionResult.isOK()) {
			throw new AlreadyExistsException("Version", properties.getVersion());
		}
		
		// FIXME remove hard coded SNOMED CT store value, versioning should get the repositoryId from the API
		final String repositoryId = "snomedStore";
		
		final String versionBranch = codeSystem.getBranchPath() + "/" + properties.getVersion();
		
		try {
			RepositoryRequests
				.branching()
				.prepareGet(versionBranch)
				.build(repositoryId)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();
			throw new ConflictException("An existing branch with path '%s' conflicts with the specified version identifier.", versionBranch);
		} catch (NotFoundException expected) {
			// fall-through
		}
	}

	private Collection<com.b2international.snowowl.datastore.ICodeSystemVersion> getCodeSystemVersions(final String shortName, final String repositoryId) {
		final Collection<com.b2international.snowowl.datastore.ICodeSystemVersion> result = newHashSet();
		result.addAll(CodeSystemRequests
				.prepareSearchCodeSystemVersion()
				.filterByCodeSystemShortName(shortName)
				.build(repositoryId)
				.execute(getEventBus())
				.getSync()
				.getItems());
		return result;
	}
	
	private IEventBus getEventBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

	private List<ICodeSystemVersion> toSortedCodeSystemVersionList(
			final Collection<com.b2international.snowowl.datastore.ICodeSystemVersion> sourceCodeSystemVersions) {
		final Collection<ICodeSystemVersion> targetCodeSystemVersions = Collections2.transform(sourceCodeSystemVersions, CODE_SYSTEM_VERSION_CONVERTER);
		return VERSION_ID_ORDERING.immutableSortedCopy(targetCodeSystemVersions);
	}

}
