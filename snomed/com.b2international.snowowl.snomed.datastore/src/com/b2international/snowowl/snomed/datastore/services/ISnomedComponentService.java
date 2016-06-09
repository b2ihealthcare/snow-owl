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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.Pair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;

/**
 * Interface for retrieving information about SNOMED&nbsp;CT core components on the server side.
 * 
 */
public interface ISnomedComponentService {
	
	/**
	 * Warms the underlying cache.
	 * @param branchPath
	 */
	void warmCache(IBranchPath branchPath);

	/**
	 * Returns with the available concrete domain data type labels for a specified concrete domain data type.
	 * @param branchPath
	 * @param dataType the data type. E.g.: {@code BOOLEAN} or {@code DECIMAL}.
	 * @return a set of concrete domain data type labels for a specified data type.
	 */
	Set<String> getAvailableDataTypeLabels(IBranchPath branchPath, final DataType dataType);

	/**
	 * Returns with a set of SNOMED&nbsp;CT concept IDs containing the 'Synonym' concept (ID:&nbsp;900000000000013009) and all descendant IDs.
	 * @param branchPath
	 * @return the 'Synonym' concept and all descendant IDs.
	 */
	Set<String> getSynonymAndDescendantIds(IBranchPath branchPath);
	
	/**
	 * Returns with a collection of active {@link SnomedDescriptionFragment description}s for a concept, all belonging to the given language.
	 * @param branchPath branch path.
	 * @param conceptId the container concept ID.
	 * @param languageRefSetId the unique language reference set concept identifier.
	 * @return a collection of active descriptions for a concept in a given language.
	 */
	Collection<SnomedDescriptionFragment> getDescriptionFragmentsForConcept(final IBranchPath branchPath, final String conceptId, final String languageRefSetId);
	
	/**
	 * This method returns with an array of the owner SNOMED&nbsp;CT concept ID description type concept ID and the term of the description specified 
	 * by its unique description ID. If either the concept ID, type ID or the term cannot be specified, this method returns with {@code null}.
	 * <p>Clients can make sure, if the returning array can be referenced, then both IDs and the term is available as well.
	 * <p>This method will return with {@code null} if the given ID is *NOT* a valid SNOMED&nbsp;CT description ID.
	 * <p>This method also returns with {@code null} if no description can be found in the store with the given ID on the specified branch.
	 * @param branchPath the branch path.
	 * @param descriptionId the unique ID of the SNOMED&nbsp;CT description.
	 * @return an array of SNOMED&nbsp;CT concept ID, description type ID and the term of the description.
	 */
	@Nullable String[] getDescriptionProperties(final IBranchPath branchPath, final String descriptionId);
	
	/**
	 * Collects the following information from all descriptions in the index (in order of appearance in the array):
	 * <ul>
	 * <li>Description ID.</li>
	 * <li>Associated concept ID.</li>
	 * <li>Description type ID.</li>
	 * <li>Description label.</li>
	 * <li>Description storage key.</li>
	 * </ul>
	 * Collected data is returned as an array of arrays of the above form. If no  descriptions can be found in the index, the
	 * method returns {@code null}.
	 * @param branchPath the branch path to collect information from (may not be {@code null})
	 * @return an array of description properties, or {@code null}
	 */
	@Nullable String[][] getAllDescriptionProperties(final IBranchPath branchPath);
	
	/**
	 * Returns with a collection of {@link IdStorageKeyPair#getId() component ID} - {@link IdStorageKeyPair#getStorageKey() storage key} pairs
	 * for all components in the SNOMED&nbsp;CT ontology on a specified {@link IBranchPath branch}. Retired members are included in the result set.
	 * @param branchPath the branch path.
	 * @param terminologyComponentId the application specific terminology component ID. See: {@link SnomedTerminologyComponentConstants#REFSET_MEMBER_NUMBER}.
	 * @return a collection of component ID - storage key pairs. 
	 */
	Collection<IdStorageKeyPair> getAllComponentIdStorageKeys(final IBranchPath branchPath, final short terminologyComponentId);

	/**
	 * Returns with a collection of reference set member storage keys (CDO IDs) where a component given its unique {@code componentId}
	 * is either the referenced component or depending on the {@link SnomedRefSetType type} is the target component.
	 * <br>(e.g.: map target for simple map reference set member, value in case of attribute value type, etc.)  
	 * @param branchPath the branch path.
	 * @param componentId the component ID.
	 * @param types the set of the SNOMED CT reference set {@link SnomedRefSetType types}.
	 * @return a collection of reference set member storage keys.
	 */
	LongSet getAllReferringMembersStorageKey(final IBranchPath branchPath, final String componentId, final EnumSet<SnomedRefSetType> types);
	
	/**
	 * Returns with a collection of the reference set member's referenced component storage keys.  
	 * 
	 * @param branchPath the branch path.
	 * @param refSetId the Id of the reference set.
	 * @param referencedComponentType the type of the reference set member's referenced component.
	 * @return a collection of component storage keys.
	 */
	LongSet getComponentByRefSetIdAndReferencedComponent(final IBranchPath branchPath, final String refSetId, final short referencedComponentType);
	
	/**
	 * Returns with a map of SNOMED&nbsp;CT concept IDs and the associated terms of the descriptions given as the description type IDs from
	 * a reference set.
	 * @param branchPath branch path.
	 * @param refSetId the reference set ID.
	 * @param descriptionTypeId the description type IDs. Optional, if omitted the PT of the concept will be returned as the term.
	 * @return a map of concept IDs and the associated description terms from a given type of descriptions.
	 */
	@Deprecated
	Map<String, String> getReferencedConceptTerms(final IBranchPath branchPath, final String refSetId, final String... descriptionTypeId);
	
	/**
	 * Returns with a collection of {@link SnomedRefSetMemberFragment reference set member}s contained by the given reference set. 
	 * @param branchPath the branch path.
	 * @param refSetId the reference set identifier concept ID.
	 * @return a collection of reference set members from a given reference set.
	 */
	Collection<SnomedRefSetMemberFragment> getRefSetMemberFragments(final IBranchPath branchPath, final String refSetId);
	
	/**
	 * Returns with the storage keys of all unpublished concepts, descriptions and relationships from the ontology.
	 * <p>Unpublished components are those components where the effective time of the component is not set.
	 * @param branchPath the branch path.
	 * @return a collection of unpublished core component storage keys.
	 */
	LongSet getAllUnpublishedComponentStorageKeys(final IBranchPath branchPath);

	/**
	 * Returns with the mapping between all concepts' ID and their corresponding module concept IDs.
	 * @param branchPath the branch path.
	 * @return a map of concept and module concept IDs.
	 */
	LongKeyLongMap getConceptModuleMapping(final IBranchPath branchPath);
	
	/**
	 * Returns with a multimap or SNOMED&nbsp;CT component IDs and the associated concrete domain values for the 
	 * concrete domain given with the (camel case) concrete domain name argument.
	 * @param branchPath the branch path.
	 * @param concreteDomainName the unique, camel case concrete domain name. 
	 * @return a multimap of component IDs and the associated concrete domain values.
	 */
	<V> Multimap<String, V> getAllConcreteDomainsForName(final IBranchPath branchPath, final String concreteDomainName);
	
	/**
	 * @param branchPath
	 * @return
	 */
	Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> getPredicates(final IBranchPath branchPath);
	
	/**
	 * @param branchPath
	 * @param conceptId
	 * @return
	 */
	Map<String, Multimap<String, String>> getDescriptionPreferabilityMap(IBranchPath branchPath, String conceptId);
	
	/**
	 * Returns with all existing {@link SnomedModuleDependencyRefSetMemberFragment module dependency reference set member}s from the underling ontology.
	 * @param branchPath the branch path.
	 * @return a collection of existing module dependency reference set members.
	 */
	Collection<SnomedModuleDependencyRefSetMemberFragment> getExistingModules(final IBranchPath branchPath);
	
	/**
	 * Returns with a map containing every module of the Module Dependency reference set and the latest effective time for the module.
	 * The provided date could be <code>null</code>.
	 * @param branchPath the branch path.
	 * @return a map where the key is the module ID and the value is the module's latest effective time
	 */
	Map<String, Date> getExistingModulesWithEffectiveTime(IBranchPath branchPath);
	
	/**
	 * Returns with a set of concept storage keys that have to be inactivated when retiring the concept concepts
	 * argument.
	 * @param branchPath the branch path for the calculation.
	 * @param focusConceptIds the concept IDs to retire with their descendants.
	 * @return a collection of concept storage keys to inactivate.
	 */
	LongSet getSelfAndAllSubtypeStorageKeysForInactivation(final IBranchPath branchPath, final String... focusConceptIds);
	
	/**
	 * Returns with a set of all core SNOMED&nbsp;CT component IDs. The returning set 
	 * includes the IDs of the retired components.
	 * @param branchPath the branch path for the component visibility.
	 * @return a set of all core component IDs.
	 */
	LongSet getAllCoreComponentIds(final IBranchPath branchPath);
	
	/**
	 * Serializable representation of a component ID and storage key pair.
	 */
	public static final class IdStorageKeyPair implements Serializable {
		
		/**Function for extracting the component ID from the {@link IdStorageKeyPair}.*/
		public static final Function<IdStorageKeyPair, String> GET_ID_FUNCTION = new Function<ISnomedComponentService.IdStorageKeyPair, String>() {
			private static final String MSG = "ID with storage key pair argument cannot be null.";
			@Override public String apply(final IdStorageKeyPair idStorageKeyPair) {
				return Preconditions.checkNotNull(idStorageKeyPair, MSG).id;
			}
		};
		
		private static final long serialVersionUID = -2748053756520925845L;
		private final String id;
		private final long storageKey;
		/**Creates a new instance.*/
		public IdStorageKeyPair(final String id, final long storageKey) { this.id = id; this.storageKey = storageKey; }
		/**Returns with the terminology specific component ID.<p>Clients should not parse it to long as it could be UUID as well.*/
		public String getId() { return id; }
		/**The repository specific primary storage key of the component.*/
		public long getStorageKey() { return storageKey; }
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + (int) (storageKey ^ (storageKey >>> 32));
			return result;
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof IdStorageKeyPair))
				return false;
			final IdStorageKeyPair other = (IdStorageKeyPair) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (storageKey != other.storageKey)
				return false;
			return true;
		}
		
	}

}