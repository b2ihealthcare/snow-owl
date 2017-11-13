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
package com.b2international.snowowl.snomed.datastore.index.entry;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class SnomedDescriptionIndexEntrySerializationTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.<Class<?>>singleton(SnomedDescriptionIndexEntry.class);
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Test
	public void indexDescription() throws Exception {
		final SnomedDescriptionIndexEntry description = SnomedDescriptionIndexEntry.builder()
				.id(RandomSnomedIdentiferGenerator.generateDescriptionId())
				.active(true)
				.released(true)
				.effectiveTime(new Date().getTime())
				.moduleId(Concepts.MODULE_ROOT)
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.SYNONYM)
				.term("New Synonym")
				.caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.languageCode("en")
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE)
				.build();
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, description);
		final SnomedDescriptionIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedDescriptionIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertDocEquals(description, actual);
	}
	
	@Test
	public void indexFsn() throws Exception {
		assertEquals("finding", SnomedDescriptionIndexEntry.extractSemanticTag("New Finding (finding)"));
		
		final SnomedDescriptionIndexEntry description = SnomedDescriptionIndexEntry.builder()
				.id(RandomSnomedIdentiferGenerator.generateDescriptionId())
				.active(true)
				.released(true)
				.effectiveTime(new Date().getTime())
				.moduleId(Concepts.MODULE_ROOT)
				.conceptId(Concepts.ROOT_CONCEPT)
				.typeId(Concepts.FULLY_SPECIFIED_NAME)
				.term("New Finding (finding)")
				.caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.languageCode("en")
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE)
				.build();
		
		assertEquals("finding", description.getSemanticTag());
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, description);
		final SnomedDescriptionIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedDescriptionIndexEntry.class, STORAGE_KEY1);
		
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals("finding", actual.getSemanticTag());
		assertDocEquals(description, actual);
	}
	
}
