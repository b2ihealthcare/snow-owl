/*
 * Copyright 2024 B2i Healthcare Ltd, http://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore.request;

import static org.junit.Assert.*;

import java.util.List;

import org.elasticsearch.core.Set;
import org.junit.Test;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.google.common.collect.Iterables;

/**
 * @since 9.2.0
 */
public class SnomedOWLExpressionConverterTest {

	@Test
	public void testSubObjectPropertyOf() {
		testSubPropertyOf("SubObjectPropertyOf");
	}
	
	@Test
	public void testSubDataPropertyOf() {
		testSubPropertyOf("SubDataPropertyOf");
	}
	
	@Test
	public void testSubAnnotationPropertyOf() {
		testSubPropertyOf("SubAnnotationPropertyOf");
	}
	
	private void testSubPropertyOf(String subPropertyOfAxiom) {
		SnomedOWLExpressionConverter converter = new SnomedOWLExpressionConverter(() -> Set.of());
		
		// XXX: Using nonsensical but valid SCTIDs
		SnomedOWLExpressionConverterResult converterResult = converter.toSnomedOWLRelationships(
			Concepts.FINDING_SITE, 
			String.format("%s(:%s :%s)", subPropertyOfAxiom, Concepts.FINDING_SITE, Concepts.AMBIGUOUS));
		
		assertNull(converterResult.getGciAxiomRelationships());
		
		final List<SnomedOWLRelationshipDocument> relationships = converterResult.getClassAxiomRelationships();
		assertEquals(1, relationships.size());
		
		final SnomedOWLRelationshipDocument relationship = Iterables.getOnlyElement(relationships);
		assertEquals(Concepts.IS_A, relationship.getTypeId());
		assertEquals(Concepts.AMBIGUOUS, relationship.getDestinationId());
	}
}
