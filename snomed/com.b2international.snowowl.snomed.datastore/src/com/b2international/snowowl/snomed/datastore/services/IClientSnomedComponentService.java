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
import java.util.Map;
import java.util.Set;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Multimap;

/**
 * Interface for retrieving information about SNOMED&nbsp;CT core components on the client side.
 * 
 */
public interface IClientSnomedComponentService {

	/**
	 * Warms the underlying cache.
	 */
	void warmCache();

	/**
	 * Returns with the available concrete domain data type labels for a specified concrete domain data type.
	 * @param dataType the data type. E.g.: {@code BOOLEAN} or {@code DECIMAL}.
	 * @return a set of concrete domain data type labels for a specified data type.
	 */
	Set<String> getAvailableDataTypeLabels(final DataType dataType);

	/**
	 * Returns with a set of SNOMED&nbsp;CT concept IDs containing the 'Synonym' concept (ID:&nbsp;900000000000013009) and all descendant IDs.
	 * @return the 'Synonym' concept and all descendant IDs.
	 */
	Set<String> getSynonymAndDescendantIds();
	
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
	 * Returns with the a set of SNOMED CT IDs for all description.
	 * @return a set of IDs for all descriptions in the ontology.
	 */
	LongSet getAllDescriptionIds();
	
	/**
	 * Returns with a collection of active {@link SnomedDescriptionFragment description}s for a concept which are belongs to the 
	 * given language.
	 * @param conceptId the container concept ID.
	 * @param languageRefSetId the unique language reference set concept identifier.
	 * @return a collection of active descriptions for a concept in a given language.
	 */
	Collection<SnomedDescriptionFragment> getDescriptionFragmentsForConcept(final String conceptId, final String languageRefSetId);
	
	/**
	 * Returns with a map of SNOMED&nbsp;CT concept IDs and the associated terms of the descriptions given as the description type IDs from
	 * a reference set.
	 * @param refSetId the reference set ID.
	 * @param descriptionTypeId the description type IDs. Optional, if omitted the PT of the concept will be returned as the term.
	 * @return a map of concept IDs and the associated description terms from a given type of descriptions.
	 */
	@Deprecated
	Map<String, String> getReferencedConceptTerms(final String refSetId, final String... descriptionTypeId);
	
	/**
	 * Returns with a multimap or SNOMED&nbsp;CT component IDs and the associated concrete domain values for the 
	 * concrete domain given with the (camel case) concrete domain name argument.
	 * @param concreteDomainName the unique, camel case concrete domain name. 
	 * @return a multimap of component IDs and the associated concrete domain values.
	 */
	<V> Multimap<String, V> getAllConcreteDomainsForName(final String concreteDomainName);
	
	/**
	 * Returns with all existing {@link SnomedModuleDependencyRefSetMemberFragment module dependency reference set member}s from the underling ontology.
	 * @return a collection of existing module dependency reference set members.
	 */
	Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules();
	
}