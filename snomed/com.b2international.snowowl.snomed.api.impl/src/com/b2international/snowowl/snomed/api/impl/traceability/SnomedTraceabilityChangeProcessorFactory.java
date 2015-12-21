/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.server.index.CDOChangeIndexProcessorFactory;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexUpdater;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;

/**
 * CDO change processor factory responsible to create {@link SnomedTraceabilityChangeProcessor traceability change processors} for the SNOMED CT terminology.
 */
public class SnomedTraceabilityChangeProcessorFactory extends CDOChangeIndexProcessorFactory {

	private static final String FACTORY_NAME = "SNOMED CT traceability change processor factory";

	@Override
	protected ICDOChangeProcessor doCreateChangeProcessor(final IBranchPath branchPath, final boolean canCopyThreadLocal) throws SnowowlServiceException {
		// SNOMED CT import is in progress
		if (ApplicationContext.getInstance().exists(ImportIndexServerService.class)) {
			return ICDOChangeProcessor.NULL_IMPL; 
		} else {
			final SnomedIndexUpdater indexService = ApplicationContext.getServiceForClass(SnomedIndexUpdater.class);
			final boolean collectSystemChanges = ApplicationContext.getServiceForClass(SnomedCoreConfiguration.class).isCollectSystemChanges();
			return new SnomedTraceabilityChangeProcessor(indexService, branchPath, collectSystemChanges);
		}
	}

	@Override
	public String getFactoryName() {
		return FACTORY_NAME;
	}
}
