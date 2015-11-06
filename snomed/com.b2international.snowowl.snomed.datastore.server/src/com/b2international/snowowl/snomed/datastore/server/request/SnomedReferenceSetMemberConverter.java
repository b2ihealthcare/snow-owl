/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.server.request;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.server.domain.SnomedReferenceSetMemberImpl;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;

/**
 * @since 4.5
 */
public class SnomedReferenceSetMemberConverter implements Function<SnomedRefSetMemberIndexEntry, SnomedReferenceSetMember> {

	@Override
	public SnomedReferenceSetMember apply(SnomedRefSetMemberIndexEntry input) {
		final SnomedReferenceSetMemberImpl member = new SnomedReferenceSetMemberImpl();
		member.setId(input.getId());
		member.setEffectiveTime(EffectiveTimes.toDate(input.getEffectiveTimeAsLong()));
		member.setReleased(input.isReleased());
		member.setActive(input.isActive());
		member.setModuleId(input.getModuleId());
		member.setReferencedComponentId(input.getReferencedComponentId());
		member.setReferenceSetId(input.getRefSetIdentifierId());
		return member;
	}

	public SnomedReferenceSetMember apply(SnomedRefSetMember input) {
		final SnomedReferenceSetMemberImpl member = new SnomedReferenceSetMemberImpl();
		member.setId(input.getUuid());
		member.setEffectiveTime(input.getEffectiveTime());
		member.setReleased(input.isReleased());
		member.setActive(input.isActive());
		member.setModuleId(input.getModuleId());
		member.setReferencedComponentId(input.getReferencedComponentId());
		member.setReferenceSetId(input.getRefSetIdentifierId());
		return member;
	}

}
