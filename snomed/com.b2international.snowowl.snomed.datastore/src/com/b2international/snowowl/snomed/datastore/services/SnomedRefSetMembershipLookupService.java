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
package com.b2international.snowowl.snomed.datastore.services;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * SNOMED CT terminology specific reference set membership lookup service implementation that uses the currently active branch path-scoped
 * client services.
 * @deprecated - UNSUPPORTED API, please use {@link SnomedRequests#prepareSearchMember()} instead
 */
@Client
public class SnomedRefSetMembershipLookupService extends AbstractSnomedRefSetMembershipLookupService {

	@Override
	protected SnomedClientIndexService getIndexService() {
		return ApplicationContext.getServiceForClass(SnomedClientIndexService.class);
	}
	
	@Override
	protected SnomedClientTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getServiceForClass(SnomedClientTerminologyBrowser.class);
	}
	
	@Override
	protected SnomedClientStatementBrowser getStatementBrowser() {
		return ApplicationContext.getServiceForClass(SnomedClientStatementBrowser.class);
	}
}