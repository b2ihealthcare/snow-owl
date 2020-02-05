/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 5.0
 */
final class SnomedAssociationMemberUpdateDelegate extends SnomedRefSetMemberUpdateDelegate {

	SnomedAssociationMemberUpdateDelegate(SnomedRefSetMemberUpdateRequest request) {
		super(request);
	}

	@Override
	boolean execute(SnomedRefSetMemberIndexEntry original, SnomedRefSetMemberIndexEntry.Builder member, TransactionContext context) {
		String newTargetComponentId = getComponentId(SnomedRf2Headers.FIELD_TARGET_COMPONENT);

		if (newTargetComponentId != null && !newTargetComponentId.equals(original.getTargetComponent())) {
			member.targetComponent(newTargetComponentId);
			return true;
		} else {
			return false;
		}
	}

}
