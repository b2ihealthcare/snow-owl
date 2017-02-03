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
package com.b2international.snowowl.snomed.mrcm.core.server.concepteditor;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob;
import com.b2international.snowowl.datastore.serviceconfig.ServiceConfigJob;
import com.b2international.snowowl.snomed.mrcm.core.concepteditor.ISnomedConceptEditorService;
import com.b2international.snowowl.snomed.mrcm.core.server.MrcmCoreServerActivator;
import com.b2international.snowowl.snomed.mrcm.core.widget.IWidgetModelProvider;

/**
 * Job for registering the widget model provider to the application context.
 *  
 * @see ServiceConfigJob
 * @see IWidgetModelProvider
 */
public class SnomedConceptEditorServiceConfigJob extends AbstractServerServiceConfigJob<SnomedConceptEditorService> {

	public SnomedConceptEditorServiceConfigJob() {
		super("SNOMED CT concept editor service configuration...", MrcmCoreServerActivator.PLUGIN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob#getServiceClass()
	 */
	@Override
	protected Class<SnomedConceptEditorService> getServiceClass() {
		return SnomedConceptEditorService.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob#createServiceImplementation()
	 */
	@Override
	protected SnomedConceptEditorService createServiceImplementation() throws SnowowlServiceException {
		return new SnomedConceptEditorService();
	}
}