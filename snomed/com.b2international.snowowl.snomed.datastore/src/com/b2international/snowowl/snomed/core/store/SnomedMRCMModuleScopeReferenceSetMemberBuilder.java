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
package com.b2international.snowowl.snomed.core.store;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;

/**
 * @since 6.5
 */
public class SnomedMRCMModuleScopeReferenceSetMemberBuilder extends SnomedMemberBuilder<SnomedMRCMModuleScopeReferenceSetMemberBuilder, SnomedMRCMModuleScopeRefSetMember> {

	private String mrcmRuleRefsetId;

	public SnomedMRCMModuleScopeReferenceSetMemberBuilder withMRCMRuleRefsetId(final String mrcmRuleRefsetId) {
		this.mrcmRuleRefsetId = mrcmRuleRefsetId;
		return getSelf();
	}

	@Override
	protected SnomedMRCMModuleScopeRefSetMember create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedMRCMModuleScopeRefSetMember();
	}

	@Override
	public void init(final SnomedMRCMModuleScopeRefSetMember component, final TransactionContext context) {
		super.init(component, context);
		component.setMrcmRuleRefsetId(mrcmRuleRefsetId);
	}

}
