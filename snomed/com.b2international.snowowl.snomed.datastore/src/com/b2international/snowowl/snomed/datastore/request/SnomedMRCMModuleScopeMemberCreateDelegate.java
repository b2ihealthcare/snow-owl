/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableSet;

/**
 * @since 6.5
 */
final class SnomedMRCMModuleScopeMemberCreateDelegate extends SnomedRefSetMemberCreateDelegate {

	SnomedMRCMModuleScopeMemberCreateDelegate(final SnomedRefSetMemberCreateRequest request) {
		super(request);
	}

	@Override
	public String execute(final SnomedRefSet refSet, final TransactionContext context) {
		checkRefSetType(refSet, SnomedRefSetType.MRCM_MODULE_SCOPE);
		checkReferencedComponent(refSet);

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MODULE_ID, getModuleId());
		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, getReferencedComponentId());

		checkComponentExists(refSet, context, SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, getProperty(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID));

		final SnomedMRCMModuleScopeRefSetMember member = SnomedComponents.newMRCMModuleScopeReferenceSetMember()
				.withId(getId())
				.withActive(isActive())
				.withModule(getModuleId())
				.withRefSet(getReferenceSetId())
				.withReferencedComponent(getReferencedComponentId())
				.withMRCMRuleRefsetId(getProperty(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID))
				.addTo(context);

		return member.getUuid();
	}

	@Override
	protected Set<String> getRequiredComponentIds() {
		checkNonEmptyProperty(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID);
		return ImmutableSet.of(getComponentId(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID));
	}

}
