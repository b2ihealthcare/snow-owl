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
import java.util.Set;

import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.snomedrefset.DataType;

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
	 * Returns with all existing {@link SnomedModuleDependencyRefSetMemberFragment module dependency reference set member}s from the underling ontology.
	 * @return a collection of existing module dependency reference set members.
	 */
	Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules();
	
}