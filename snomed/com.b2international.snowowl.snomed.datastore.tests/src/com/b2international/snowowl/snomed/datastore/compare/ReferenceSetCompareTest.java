/*******************************************************************************
 * Copyright (c) 2020 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.datastore.compare;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.compare.CompareSetResult;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.8
 */
public class ReferenceSetCompareTest {
	private static final String CODE_SYSTEM_1 = SnomedTerminologyComponentConstants.TERMINOLOGY_ID;
	private static final String CODE_SYSTEM_2 = "cs1";
	private static final String CODE_SYSTEM_3 = "cs2";
	
	final ReferenceSetCompare comparator = new ReferenceSetCompare();
	
	@Test
	public void compareEqualSimleMapRefSets() {
		SnomedReferenceSet rf1 = new SnomedReferenceSet();
		rf1.setId("rf1");
		rf1.setReferencedComponentType(CODE_SYSTEM_1);
		rf1.setMapTargetComponentType(CODE_SYSTEM_2);
		rf1.setType(SnomedRefSetType.SIMPLE_MAP);
		
		SnomedReferenceSet rf2 = new SnomedReferenceSet();
		rf2.setId("rf2");
		rf2.setReferencedComponentType(CODE_SYSTEM_1);
		rf2.setMapTargetComponentType(CODE_SYSTEM_2);
		rf2.setType(SnomedRefSetType.SIMPLE_MAP);
		
		final Map<String, Object> properties1 = new HashMap<String, Object>();
		properties1.put(SnomedRf2Headers.FIELD_MAP_TARGET, "2");
		
		SnomedConcept concept1 = new SnomedConcept("1");
		
		SnomedReferenceSetMember member1 = new SnomedReferenceSetMember();
		member1.setId("1");
		member1.setProperties(properties1);
		member1.setReferencedComponent(concept1);
		
		SnomedReferenceSetMember member2 = new SnomedReferenceSetMember();
		member2.setId("2");
		member2.setProperties(properties1);
		member2.setReferencedComponent(concept1);
		
		rf1.setMembers(new SnomedReferenceSetMembers(ImmutableList.of(member1), null, 1, 1));
		rf2.setMembers(new SnomedReferenceSetMembers(ImmutableList.of(member2), null, 1, 1));
		
		CompareSetResult<SnomedReferenceSetMember> result = comparator.doCompare(rf1, rf2);
		assertEquals(0, result.getRemovedMembers().size());
		assertEquals(0, result.getAddedMembers().size());
		assertEquals(0, result.getChangedMembers().size());
		
	}
	
	@Test(expected = BadRequestException.class)
	public void compareDifferentTypeSimleMapRefSets() {
		SnomedReferenceSet rf1 = new SnomedReferenceSet();
		rf1.setId("rf1");
		rf1.setReferencedComponentType(CODE_SYSTEM_1);
		rf1.setMapTargetComponentType(CODE_SYSTEM_2);
		rf1.setType(SnomedRefSetType.SIMPLE_MAP);

		SnomedReferenceSet rf2 = new SnomedReferenceSet();
		rf2.setId("rf2");
		rf2.setReferencedComponentType(CODE_SYSTEM_1);
		rf2.setMapTargetComponentType(CODE_SYSTEM_3);
		rf2.setType(SnomedRefSetType.SIMPLE_MAP);

		comparator.doCompare(rf1, rf2);
	}
	
	@Test(expected = BadRequestException.class)
	public void compareNonSympleTypeRefSets() {
		SnomedReferenceSet rf1 = new SnomedReferenceSet();
		rf1.setId("rf1");
		rf1.setReferencedComponentType(CODE_SYSTEM_1);
		rf1.setMapTargetComponentType(CODE_SYSTEM_2);
		rf1.setType(SnomedRefSetType.ANNOTATION);

		SnomedReferenceSet rf2 = new SnomedReferenceSet();
		rf2.setId("rf2");
		rf2.setReferencedComponentType(CODE_SYSTEM_1);
		rf2.setMapTargetComponentType(CODE_SYSTEM_3);
		rf2.setType(SnomedRefSetType.SIMPLE_MAP);
		
		comparator.doCompare(rf1, rf2);
	}
	
	@Test
	public void compareDifferentSimleMapRefSets() {
		SnomedReferenceSet rf1 = new SnomedReferenceSet();
		rf1.setId("rf1");
		rf1.setReferencedComponentType(CODE_SYSTEM_1);
		rf1.setMapTargetComponentType(CODE_SYSTEM_2);
		rf1.setType(SnomedRefSetType.SIMPLE_MAP);
		
		SnomedReferenceSet rf2 = new SnomedReferenceSet();
		rf2.setId("rf2");
		rf2.setReferencedComponentType(CODE_SYSTEM_1);
		rf2.setMapTargetComponentType(CODE_SYSTEM_2);
		rf2.setType(SnomedRefSetType.SIMPLE_MAP);
		
		final Map<String, Object> properties1 = new HashMap<String, Object>();
		properties1.put(SnomedRf2Headers.FIELD_MAP_TARGET, "2");
		
		SnomedConcept concept1 = new SnomedConcept("1");
		
		SnomedReferenceSetMember equalMember1 = new SnomedReferenceSetMember();
		equalMember1.setId("equalMember1");
		equalMember1.setProperties(properties1);
		equalMember1.setReferencedComponent(concept1);
		
		SnomedReferenceSetMember equalMember2 = new SnomedReferenceSetMember();
		equalMember2.setId("equalMember2");
		equalMember2.setProperties(properties1);
		equalMember2.setReferencedComponent(concept1);
		
		SnomedConcept changedConcept = new SnomedConcept("3");
		
		SnomedReferenceSetMember changedMember = new SnomedReferenceSetMember();
		changedMember.setId("changedMember");
		changedMember.setProperties(properties1);
		changedMember.setReferencedComponent(changedConcept);
		
		SnomedConcept removedConcept = new SnomedConcept("4");
		
		final Map<String, Object> properties2 = new HashMap<String, Object>();
		properties2.put(SnomedRf2Headers.FIELD_MAP_TARGET, "4");
		
		SnomedReferenceSetMember removedMember = new SnomedReferenceSetMember();
		removedMember.setId("removedMember");
		removedMember.setProperties(properties2);
		removedMember.setReferencedComponent(removedConcept);
		
		SnomedConcept addedConcept = new SnomedConcept("5");
		
		final Map<String, Object> addedProperties = new HashMap<String, Object>();
		addedProperties.put(SnomedRf2Headers.FIELD_MAP_TARGET, "5");
		
		SnomedReferenceSetMember addedMember = new SnomedReferenceSetMember();
		addedMember.setId("5");
		addedMember.setProperties(addedProperties);
		addedMember.setReferencedComponent(addedConcept);
		
		rf1.setMembers(new SnomedReferenceSetMembers(ImmutableList.of(equalMember1, removedMember), null, 2, 2));
		rf2.setMembers(new SnomedReferenceSetMembers(ImmutableList.of(equalMember2, changedMember, addedMember), null, 3, 3));
		
		CompareSetResult<SnomedReferenceSetMember> result = comparator.doCompare(rf1, rf2);
		assertEquals(1, result.getRemovedMembers().size());
		assertEquals(1, result.getAddedMembers().size());
		assertEquals(1, result.getChangedMembers().size());
		assertEquals(true, result.getAddedMembers().contains(addedMember));
		assertEquals(true, result.getRemovedMembers().contains(removedMember));
		assertEquals(true, result.getChangedMembers().containsEntry(equalMember1, changedMember));
	}
	
}
