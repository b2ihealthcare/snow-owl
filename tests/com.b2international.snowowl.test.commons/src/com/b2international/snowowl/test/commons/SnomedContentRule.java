/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.test.commons;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

import org.junit.rules.ExternalResource;

import com.b2international.commons.platform.PlatformUtil;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.attachments.AttachmentRegistry;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Rf2ReleaseType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;

/**
 * JUnit test rule to import SNOMED CT content during automated tests.
 * 
 * @since 3.6
 */
public class SnomedContentRule extends ExternalResource {

	private final File importArchive;
	private final Rf2ReleaseType contentType;
	private final String codeSystemShortName;
	private final String codeSystemBranchPath;

	public SnomedContentRule(final String codeSystemShortName, final String branchPath, final String importArchivePath, final Rf2ReleaseType contentType) {
		this(codeSystemShortName, branchPath, SnomedContentRule.class, importArchivePath, contentType);
	}
	
	public SnomedContentRule(final String codeSystemShortName, final String branchPath, final Class<?> relativeClass, final String importArchivePath, final Rf2ReleaseType contentType) {
		this.codeSystemShortName = checkNotNull(codeSystemShortName, "codeSystem");
		this.codeSystemBranchPath = checkNotNull(branchPath, "branchPath");
		this.contentType = checkNotNull(contentType, "contentType");
		this.importArchive = PlatformUtil.toAbsolutePathBundleEntry(relativeClass, importArchivePath).toFile();
	}

	@Override
	protected void before() throws Throwable {
		createBranch();
		createCodeSystemIfNotExist();
		UUID rf2ArchiveId = UUID.randomUUID();
		ApplicationContext.getServiceForClass(AttachmentRegistry.class).upload(rf2ArchiveId, new FileInputStream(importArchive));
		SnomedRequests.rf2().prepareImport()
			.setRf2ArchiveId(rf2ArchiveId)
			.setUserId("info@b2international.com")
			.setReleaseType(contentType)
			.setCreateVersions(true)
			.setCodeSystemShortName(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, codeSystemBranchPath)
			.execute(getBus())
			.getSync();
	}
	
	private void createBranch() {
		if (!IBranchPath.MAIN_BRANCH.equals(codeSystemBranchPath)) {
			final IBranchPath csPath = BranchPathUtils.createPath(codeSystemBranchPath);
			RepositoryRequests.branching().prepareCreate()
				.setParent(csPath.getParentPath())
				.setName(csPath.lastSegment())
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(getBus())
				.getSync();
		}
	}

	private void createCodeSystemIfNotExist() {
		if (!SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME.equals(codeSystemShortName)) {
			final IEventBus eventBus = getBus();
			
			CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
					.setLimit(0)
					.filterById(codeSystemShortName)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID)
					.execute(eventBus)
					.getSync();
			
			if (codeSystems.getTotal() < 1) {
				CodeSystemRequests.prepareNewCodeSystem()
					.setBranchPath(codeSystemBranchPath)
					.setCitation("citation")
					.setExtensionOf(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME)
					.setIconPath("iconPath")
					.setLanguage("language")
					.setLink("organizationLink")
					.setName("Extension " + codeSystemShortName)
					.setOid("oid:"+codeSystemShortName)
					.setRepositoryUuid(SnomedDatastoreActivator.REPOSITORY_UUID)
					.setShortName(codeSystemShortName)
					.setTerminologyId(SnomedTerminologyComponentConstants.TERMINOLOGY_ID)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, Branch.MAIN_PATH, "info@b2international.com", String.format("Create code system %s", codeSystemShortName))
					.execute(eventBus)
					.getSync();
			}
		}
	}
	
	private static IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
}
