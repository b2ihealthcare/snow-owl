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

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.elasticsearch.core.Set;
import org.junit.Test;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;

/**
 * @since 7.24.3
 */
public class SnomedOWLRelationshipConverterTest {

	@Test
	public void testSubClassOf() {
		testSubPropertyOf(
			"SubClassOf", 
			Set.of(),
			Set.of(),
			Set.of(),
			Concepts.DEVICE
		);
	}
	
	@Test
	public void testSubObjectPropertyOf() {
		testSubPropertyOf(
			"SubObjectPropertyOf", 
			Set.of(Long.parseLong(Concepts.CONCEPT_MODEL_OBJECT_ATTRIBUTE)),
			Set.of(),
			Set.of(),
			Concepts.CONCEPT_MODEL_OBJECT_ATTRIBUTE
		);
	}
	
	@Test
	public void testSubDataPropertyOf() {
		testSubPropertyOf(
			"SubDataPropertyOf", 
			Set.of(),
			Set.of(Long.parseLong(Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE)),
			Set.of(),
			Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE
		);
	}
	
	@Test
	public void testSubAnnotationPropertyOf() {
		testSubPropertyOf(
			"SubAnnotationPropertyOf",
			Set.of(),
			Set.of(),
			Set.of(Long.parseLong(Concepts.ANNOTATION_ATTRIBUTE)),
			Concepts.ANNOTATION_ATTRIBUTE
		);
	}
	
	private void testSubPropertyOf(
		String subPropertyOfAxiom,
		Collection<Long> objectAttributes,
		Collection<Long> dataAttributes,
		Collection<Long> annotationAttributes,
		String destinationId
	) {
		SnomedOWLRelationshipConverter converter = new SnomedOWLRelationshipConverter(
			Set.of(), 
			objectAttributes, 
			dataAttributes, 
			annotationAttributes);
		
		// XXX: Using nonsensical but valid SCTIDs
		final List<SnomedOWLRelationshipDocument> owlRelationships = List.of(
			SnomedOWLRelationshipDocument.create(Concepts.IS_A, destinationId, 0)
		);
		
		final String expectedExpression = String.format("%s(:%s :%s)", subPropertyOfAxiom, Concepts.FINDING_SITE, destinationId);
		final String actualExpression = converter.fromSnomedOwlRelationships(
			false,
			true,
			Concepts.FINDING_SITE, 
			owlRelationships
		);
		
		assertEquals(expectedExpression, actualExpression);
	}
}
