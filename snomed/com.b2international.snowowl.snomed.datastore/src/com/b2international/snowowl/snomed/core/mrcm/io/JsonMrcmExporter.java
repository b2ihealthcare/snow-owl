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

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BaseComponent;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraints;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @since 7.0
 */
final class JsonMrcmExporter {

	public void doExport(IEventBus bus, OutputStream stream, String branch) {
		
		SnomedConstraints constraints = SnomedRequests.prepareSearchConstraint()
			.all()
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
			.execute(bus)
			.getSync();

		ObjectMapper mapper = new ObjectMapper()
				.setSerializationInclusion(Include.NON_EMPTY)
				.registerModule(new SimpleModule().addSerializer(Set.class, new CustomSerializer()));
		
		try (SequenceWriter writer = mapper.writer().withDefaultPrettyPrinter().writeValues(stream)) {
			for (SnomedConstraint c : constraints.stream().sorted((c1,c2) -> c1.getId().compareTo(c2.getId())).collect(toList())) {
				writer.write(c);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}
	
	private class CustomSerializer<T extends BaseComponent> extends JsonSerializer<Set<T>> {

		@Override
		public void serialize(Set<T> set, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
			gen.writeStartArray();
	        if (!set.isEmpty()) {
	            for (Object item : set.stream().sorted((c1,c2) -> c1.getId().compareTo(c2.getId())).collect(toList())) {
	                gen.writeObject(item);
	            }
	        }
	        gen.writeEndArray();
		}
		
	}
	
}
