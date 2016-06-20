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
package com.b2international.snowowl.snomed.datastore.services;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * Interface for retrieving information about SNOMED&nbsp;CT core components on the client side.
 * 
 */
public interface IClientSnomedComponentService {

	/**
	 * Returns with the available concrete domain data type labels for a specified concrete domain data type.
	 * @param dataType the data type. E.g.: {@code BOOLEAN} or {@code DECIMAL}.
	 * @return a set of concrete domain data type labels for a specified data type.
	 */
	Set<String> getAvailableDataTypeLabels(final DataType dataType);

	/**
	 * Returns with a collection of reference set member storage keys (CDO IDs) where a component given its unique {@code componentId}
	 * is either the referenced component or depending on the {@link SnomedRefSetType type} is the target component.
	 * <br>(e.g.: map target for simple map reference set member, value in case of attribute value type, etc.)  
	 * @param componentId the component ID.
	 * @param types the set of the SNOMED CT reference set {@link SnomedRefSetType types}.
	 * @return a collection of reference set member storage keys.
	 */
	LongSet getAllReferringMembersStorageKey(final String componentId, final EnumSet<SnomedRefSetType> types);
	
	/**
	 * Returns with all existing {@link SnomedModuleDependencyRefSetMemberFragment module dependency reference set member}s from the underling ontology.
	 * @return a collection of existing module dependency reference set members.
	 */
	Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules();
	
}