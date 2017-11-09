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
package com.b2international.snowowl.snomed.reasoner.server.changeprocessor;

import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.ft.FeatureToggles;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.server.CDOChangeProcessorFactory;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 * CDO change processor factory responsible to create {@link SnomedReasonerChangeProcessor change processors} for SNOMED CT ontologies.
 * 
 */
public class SnomedReasonerChangeProcessorFactory implements CDOChangeProcessorFactory {

	private static final String FACTORY_NAME = "SNOMED CT OWL change processor factory";

	@Override
	public String getFactoryName() {
		return FACTORY_NAME;
	}

	@Override
	public ICDOChangeProcessor createChangeProcessor(IBranchPath branchPath) throws SnowowlServiceException {
		final FeatureToggles features = ApplicationContext.getServiceForClass(FeatureToggles.class);
		final String feature = SnomedDatastoreActivator.REPOSITORY_UUID + ".import";
		
		if (features.exists(feature) && features.check(feature)) {
			return ICDOChangeProcessor.NULL_IMPL;
		} else {
			final ApplicationContext context = ApplicationContext.getInstance();
			final RevisionIndex index = context.getService(RepositoryManager.class).get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
			return new SnomedReasonerChangeProcessor(branchPath, index);
		}
	}
}
