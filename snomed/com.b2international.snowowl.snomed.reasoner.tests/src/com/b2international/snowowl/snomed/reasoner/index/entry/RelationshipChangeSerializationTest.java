/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.b2international.snowowl.snomed.reasoner.index.RelationshipChangeDocument;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.0
 */
public class RelationshipChangeSerializationTest extends BaseRevisionIndexTest {

	@Override
	protected void configureMapper(final ObjectMapper mapper) {
		mapper.registerModule(new PrimitiveCollectionModule());
	}

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.singleton(RelationshipChangeDocument.class);
	}

	private static String randomUUID() {
		return UUID.randomUUID().toString();
	}

	@Test
	public void indexClassificationTask() throws Exception {
		final String id = randomUUID();
		final String classificationId = randomUUID();

		final RelationshipChangeDocument expected = RelationshipChangeDocument.builder()
				.classificationId(classificationId)
				.sourceId("sourceId")
				.typeId("typeId")
				.destinationId("destinationId")
				.group(1)
				.unionGroup(2)
				.nature(ChangeNature.INFERRED)
				.released(true)
				.relationshipId("12345678901")
				.build();

		indexDocument(id, expected);

		final RelationshipChangeDocument actual = rawIndex()
				.read(r -> r.get(RelationshipChangeDocument.class, id));
		assertDocEquals(expected, actual);
	}
}
