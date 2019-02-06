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
package com.b2international.snowowl.snomed.reasoner.index.entry;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.snowowl.snomed.reasoner.domain.ClassificationStatus;
import com.b2international.snowowl.snomed.reasoner.index.ClassificationTaskDocument;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.0
 */
public class ClassificationTaskSerializationTest extends BaseRevisionIndexTest {

	@Override
	protected void configureMapper(final ObjectMapper mapper) {
		mapper.registerModule(new PrimitiveCollectionModule());
	}

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.singleton(ClassificationTaskDocument.class);
	}

	private static String randomUUID() {
		return UUID.randomUUID().toString();
	}

	@Test
	public void indexClassificationTask() throws Exception {
		final String id = randomUUID();
		final Date completionDate = new Date();
		final Date creationDate = new Date();
		final Date saveDate = new Date();

		final ClassificationTaskDocument expected = ClassificationTaskDocument.builder()
				.id(id)
				.reasonerId("reasonerId")
				.branch("MAIN/a/b")
				.completionDate(completionDate)
				.creationDate(creationDate)
				.saveDate(saveDate)
				.deleted(false)
				.hasEquivalentConcepts(true)
				.hasInferredChanges(false)
				.hasRedundantStatedChanges(null)
				.status(ClassificationStatus.SAVED)
				.timestamp(1234L)
				.userId("user@host.domain")
				.build();

		indexDocument(id, expected);

		final ClassificationTaskDocument actual = rawIndex()
				.read(r -> r.get(ClassificationTaskDocument.class, id));
		assertDocEquals(expected, actual);
	}
}
