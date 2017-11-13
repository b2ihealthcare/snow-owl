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
package com.b2international.snowowl.snomed.core.mrcm.io;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.serviceconfig.AbstractServerServiceConfigJob;

/**
 * MRCM Exporter config job to register the service.
 * @since 4.4
 */
public class MrcmExporterConfigJob extends AbstractServerServiceConfigJob<MrcmExporter> {

	public MrcmExporterConfigJob() {
		super("Configuring MRCM exporter...", "mrcm");
	}

	@Override
	protected Class<MrcmExporter> getServiceClass() {
		return MrcmExporter.class;
	}

	@Override
	protected MrcmExporter createServiceImplementation() throws SnowowlServiceException {
		return new MrcmExporterImpl();
	}

}
