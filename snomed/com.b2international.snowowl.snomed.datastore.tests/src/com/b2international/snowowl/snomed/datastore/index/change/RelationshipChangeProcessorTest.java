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

import static org.junit.Assert.*;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.Iterables;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedPackage;

/**
 * @since 4.7
 */
public class RelationshipChangeProcessorTest extends BaseChangeProcessorTest {
	
	private RelationshipChangeProcessor processor;

	@Before
	public void givenProcessor() {
		processor = new RelationshipChangeProcessor();
	}
	
	@Test
	public void newRelationship() throws Exception {
		final Relationship relationship = createRelationship();
		registerNew(relationship);
		
		process(processor);
		
		final SnomedRelationshipIndexEntry expected = SnomedRelationshipIndexEntry.builder(relationship).build();
		final Revision actual = Iterables.getOnlyElement(processor.getMappings().values());
		assertEquals(expected, actual);
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changedRelationship() throws Exception {
		final Relationship relationship = createRelationship();
		registerDirty(relationship);
		
		process(processor);
		
		final SnomedRelationshipIndexEntry expected = SnomedRelationshipIndexEntry.builder(relationship).build();
		final Revision actual = Iterables.getOnlyElement(processor.getMappings().values());
		assertEquals(expected, actual);
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

	private Relationship createRelationship() {
		final Relationship relationship = SnomedFactory.eINSTANCE.createRelationship();
		withCDOID(relationship, nextStorageKey());
		relationship.setId(generateRelationshipId());
		relationship.setActive(true);
		relationship.setGroup(0);
		relationship.setModifier(getConcept(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER));
		relationship.setModule(module());
		relationship.setType(getConcept(Concepts.IS_A));
		relationship.setSource(getConcept(generateConceptId()));
		relationship.setDestination(getConcept(generateConceptId()));
		relationship.setCharacteristicType(getConcept(Concepts.STATED_RELATIONSHIP));
		return relationship;
	}
	
}
