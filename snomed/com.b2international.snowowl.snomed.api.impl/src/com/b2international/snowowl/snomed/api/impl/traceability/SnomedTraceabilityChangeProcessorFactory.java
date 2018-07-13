/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.ft.FeatureToggles;
import com.b2international.snowowl.core.ft.Features;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.server.CDOChangeProcessorFactory;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;

/**
 * CDO change processor factory responsible to create {@link SnomedTraceabilityChangeProcessor traceability change processors} for the SNOMED CT terminology.
 */
public class SnomedTraceabilityChangeProcessorFactory implements CDOChangeProcessorFactory {

	private static final String FACTORY_NAME = "SNOMED CT traceability change processor factory";

	@Override
	public ICDOChangeProcessor createChangeProcessor(final IBranchPath branchPath) throws SnowowlServiceException {
		// SNOMED CT import is in progress
		if (isImportInProgress(branchPath) || isReindexInProgress() || isClassifyInProgress(branchPath)) {
			return ICDOChangeProcessor.NULL_IMPL;
		} else {
			final boolean collectSystemChanges = ApplicationContext.getServiceForClass(SnomedCoreConfiguration.class).isCollectSystemChanges();
			return new SnomedTraceabilityChangeProcessor(collectSystemChanges);
		}
	}

	private boolean isImportInProgress(final IBranchPath branchPath) {
		return getFeatureToggles().isEnabled(Features.getImportFeatureToggle(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath()));
	}

	private boolean isReindexInProgress() {
		return getFeatureToggles().isEnabled(Features.getReindexFeatureToggle(SnomedDatastoreActivator.REPOSITORY_UUID));
	}
	
	private boolean isClassifyInProgress(final IBranchPath branchPath) {
		return getFeatureToggles().isEnabled(Features.getClassifyFeatureToggle(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath()));
	}

	private FeatureToggles getFeatureToggles() {
		return ApplicationContext.getServiceForClass(FeatureToggles.class);
	}
	
	@Override
	public String getFactoryName() {
		return FACTORY_NAME;
	}
}
