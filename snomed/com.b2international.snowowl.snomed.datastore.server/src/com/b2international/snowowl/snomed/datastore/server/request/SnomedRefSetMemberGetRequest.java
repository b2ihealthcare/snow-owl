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

import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.server.converter.SnomedConverters;

/**
 * @since 4.5
 */
final class SnomedRefSetMemberGetRequest extends GetRequest<SnomedReferenceSetMember> {

	SnomedRefSetMemberGetRequest() {
		super("Reference Set Member");
	}
	
	@Override
	protected SnomedReferenceSetMember process(BranchContext context, IComponent<String> component, List<String> expand) {
		// FIXME class cast
		return SnomedConverters.newMemberConverter(context, expand, locales()).convert((SnomedRefSetMemberIndexEntry) component);
	}
	
	@Override
	protected Class<SnomedReferenceSetMember> getReturnType() {
		return SnomedReferenceSetMember.class;
	}
	
	@Override
	protected ILookupService<String, ? extends CDOObject, CDOView> getLookupService() {
		return new SnomedRefSetMemberLookupService();
	}
	
}
