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
package com.b2international.snowowl.datastore.server.snomed.index;

import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.server.CDOChangeProcessorFactory;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 * CDO change processor factory responsible to create {@link SnomedCDOChangeProcessor change processors} for SNOMED CT terminology.
 */
public class SnomedCDOChangeProcessorFactory implements CDOChangeProcessorFactory {

	private static final String FACTORY_NAME = "SNOMED CT change processor factory";

	@Override
	public ICDOChangeProcessor createChangeProcessor(final IBranchPath branchPath) throws SnowowlServiceException {
		final ApplicationContext context = ApplicationContext.getInstance();
		final RevisionIndex index = context.getService(RepositoryManager.class).get(SnomedDatastoreActivator.REPOSITORY_UUID).service(RevisionIndex.class);
		return new SnomedCDOChangeProcessor(branchPath, index);
	}
	
	@Override
	public String getFactoryName() {
		return FACTORY_NAME;
	}
}
