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
package com.b2international.snowowl.snomed.datastore.index.change;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.junit.Test;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class RelationshipChangeProcessorTest extends BaseChangeProcessorTest {
	
	private RelationshipChangeProcessor processor = new RelationshipChangeProcessor();

	@Test
	public void newRelationship() throws Exception {
		final Relationship relationship = createRandomRelationship();
		registerNew(relationship);
		
		process(processor);
		
		final SnomedRelationshipIndexEntry expected = SnomedRelationshipIndexEntry.builder(relationship).build();
		final Revision actual = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changedRelationship() throws Exception {
		final Relationship relationship = createRandomRelationship();
		registerDirty(relationship);
		
		process(processor);
		
		final SnomedRelationshipIndexEntry expected = SnomedRelationshipIndexEntry.builder(relationship).build();
		final Revision actual = Iterables.getOnlyElement(processor.getMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void detachedRelationship() throws Exception {
		final CDOID deletedRelationshipStorageKey = nextStorageKeyAsCDOID();
		registerDetached(deletedRelationshipStorageKey, SnomedPackage.Literals.RELATIONSHIP);
		
		process(processor);
		
		assertEquals(0, processor.getMappings().size());
		assertEquals(1, processor.getDeletions().size());
	}

}
