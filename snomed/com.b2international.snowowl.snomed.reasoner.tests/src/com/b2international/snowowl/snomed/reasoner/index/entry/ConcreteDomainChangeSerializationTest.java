/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.index.entry;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.index.ConcreteDomainChangeDocument;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.0
 */
public class ConcreteDomainChangeSerializationTest extends BaseRevisionIndexTest {

	@Override
	protected void configureMapper(final ObjectMapper mapper) {
		mapper.registerModule(new PrimitiveCollectionModule());
	}

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.singleton(ConcreteDomainChangeDocument.class);
	}

	private static String randomUUID() {
		return UUID.randomUUID().toString();
	}

	@Test
	public void indexNewConcreteDomain() throws Exception {
		final String id = randomUUID();
		final String classificationId = randomUUID();

		final ConcreteDomainChangeDocument expected = ConcreteDomainChangeDocument.builder()
				.characteristicTypeId(Concepts.INFERRED_RELATIONSHIP)
				.classificationId(classificationId)
				.group(1)
				.memberId(randomUUID())
				.nature(ChangeNature.NEW)
				.referencedComponentId("12345678901")
				.released(Boolean.FALSE)
				.serializedValue("500")
				.build();

		indexDocument(id, expected);

		final ConcreteDomainChangeDocument actual = rawIndex()
				.read(r -> r.get(ConcreteDomainChangeDocument.class, id));
		
		assertDocEquals(expected, actual);
	}
	
	@Test
	public void indexUpdatedConcreteDomain() throws Exception {
		final String id = randomUUID();
		final String classificationId = randomUUID();
		
		final ConcreteDomainChangeDocument expected = ConcreteDomainChangeDocument.builder()
				.classificationId(classificationId)
				.memberId(randomUUID())
				.nature(ChangeNature.UPDATED)
				.referencedComponentId("12345678901")
				.released(Boolean.TRUE)
				.serializedValue("500")
				.build();
		
		indexDocument(id, expected);
		
		final ConcreteDomainChangeDocument actual = rawIndex()
				.read(r -> r.get(ConcreteDomainChangeDocument.class, id));
		
		assertDocEquals(expected, actual);
	}
	
	@Test
	public void indexRedundantConcreteDomain() throws Exception {
		final String id = randomUUID();
		final String classificationId = randomUUID();
		
		final ConcreteDomainChangeDocument expected = ConcreteDomainChangeDocument.builder()
				.classificationId(classificationId)
				.memberId(randomUUID())
				.nature(ChangeNature.REDUNDANT)
				.referencedComponentId("12345678901")
				.released(Boolean.TRUE)
				.build();
		
		indexDocument(id, expected);
		
		final ConcreteDomainChangeDocument actual = rawIndex()
				.read(r -> r.get(ConcreteDomainChangeDocument.class, id));
		
		assertDocEquals(expected, actual);
	}
}
