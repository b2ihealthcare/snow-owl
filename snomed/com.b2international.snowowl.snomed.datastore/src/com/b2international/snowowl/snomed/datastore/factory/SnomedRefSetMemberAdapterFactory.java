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
package com.b2international.snowowl.snomed.datastore.factory;

import org.eclipse.emf.spi.cdo.FSMUtil;

import com.b2international.commons.TypeSafeAdapterFactory;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * Adapter factory implementation for SNOMED CT reference set members.
 */
public class SnomedRefSetMemberAdapterFactory extends TypeSafeAdapterFactory {

	public SnomedRefSetMemberAdapterFactory() {
		super(IComponent.class, SnomedRefSetMemberIndexEntry.class);
	}

	@Override
	protected <T> T getAdapterSafe(final Object adaptableObject, final Class<T> adapterType) {

		if (adaptableObject instanceof SnomedRefSetMemberIndexEntry) {
			return adapterType.cast(adaptableObject);
		} 

		if (adaptableObject instanceof SnomedRefSetMember) {

			final SnomedRefSetMember refSetMember = (SnomedRefSetMember) adaptableObject;
			final SnomedRefSetMemberIndexEntry refSetMemberIndexEntry;

			if (FSMUtil.isClean(refSetMember) && !refSetMember.cdoRevision().isHistorical()) {
				refSetMemberIndexEntry = new SnomedRefSetMemberLookupService().getComponent(BranchPathUtils.createPath(refSetMember), refSetMember.getUuid());
			} else {
				refSetMemberIndexEntry = SnomedRefSetMemberIndexEntry.builder(refSetMember).build();
			}

			return adapterType.cast(refSetMemberIndexEntry);
		}

		return null;
	}

}
