/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 6.14
 */
public class DescriptionTypeMemberEffectiveTimeRestorer extends MemberEffectiveTimeRestorer {

	@Override
	protected boolean canRestoreMemberEffectiveTime(SnomedRefSetMember memberToRestore, SnomedReferenceSetMember previousMember) {
		final SnomedDescriptionTypeRefSetMember descriptionTypeMemberToRestore = (SnomedDescriptionTypeRefSetMember) memberToRestore;

		final String previousDescriptionFormat = (String) previousMember.getProperties().get(SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT);
		final Integer previousDescriptionLength = (Integer) previousMember.getProperties().get(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH);

		if (previousDescriptionFormat != null && !previousDescriptionFormat.equals(descriptionTypeMemberToRestore.getDescriptionFormat())) {
			return false;
		}

		if (previousDescriptionLength != null && previousDescriptionLength.intValue() != descriptionTypeMemberToRestore.getDescriptionLength()) {
			return false;
		}
		
		return true;
	}

}
