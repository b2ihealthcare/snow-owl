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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_CITATION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_ICON_PATH;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_LANGUAGE;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_LINK;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_OID;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_NAME;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.TERMINOLOGY_ID;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.GENERATOR_RESOURCE_NAME;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.META_ROOT_RESOURCE_NAME;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.MRCM_ROOT_RESOURCE_NAME;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REFSET_ROOT_RESOURCE_NAME;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.ROOT_RESOURCE_NAME;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.RepositoryInitializer;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemCreateRequestBuilder;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.collect.ImmutableSet;

/**
 * Repository initializer for the SNOMED CT tooling.
 */
public final class SnomedRepositoryInitializer extends RepositoryInitializer {

	private static final Set<String> UNORDERED_ENVELOPE_RESOURCES = ImmutableSet.of(ROOT_RESOURCE_NAME, GENERATOR_RESOURCE_NAME);

	@Override
	protected void checkContent(String userId, String repositoryUuid, CDOTransaction transaction) throws CommitException {
		transaction.rollback();
		
		// Add unordered SNOMED CT Concept list wrapper objects
		for (final String path : UNORDERED_ENVELOPE_RESOURCES) {
			CDOResource resource = transaction.getOrCreateResource(path);
			if (resource.getContents().size() < 1) {
				resource.getContents().add(SnomedFactory.eINSTANCE.createConcepts());
			}
		}
		
		// Add MRCM concept model wrapper object
		CDOResource mrcmResource = transaction.getOrCreateResource(MRCM_ROOT_RESOURCE_NAME);
		if (mrcmResource.getContents().size() < 1) {
			mrcmResource.getContents().add(MrcmFactory.eINSTANCE.createConceptModel());
		}

		if (transaction.isDirty()) {
			transaction.setCommitComment("Create terminology content wrapper for repository");
			transaction.commit();
		}

		super.checkContent(userId, repositoryUuid, transaction);
	}

	@Override
	protected Collection<String> getResourceNames() {
		return ImmutableSet.of(ROOT_RESOURCE_NAME, REFSET_ROOT_RESOURCE_NAME, MRCM_ROOT_RESOURCE_NAME, GENERATOR_RESOURCE_NAME, META_ROOT_RESOURCE_NAME);
	}

	@Override
	protected String getPrimaryCodeSystemShortName() {
		return SNOMED_SHORT_NAME;
	}

	@Override
	protected CodeSystemCreateRequestBuilder prepareNewPrimaryCodeSystem() {
		return CodeSystemRequests.prepareNewCodeSystem()
				.setName(SNOMED_NAME)
				.setOid(SNOMED_INT_OID)
				.setLanguage(SNOMED_INT_LANGUAGE)
				.setLink(SNOMED_INT_LINK)
				.setCitation(SNOMED_INT_CITATION)
				.setBranchPath(Branch.MAIN_PATH)
				.setIconPath(SNOMED_INT_ICON_PATH)
				.setTerminologyId(TERMINOLOGY_ID);
	}

	@Override
	protected boolean shouldCreateDbIndexes() {
		return false;
	}
}
