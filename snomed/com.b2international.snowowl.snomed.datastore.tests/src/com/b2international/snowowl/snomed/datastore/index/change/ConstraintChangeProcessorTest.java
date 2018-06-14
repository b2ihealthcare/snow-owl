/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class ConstraintChangeProcessorTest extends BaseChangeProcessorTest {

	private ConstraintChangeProcessor processor = new ConstraintChangeProcessor();

	@Test
	public void newDescriptionConstraint() throws Exception {
		final AttributeConstraint constraint = createDescriptionConstraint();
		registerNew(constraint);
		registerNew(constraint.getDomain());
		registerNew(constraint.getPredicate());
		
		process(processor);
		
		final SnomedConstraintDocument expected = SnomedConstraintDocument.builder(constraint).build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void detachedDescriptionConstraint() throws Exception {
		final AttributeConstraint constraint = createDescriptionConstraint();
		indexRevision(MAIN, SnomedConstraintDocument.builder(constraint).build());
		registerDetached(constraint.cdoID(), MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT);
		process(processor);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(1, processor.getDeletions().size());
	}

	private AttributeConstraint createDescriptionConstraint() {
		final AttributeConstraint constraint = MrcmFactory.eINSTANCE.createAttributeConstraint();
		withCDOID(constraint, nextStorageKey());
		constraint.setActive(true);
		constraint.setAuthor("test");
		constraint.setUuid(UUID.randomUUID().toString());
		constraint.setForm(ConstraintForm.ALL_FORMS);
		constraint.setDomain(allConcepts());
		constraint.setPredicate(createDescriptionPredicate());
		return constraint;
	}

	private ConceptSetDefinition allConcepts() {
		final HierarchyConceptSetDefinition definition = MrcmFactory.eINSTANCE.createHierarchyConceptSetDefinition();
		definition.setActive(true);
		definition.setConceptId(Concepts.ROOT_CONCEPT);
		definition.setInclusionType(HierarchyInclusionType.SELF_OR_DESCENDANT);
		definition.setUuid(UUID.randomUUID().toString());
		return definition;
	}

	private ConceptModelPredicate createDescriptionPredicate() {
		final DescriptionPredicate predicate = MrcmFactory.eINSTANCE.createDescriptionPredicate();
		predicate.setActive(true);
		predicate.setEffectiveTime(new Date());
		predicate.setTypeId(Concepts.FULLY_SPECIFIED_NAME);
		predicate.setUuid(UUID.randomUUID().toString());
		return predicate;
	}
	
}
