/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;

/**
 * @since 6.25.3
 */
public class JsonMrcmExporter {

	private static final Logger LOG = LoggerFactory.getLogger(JsonMrcmExporter.class);

	public void doExport(String user, OutputStream stream) {
		final String branch = Branch.MAIN_PATH;
		LogUtils.logExportActivity(LOG, user, branch, "Exporting MRCM rules to JSON...");
		final ObjectMapper mapper = ApplicationContext.getServiceForClass(ObjectMapper.class);

		SnomedConstraints constraints = SnomedRequests.prepareSearchConstraint()
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync();

		try (SequenceWriter writer = mapper.writer().withDefaultPrettyPrinter().writeValues(stream)) {
			for (SnomedConstraint c : constraints) {
				writer.write(c);
			}
			LogUtils.logExportActivity(LOG, user, branch, "MRCM rule export to JSON successfully finished.");
		} catch (IOException e) {
			LogUtils.logExportActivity(LOG, user, branch, "Failed to export MRCM rules.");
			throw new SnowowlRuntimeException("Failed to export MRCM rules.", e);
		}
	}

}
