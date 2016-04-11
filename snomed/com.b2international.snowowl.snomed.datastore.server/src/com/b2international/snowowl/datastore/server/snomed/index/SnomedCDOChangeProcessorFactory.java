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
package com.b2international.snowowl.datastore.server.snomed.index;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.server.CDOChangeProcessorFactory;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;

/**
 * CDO change processor factory responsible to create {@link SnomedCDOChangeProcessor change processors} for SNOMED CT terminology.
 */
public class SnomedCDOChangeProcessorFactory implements CDOChangeProcessorFactory {

	private static final String FACTORY_NAME = "SNOMED CT change processor factory";

	@Override
	public ICDOChangeProcessor createChangeProcessor(final IBranchPath branchPath) throws SnowowlServiceException {
		final ApplicationContext context = ApplicationContext.getInstance();
		
		//SNOMED CT import is in progress
		if (context.exists(ImportIndexServerService.class)) {
			return new SnomedImportCDOChangeProcessor(context.getService(ImportIndexServerService.class), branchPath); 
		}
		
		final SnomedIndexUpdater indexService = context.getService(SnomedIndexUpdater.class);
		final SnomedTerminologyBrowser terminologyBrowser = context.getService(SnomedTerminologyBrowser.class);
		final SnomedStatementBrowser statementBrowser = context.getService(SnomedStatementBrowser.class);
		final ISnomedIdentifierService identifierService = context.getService(ISnomedIdentifierService.class);
		return new SnomedCDOChangeProcessor(branchPath, terminologyBrowser, statementBrowser, identifierService, indexService);
	}

	@Override
	public String getFactoryName() {
		return FACTORY_NAME;
	}
}
