/*******************************************************************************
 * Copyright (c) 2020 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.datastore.compare;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.compare.CompareSetResult;
import com.b2international.snowowl.core.compare.CompareSets;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;

/**
 * @since 7.8
 */
public final class ReferenceSetCompare implements CompareSets<SnomedReferenceSetMember, SnomedReferenceSet> {

	@Override
	public CompareSetResult<SnomedReferenceSetMember> doCompare(SnomedReferenceSet baseRf, SnomedReferenceSet compareRf) {
		if (!SnomedRefSetType.SIMPLE_MAP.equals(baseRf.getType()) || !SnomedRefSetType.SIMPLE_MAP.equals(compareRf.getType())) {
			throw new BadRequestException("Comparing Reference Sets requires the following Reference Sets (%s, %s) to be Simple Map Reference Sets!", baseRf.getId(), compareRf.getId());
		}
		return compareDifferents(baseRf.getMembers().getItems(), compareRf.getMembers().getItems());
	}

	@Override
	public boolean isSourceEqual(SnomedReferenceSetMember memberA, SnomedReferenceSetMember memberB) {
		if (memberA.getReferencedComponent().getId().equals(memberB.getReferencedComponent().getId())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isTargetEqual(SnomedReferenceSetMember memberA, SnomedReferenceSetMember memberB) {
		if (memberA.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET).equals(memberB.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET))) {
			return true;
		}
		return false;
	}

}
