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

import java.util.Date;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Objects;

/**
 * @since 6.14
 */
public class ModuleDependencyMemberEffectiveTimeRestorer extends MemberEffectiveTimeRestorer {

	@Override
	protected boolean canRestoreMemberEffectiveTime(SnomedRefSetMember memberToRestore, SnomedReferenceSetMember previousMember) {
		final SnomedModuleDependencyRefSetMember moduleDependencyMemberToRestore = (SnomedModuleDependencyRefSetMember) memberToRestore;

		if (previousMember.getProperties().containsKey(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME)) {
			final Date previousSourceEffectiveDate = (Date) previousMember.getProperties().get(SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME);

			if (!Objects.equal(previousSourceEffectiveDate, moduleDependencyMemberToRestore.getSourceEffectiveTime())) {
				return false;
			}
		}

		if (previousMember.getProperties().containsKey(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME)) {
			final Date previousTargetEffectiveDate = (Date) previousMember.getProperties().get(SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME);

			if (!Objects.equal(previousTargetEffectiveDate, moduleDependencyMemberToRestore.getTargetEffectiveTime())) {
				return false;
			}
		}

		return true;
	}


}
