/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;

/**
 * @since 6.7
 */
public class MrcmJsonImporter implements MrcmImporter {

	private final Provider<IEventBus> bus;

	public MrcmJsonImporter(Provider<IEventBus> bus) {
		this.bus = bus;
	}
	
	@Override
	public void doImport(String user, InputStream source) {
		final String branch = Branch.MAIN_PATH;
		ObjectMapper mapper = ApplicationContext.getServiceForClass(ObjectMapper.class);
		
		Set<String> existingConstraints = SnomedRequests.prepareSearchConstraint()
			.all()
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
			.execute(bus.get())
			.getSync()
			.stream()
			.map(SnomedConstraint::getId)
			.collect(Collectors.toSet());
		
		try (MappingIterator<SnomedConstraint> it = mapper.readerFor(SnomedConstraint.class).withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValues(source)) {
			final BulkRequestBuilder<TransactionContext> bulk = BulkRequest.create();
			
			while (it.hasNext()) {
				SnomedConstraint constraint = it.next();
				if (existingConstraints.contains(constraint.getId())) {
					bulk.add(constraint.toUpdateRequest());
				} else {
					bulk.add(constraint.toCreateRequest());
				}
			}
			
			SnomedRequests.prepareCommit()
				.setUserId(user)
				.setCommitComment("Imported MRCM from JSON file.")
				.setBody(bulk)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(bus.get())
				.getSync();
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Failed to import MRCM from JSON file.", e);
		}
	}

}
