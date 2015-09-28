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
package com.b2international.snowowl.snomed.datastore.index.refset;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * @since 4.3
 */
public class RefSetMemberImmutablePropertyUpdater extends DocumentUpdaterBase<SnomedDocumentBuilder> {

	private SnomedRefSetMember member;

	public RefSetMemberImmutablePropertyUpdater(SnomedRefSetMember member) {
		super(member.getUuid());
		this.member = member;
	}

	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
		doc
			.storageKey(CDOIDUtil.getLong(member.cdoID()))
			.field(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UUID, getComponentId())
			.memberRefSetId(member.getRefSetIdentifierId())
			.memberRefSetType(member.getRefSet().getType())
			.memberReferencedComponentId(member.getReferencedComponentId())
			.memberReferencedComponentType(member.getReferencedComponentType());
	}

}
