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
package com.b2international.snowowl.snomed.api.impl.traceability;

import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.ft.FeatureToggles;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.server.CDOChangeProcessorFactory;
import com.b2international.snowowl.datastore.server.reindex.ReindexRequest;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.importer.rf2.util.ImportUtil;

/**
 * CDO change processor factory responsible to create {@link SnomedTraceabilityChangeProcessor traceability change processors} for the SNOMED CT terminology.
 */
public class SnomedTraceabilityChangeProcessorFactory implements CDOChangeProcessorFactory {

	private static final String FACTORY_NAME = "SNOMED CT traceability change processor factory";

	@Override
	public ICDOChangeProcessor createChangeProcessor(final IBranchPath branchPath) throws SnowowlServiceException {
		// SNOMED CT import is in progress
		if (isImportInProgress(branchPath) || isReindexInProgress()) {
			return ICDOChangeProcessor.NULL_IMPL;
		} else {
			final RevisionIndex index = ApplicationContext.getServiceForClass(RepositoryManager.class).get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
			final boolean collectSystemChanges = ApplicationContext.getServiceForClass(SnomedCoreConfiguration.class).isCollectSystemChanges();
			return new SnomedTraceabilityChangeProcessor(index, branchPath, collectSystemChanges);
		}
	}

	private boolean isImportInProgress(final IBranchPath branchPath) {
		final FeatureToggles features = ApplicationContext.getServiceForClass(FeatureToggles.class);
		final String feature = ImportUtil.createFeatureToggleString(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath);
		
		return features.exists(feature) && features.check(feature);
	}

	private boolean isReindexInProgress() {
		final FeatureToggles features = ApplicationContext.getServiceForClass(FeatureToggles.class);
		final String feature = ReindexRequest.featureFor(SnomedDatastoreActivator.REPOSITORY_UUID);
		
		return features.exists(feature) && features.check(feature);
	}

	@Override
	public String getFactoryName() {
		return FACTORY_NAME;
	}
}
