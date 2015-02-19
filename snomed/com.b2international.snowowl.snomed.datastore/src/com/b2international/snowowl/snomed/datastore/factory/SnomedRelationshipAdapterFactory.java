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

import org.eclipse.core.runtime.IAdapterFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipIndexEntry;

/**
 *
 */
public class SnomedRelationshipAdapterFactory implements IAdapterFactory {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object getAdapter(final Object adaptableObject, final Class adapterType) {
		if (IComponent.class == adapterType) {
			if (adaptableObject instanceof SnomedRelationshipIndexEntry) {
				return adaptableObject;
			} else if (adaptableObject instanceof Relationship) {
				SnomedRelationshipIndexEntry statement = ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class).getStatement(((Relationship) adaptableObject).getId());
				if (null == statement) {
					final Relationship relationship = (Relationship) adaptableObject;
					statement = new SnomedRelationshipIndexEntry(
									relationship.getId(), 
									relationship.getSource().getId(), 
									relationship.getType().getId(), 
									relationship.getDestination().getId(), 
									relationship.getCharacteristicType().getId(),
									CDOIDUtils.asLong(relationship.cdoID()),
									relationship.getModule().getId(),
									(byte)relationship.getGroup(),
									(byte)relationship.getUnionGroup(),
									SnomedRelationshipIndexEntry.generateFlags(
											relationship.isReleased(),
											relationship.isActive(),
											SnomedConstants.Concepts.INFERRED_RELATIONSHIP.equals(relationship.getCharacteristicType().getId()), 
											SnomedConstants.Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship.getModifier().getId()),
											relationship.isDestinationNegated()),
									relationship.getEffectiveTime() == null ? EffectiveTimes.UNSET_EFFECTIVE_TIME : relationship.getEffectiveTime().getTime());
					
				}
				return statement;
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