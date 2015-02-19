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

import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.cdo.CDOState;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 *
 */
public class SnomedRefSetMemberAdapterFactory implements IAdapterFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IComponent.class == adapterType) {
			if (adaptableObject instanceof SnomedRefSetMemberIndexEntry) {
				return adaptableObject;
			} else if (adaptableObject instanceof SnomedRefSetMember) {
				final SnomedClientIndexService indexSearcher = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
				final SnomedRefSetMember member = (SnomedRefSetMember) adaptableObject;
				final List<SnomedRefSetMemberIndexEntry> result = indexSearcher.search(
						SnomedRefSetMembershipIndexQueryAdapter.createFindByUuidQuery(member.getUuid()), 1);
				switch (result.size()) {
					case 1: return result.get(0);
					case 0: 
						if (CDOState.NEW.equals(member.cdoState())) {
							return SnomedRefSetMemberIndexEntry.createForNewMember(member);
						} else if (CDOState.DIRTY.equals(member.cdoState())) {
							return SnomedRefSetMemberIndexEntry.create(member);
						}
					default: throw new RuntimeException("Non-unique SNOMED CT reference set member was found by the '" + member.getUuid() + "' identifier.");
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class[] getAdapterList() {
		return new Class[] { IComponent.class };
	}

}