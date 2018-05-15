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
package com.b2international.snowowl.snomed.api.impl;

import static com.b2international.commons.FileUtils.copyContentToTempFile;
import static com.b2international.snowowl.snomed.common.ContentSubType.getByNameIgnoreCase;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.valueOf;
import static java.util.Collections.synchronizedMap;
import static java.util.UUID.randomUUID;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.ApiValidation;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.ContentAvailabilityInfoManager;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.ISnomedRf2ImportService;
import com.b2international.snowowl.snomed.api.domain.exception.SnomedImportConfigurationNotFoundException;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedImportConfiguration;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration.ImportStatus;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.importer.net4j.SnomedImportResult;
import com.b2international.snowowl.snomed.importer.net4j.SnomedValidationDefect;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * {@link ISnomedRf2ImportService SNOMED&nbsp;CT RF2 import service} implementation.
 * Used for importing SNOMED&nbsp;CT content into the system from RF2 release archives.
 */
public class SnomedRf2ImportService implements ISnomedRf2ImportService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SnomedRf2ImportService.class);
	
	/**
	 * A mapping between the registered import identifiers and the associated import configurations.
	 * <p>
	 * Keys are versions.<br>Values are mapping between the import IDs and the configurations.
	 */
	private final Map<UUID, ISnomedImportConfiguration> configurationMapping = synchronizedMap(Maps.<UUID, ISnomedImportConfiguration>newHashMap());
	
	@Override
	public ISnomedImportConfiguration getImportDetails(final UUID importId) {

		final ISnomedImportConfiguration configuration = configurationMapping.get(importId);
		if (null == configuration) {
			throw new SnomedImportConfigurationNotFoundException(importId);
		}
		
		return configuration;
	}

	@Override
	public void deleteImportDetails(final UUID importId) {
		configurationMapping.remove(importId);
	}

	@Override
	public void startImport(final UUID importId, final InputStream inputStream) {
		
		checkNotNull(importId, "SNOMED CT import identifier should be specified.");
		checkNotNull(inputStream, "Cannot stream the content of the given SNOMED CT release archive.");
		
		final ISnomedImportConfiguration configuration = getImportDetails(importId);
		
		final ImportStatus currentStatus = configuration.getStatus();
		if (!ImportStatus.WAITING_FOR_FILE.equals(currentStatus)) {
			final StringBuilder sb = new StringBuilder();
			sb.append("Cannot start SNOMED CT import. Import configuration is ");
			sb.append(valueOf(currentStatus).toLowerCase());
			sb.append(".");
			throw new BadRequestException(sb.toString());
		}
		
		if (isImportAlreadyRunning()) {
			throw new BadRequestException("Cannot perform SNOMED CT import from RF2 archive. "
					+ "An import is already in progress. Please try again later.");
		}
		
		final Rf2ReleaseType releaseType = configuration.getRf2ReleaseType();
		final boolean contentAvailable = ContentAvailabilityInfoManager.INSTANCE.isAvailable(REPOSITORY_UUID);
		final boolean isMain = Branch.MAIN_PATH.equals(configuration.getBranchPath());
		
		if (contentAvailable && Rf2ReleaseType.FULL.equals(releaseType) && isMain) {
			throw new BadRequestException("Importing a full release of SNOMED CT "
					+ "from an archive to MAIN branch is prohibited when SNOMED CT "
					+ "ontology is already available on the terminology server. "
					+ "Please perform either a delta or a snapshot import instead.");
		}
		
		if (!contentAvailable && Rf2ReleaseType.DELTA.equals(releaseType) && isMain) {
			throw new BadRequestException("Importing a delta release of SNOMED CT "
					+ "from an archive to MAIN branch is prohibited when SNOMED CT "
					+ "ontology is not available on the terminology server. "
					+ "Please perform either a full or a snapshot import instead.");
		}
		
		if (!contentAvailable && !isMain) {
			throw new BadRequestException("Importing a release of SNOMED CT from an "
					+ "archive to other than MAIN branch is prohibited when SNOMED CT "
					+ "ontology is not available on the terminology server. "
					+ "Please perform a full import to MAIN branch first.");
		}
		
		final String branchPath = configuration.getBranchPath();
		if (!isMain && !BranchPathUtils.exists(REPOSITORY_UUID, branchPath)) {
			throw new BadRequestException("Importing a release of SNOMED CT from an "
					+ "archive to other than MAIN branch is prohibited when the given "
					+ "branch does not exist. Please perform a branch creation first.");
		}
		
		final String codeSystemShortName = configuration.getCodeSystemShortName();
		final CodeSystemEntry codeSystemEntry = getCodeSystem(codeSystemShortName);
		if (codeSystemEntry == null && !SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME.equals(codeSystemShortName)) {
			throw new BadRequestException("Importing a release of SNOMED CT from an archive is prohibited "
					+ "when SNOMED CT extension with short name %s does not exist. Please create it before "
					+ "importing content with this configuration, or use SNOMEDCT for importing the "
					+ "International Release; in this case the corresponding code system will be created automatically.", 
					codeSystemShortName);
		}
		
		final File archiveFile = copyContentToTempFile(inputStream, valueOf(randomUUID()));
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					((SnomedImportConfiguration) configuration).setStatus(ImportStatus.RUNNING);
					final SnomedImportResult result = doImport(configuration, archiveFile);
					((SnomedImportConfiguration) configuration).setStatus(convertStatus(result.getValidationDefects())); 
				} catch (final Exception e) {
					LOG.error("Error during the import of " + archiveFile, e);
					((SnomedImportConfiguration) configuration).setStatus(ImportStatus.FAILED);
				}
			}
		}).start();
	}
	
	private ImportStatus convertStatus(Set<SnomedValidationDefect> validationDefects) {
		for (SnomedValidationDefect validationDefect : validationDefects) {
			if (validationDefect.getDefectType().isCritical()) {
				return ImportStatus.FAILED;
			}
		}
		
		return ImportStatus.COMPLETED;
	}

	private SnomedImportResult doImport(final ISnomedImportConfiguration configuration, final File archiveFile) throws Exception {
		final IBranchPath branch = BranchPathUtils.createPath(configuration.getBranchPath());
		return new ImportUtil().doImport(
				configuration.getCodeSystemShortName(),
				getByNameIgnoreCase(valueOf(configuration.getRf2ReleaseType())), 
				branch,
				archiveFile,
				configuration.shouldCreateVersion());
	}

	private boolean isImportAlreadyRunning() {
		return Iterables.any(configurationMapping.values(), new Predicate<ISnomedImportConfiguration>() {
			@Override
			public boolean apply(final ISnomedImportConfiguration configuration) {
				return ImportStatus.RUNNING.equals(configuration.getStatus());
			}
		});
	}
	
	private CodeSystemEntry getCodeSystem(final String shortName) {
		try {
			return CodeSystemRequests.prepareGetCodeSystem(shortName)
					.build(REPOSITORY_UUID)
					.execute(getEventBus())
					.getSync();
		} catch (NotFoundException e) {
			return null;
		}
	}
	
	private IEventBus getEventBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}
	
	@Override
	public UUID create(final ISnomedImportConfiguration configuration) {
		checkNotNull(configuration, "SNOMED CT import configuration should be specified.");
		ApiValidation.checkInput(configuration);
		
		// Check version and branch existence in case of DELTA RF2 import
		// FULL AND SNAPSHOT can be import into empty databases
		if (Rf2ReleaseType.DELTA == configuration.getRf2ReleaseType()) {
			// will throw exception internally if the branch is not found
			RepositoryRequests.branching().prepareGet(configuration.getBranchPath()).build(REPOSITORY_UUID).execute(getEventBus()).getSync(1, TimeUnit.MINUTES);
		}
		
		final UUID importId = randomUUID();
		configurationMapping.put(importId, configuration);
		return importId;
	}
}
