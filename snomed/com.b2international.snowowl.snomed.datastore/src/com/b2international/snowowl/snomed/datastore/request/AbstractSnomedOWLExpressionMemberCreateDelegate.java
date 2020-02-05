/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 6.5
 */
abstract class AbstractSnomedOWLExpressionMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	AbstractSnomedOWLExpressionMemberCreateDelegate(final SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	abstract protected SnomedRefSetType getRefsetType();

	@Override
	public String execute(final SnomedReferenceSet refSet, final TransactionContext context) {
		checkRefSetType(refSet, getRefsetType());
		checkReferencedComponent(refSet);
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_OWL_EXPRESSION);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());

		final SnomedRefSetMemberIndexEntry member = SnomedComponents.newOWLExpressionReferenceSetMember()
				.withId(getId())
				.withActive(isActive())
				.withReferencedComponent(getReferencedComponentId())
				.withModule(getModuleId())
				.withRefSet(getReferenceSetId())
				.withOWLExpression(getProperty(SnomedRf2Headers.FIELD_OWL_EXPRESSION))
				.addTo(context);

		return member.getId();
	}

}
