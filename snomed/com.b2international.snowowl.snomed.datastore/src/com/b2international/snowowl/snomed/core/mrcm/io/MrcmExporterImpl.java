/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.OutputStream;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.authorization.AuthorizedEventBus;
import com.b2international.snowowl.core.authorization.AuthorizedRequest;
import com.b2international.snowowl.core.identity.JWTGenerator;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provider;

/**
 * MRCM Exporter to delegate the export based on the export format.
 * 
 * @author bbanfai
 * @since 4.4
 */
public class MrcmExporterImpl implements MrcmExporter {

	private final Provider<IEventBus> bus;

	public MrcmExporterImpl(Provider<IEventBus> bus) {
		this.bus = bus;
	}
	
	@Override
	public void doExport(ResourceURI resourceUri, User user, OutputStream content, MrcmExportFormat exportFormat) {
		String authorizationToken = ApplicationContext.getServiceForClass(JWTGenerator.class).generate(user);
		doExport(resourceUri, authorizationToken, content, exportFormat);
	}
	
	@Override
	public void doExport(ResourceURI resourceUri, String authorizationToken, OutputStream content, MrcmExportFormat exportFormat) {
		final AuthorizedEventBus bus = new AuthorizedEventBus(this.bus.get(), ImmutableMap.of(AuthorizedRequest.AUTHORIZATION_HEADER, authorizationToken));
		if (exportFormat == MrcmExportFormat.JSON) {
			new JsonMrcmExporter().doExport(resourceUri, bus, content);
		} else if (exportFormat == MrcmExportFormat.CSV) {
			new CsvMrcmExporter().doExport(resourceUri, bus, content);
		} else {
			throw new UnsupportedOperationException("No exporter is registered for " + exportFormat);
		}
	}

}
