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
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService.IdStorageKeyPair;
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
	 * Returns with all the IDs and the allowed term length of the description type SNOMED&nbsp;CT concepts. ID is included in the return set if concept fulfills the followings:
	 * <ul>
	 * <li>Concept is descendant of the {@code Description type (core metadata concept)} concept.</li>
	 * <li>Concept is active</li>
	 * <li>Concept has an active description type reference set member</li>
	 * </ul>
	 * @return a set of description type concept IDs and the allowed description term length.
	 */
	Map<String, Integer> getAvailableDescriptionTypeIdsWithLength();
	
	/**
	 * Returns with all the IDs of the description type SNOMED&nbsp;CT concepts. ID is included in the return set if concept fulfills the followings:
	 * <ul>
	 * <li>Concept is descendant of the {@code Description type (core metadata concept)} concept.</li>
	 * <li>Concept is active</li>
	 * <li>Concept has an active description type reference set member</li>
	 * </ul>
	 * @return a set of description type concept IDs
	 */
	Set<String> getAvailableDescriptionTypeIds();

	/**
	 * Returns with a set of allowed SNOMED&nbsp;CT concepts' ID.<br>Concept is allowed as preferred description type concept if 
	 * has an associated active description type reference set member and is the 'Synonym' concept or one of its descendant.
	 * @return a set of SNOMED&nbsp;CT description type concept identifier that can act as a preferred term of a concept.
	 */
	Set<String> getAvailablePreferredTermIds();

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
	 * Returns {@code true} if the specified active description is configured as the preferred one for the currently used language.
	 * @param description the SNOMED&nbsp;CT description to check. 
	 * @return {@code true} if the active description is the preferred term, otherwise returns {@code false}.
	 */
	boolean isPreferred(final Description description);
	
	/**
	 * Returns with the namespace extension concept ID for a specified namespace extracted from the given SNOMED&nbsp;CT core component ID.
	 * @param componentId the unique ID of the SNOMED&nbsp;CT core component.
	 * @return the extension namespace concept ID, or {@code -1L} if the ID was not found.
	 */
	long getExtensionConceptId(final String componentId);
	
	/**
	* Returns with an array of SNOMED&nbsp;CT concept icon IDs for
	* a bunch of SNOMED&nbsp;CT concepts given by their unique SNOMED&nbsp;CT concept ID.
	* <br>Returning array may contain {@code null} elements. Order of the given concept IDs and the returning array
	* of image IDs are the same.
	* @param conceptId the concept IDs.
	* @return a map concept concept IDs and associated image concept IDs.
	*/
	String[] getIconId(final String... conceptId);
	
	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT description exists with the given unique ID.
	 * @param descriptionId the unique ID of the description.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	boolean descriptionExists(final String descriptionId);
	
	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT relationship exists with the given unique ID.
	 * @param relationshipId the unique ID of the relationship.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	boolean relationshipExists(final String relationshipId);
	
	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT core component exists with the given unique ID.
	 * @param componentId the unique ID of the SNOMED&nbsp;CT core component.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	boolean componentExists(final String componentId);
	
	/**
	 * Returns with a collection of {@link IdStorageKeyPair#getId() component ID} - {@link IdStorageKeyPair#getStorageKey() storage key} pairs
	 * for all components in the SNOMED&nbsp;CT ontology on the currently active {@link IBranchPath branch}. Retired members are included in the result set.
	 * @param terminologyComponentId the application specific terminology component ID. See: {@link SnomedTerminologyComponentConstants#REFSET_MEMBER_NUMBER}.
	 * @return a collection of component ID - storage key pairs. 
	 * @see #getAllMemberIdStorageKeys(IBranchPath, short)
	 */
	Collection<IdStorageKeyPair> getAllComponentIdStorageKeys(final short terminologyComponentId);

	/**
	 * Returns with a collection of {@link IdStorageKeyPair#getId() UUID} - {@link IdStorageKeyPair#getStorageKey() storage key} pairs
	 * for all reference set members that belong to the given {@link SnomedRefSetType SNOMED&nbsp;CT reference set type} given as an ordinal
	 * @param refSetTypeOrdinal the reference set type
	 * @return a collection of reference set member UUID - storage key pairs. 
	 */
	Collection<IdStorageKeyPair> getAllMemberIdStorageKeys(final int refSetTypeOrdinal);
	
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
	 * Returns with a collection of all active {@link SnomedDescriptionIndexEntry} for the current branch.
	 * @return a collection of {@link SnomedDescriptionIndexEntry}
	 */
	Collection<SnomedDescriptionIndexEntry> getAllActiveDescriptionEntry();
	
	/**
	 * Returns with a collection of active {@link SnomedDescriptionFragment description}s for a concept which are belongs to the 
	 * given language.
	 * @param conceptId the container concept ID.
	 * @param languageRefSetId the unique language reference set concept identifier.
	 * @return a collection of active descriptions for a concept in a given language.
	 */
	Collection<SnomedDescriptionFragment> getDescriptionFragmentsForConcept(final String conceptId, final String languageRefSetId);
	
	/**
	 * Returns with the identifier concept IDs of all available SNOMED&nbsp;CT reference sets.
	 * @return a set containing all reference set identifier concept IDs.
	 */
	LongSet getAllRefSetIds();
	
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
	 * Returns with a collection of {@link SnomedRefSetMemberFragment reference set member}s contained by the given reference set. 
	 * @param refSetId the reference set identifier concept ID.
	 * @return a collection of reference set members from a given reference set.
	 */
	Collection<SnomedRefSetMemberFragment> getRefSetMemberFragments(final String refSetId);
	
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
	
	/**
	 * Returns with a map containing every module of the Module Dependency reference set and the latest effective time for the module.
	 * The provided date could be <code>null</code>.
	 * @return a map where the key is the module ID and the value is the module's latest effective time
	 */
	Map<String, Date> getExistingModulesWithEffectiveTime();
	
}