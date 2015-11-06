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

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.5
 */
public class SnomedRefSetMemberCreateRequest extends SnomedRefSetMemberRequest<TransactionContext, SnomedReferenceSetMember> {

	@NotEmpty
	private String moduleId;
	
	@NotEmpty
	private String referenceSetId;
	
	private String referencedComponentId;

	SnomedRefSetMemberCreateRequest() {
	}
	
	public void setReferencedComponentId(String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
	}
	
	public void setReferenceSetId(String referenceSetId) {
		this.referenceSetId = referenceSetId;
	}
	
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	@Override
	public SnomedReferenceSetMember execute(TransactionContext context) {
		final SnomedReferenceSet refSet = new SnomedRefSetReadRequest(referenceSetId).execute(context);
		final SnomedRefSetType type = refSet.getType();
		RefSetSupport.check(type);
		if (referencedComponentId != null) {
			// XXX referenced component ID for query type reference set cannot be defined, validate only if defined
			// TODO support for other terminologies when enabling mappings
			SnomedIdentifiers.validate(referencedComponentId);
			final String referencedComponentType = SnomedTerminologyComponentConstants.getTerminologyComponentId(referencedComponentId);
			RefSetSupport.checkType(type, referencedComponentType);
		}
		
		final SnomedRefSetMember member;

		switch (type) {
		case SIMPLE:
			member = SnomedComponents
				.newSimpleMember()
				.withReferencedComponent(referencedComponentId)
				.withModule(moduleId)
				.withRefSet(referenceSetId)
				.addTo(context);
			break;
		default: throw new UnsupportedOperationException("Not implemented support for creation of '"+type+"' members");
		}
		
		return new SnomedReferenceSetMemberConverter().apply(member);
	}

	@Override
	protected Class<SnomedReferenceSetMember> getReturnType() {
		return SnomedReferenceSetMember.class;
	}

}
