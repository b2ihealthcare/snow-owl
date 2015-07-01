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
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.id.CDOID;

import bak.pcj.list.LongList;
import bak.pcj.map.LongKeyLongMap;
import bak.pcj.set.LongSet;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.SnomedModuleDependencyRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberFragment;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
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
	 * Returns with all the IDs and the allowed term length of the description type SNOMED&nbsp;CT concepts. ID is included in the return set if concept fulfills the followings:
	 * <ul>
	 * <li>Concept is descendant of the {@code Description type (core metadata concept)} concept.</li>
	 * <li>Concept is active</li>
	 * <li>Concept has an active description type reference set member</li>
	 * </ul>
	 * @return a set of description type concept IDs and the allowed description term length.
	 */
	Map<String, Integer> getAvailableDescriptionTypeIdsWithLength(IBranchPath branchPath);
	
	/**
	 * Returns with all the IDs of the description type SNOMED&nbsp;CT concepts. ID is included in the return set if concept fulfills the followings:
	 * <ul>
	 * <li>Concept is descendant of the {@code Description type (core metadata concept)} concept.</li>
	 * <li>Concept is active</li>
	 * <li>Concept has an active description type reference set member</li>
	 * </ul>
	 * @param branchPath
	 * @return a set of description type concept IDs
	 */
	Set<String> getAvailableDescriptionTypeIds(IBranchPath branchPath);

	/**
	 * Returns with a set of allowed SNOMED&nbsp;CT concepts' ID.<br>Concept is allowed as preferred description type concept if 
	 * has an associated active description type reference set member and is the 'Synonym' concept or one of its descendant.
	 * @param branchPath
	 * @return a set of SNOMED&nbsp;CT description type concept identifier that can act as a preferred term of a concept.
	 */
	Set<String> getAvailablePreferredTermIds(IBranchPath branchPath);

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
	 * Returns {@code true} if the specified active description is configured as the preferred one for the currently used language.
	 * @param branchPath
	 * @param description the SNOMED&nbsp;CT description to check. 
	 * @return {@code true} if the active description is the preferred term, otherwise returns {@code false}.
	 */
	boolean isPreferred(final IBranchPath branchPath, final Description description);
	
	/**
	 * Returns with the namespace extension concept ID for a specified namespace extracted from the given SNOMED&nbsp;CT core component ID.
	 * @param branchPath the branch path.
	 * @param componentId the unique ID of the SNOMED&nbsp;CT core component.
	 * @return the extension namespace concept ID, or {@code -1L} if the ID was not found.
	 */
	long getExtensionConceptId(final IBranchPath branchPath, final String componentId);
	
	/**
	 * Returns with an array of labels for components identified by their unique SNOMED&nbsp;CT core component ID.
	 * <br>The component type will be identified from the core component ID.
	 * <p>Clients can make sure that the order of the specified component IDs will be the same as the returning array of label.
	 * <p>The returning array may contain {@code null}s. Clients must take care of them.
	 * <p>If the specified component ID is *NOT* a valid SNOMED&nbsp;CT core component ID, it will be ignored and the associated
	 * label will be {@code null}.
	 * @param branchPath the branch path.
	 * @param componentIds the unique ID of a SNOMED&nbsp;CT core components.
	 * @return an array of labels.
	 * @expert
	 */
	@Nullable String[] getLabels(final IBranchPath branchPath, final String... componentIds);
	
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
	 * This method returns an array containing the followings:
	 * <ul>
	 * <li>Source concept ID.</li>
	 * <li>Type concept ID.</li>
	 * <li>Destination concept ID.</li>
	 * <li>Relationship destination negated. (Empty string if not negated, 'NOT' if negated.)</>
	 * </ul>
	 * Clients can make sure, that the order will *NOT* change. Clients can also make sure, if one of the followings described above is 
	 * *NOT* available, this method rather returns with {@code null}.
	 * <p>This method will return with {@code null} if the given ID is *NOT* a valid SNOMED&nbsp;CT relationship ID.
	 * <p>This method also returns with {@code null} if no relationship can be found in the store with the given ID on the specified branch.
	 * @param branchPath the branch path.
	 * @param relationshipId the unique ID of the SNOMED&nbsp;CT relationship.
	 * @return an array of the source, type and destination concept IDs of the relationship. Also includes negated information.
	 */
	@Nullable String[] getRelationshipProperties(final IBranchPath branchPath, final String relationshipId);
	
	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT component is active. Returns with {@code false} if the 
	 * component is retired. This method also returns with {@code false} if no SNOMED&nbsp;CT component were found 
	 * with the specified unique storage key (CDO ID) on the specified branch.
	 * @param branchPath the branch path.
	 * @param storageKey the unique storage key of the SNOMED&nbsp;CT component.
	 * @return {@code true} if the component is active. Otherwise {@code false}.
	 */
	boolean isActive(final IBranchPath branchPath, final long storageKey);
	
	/**
	 * Returns with a bit set representing the status of a bunch of SNOMED&nbsp;CT components.
	 * A set bit representing an active status of an associated component identified by its unique storage key.
	 * If the bit is unset, then the component is retired. Clients can make sure that specified list of 
	 * storage key and the returning bit set has exactly the same order.
	 * <br>The bit will be unset if no component cannot be found on the specified branch.
	 * @param branchPath the branch path.
	 * @param storageKeys a list of component storage keys.
	 * @return a bit set representing the status of a bunch of components.
	 */
	BitSet isActive(final IBranchPath branchPath, final LongList storageKeys);

	/**
	* Returns with an array of SNOMED&nbsp;CT concept icon IDs for
	* a bunch of SNOMED&nbsp;CT concepts given by their unique SNOMED&nbsp;CT concept ID.
	* <br>Returning array may contain {@code null} elements. Order of the given concept IDs and the returning array
	* of image IDs are the same.
	* @param branchPath the branch path.
	* @param conceptId the concept IDs.
	* @return a map concept concept IDs and associated image concept IDs.
	*/
	@Nullable String[] getIconId(final IBranchPath branchPath, final String... conceptId);
	
	/**
	 * Returns with the unique storage key (CDO ID) of the SNOMED&nbsp;CT description specified with it's unique ID.
	 * This method will return with {@code -1} if no description can be found on the specified branch with the given
	 * description ID. 
	 * @param branchPath the branch path.
	 * @param descriptionId the unique ID of the description.
	 * @return the storage key of the description, or {@code -1} if the description does not exist.
	 */
	long getDescriptionStorageKey(final IBranchPath branchPath, final String descriptionId);
	
	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT description exists with the given unique ID.
	 * @param branchPath the branch path.
	 * @param descriptionId the unique ID of the description.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	boolean descriptionExists(final IBranchPath branchPath, final String descriptionId);

	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT relationship exists with the given unique ID.
	 * @param branchPath the branch path.
	 * @param relationshipId the unique ID of the relationship.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	boolean relationshipExists(final IBranchPath branchPath, final String relationshipId);

	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT core component exists with the given unique ID.
	 * @param branchPath the branch path.
	 * @param componentId the unique ID of the SNOMED&nbsp;CT core component.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	boolean componentExists(final IBranchPath branchPath, final String componentId);
	
	/**
	 * Returns with a collection of {@link IdStorageKeyPair#getId() component ID} - {@link IdStorageKeyPair#getStorageKey() storage key} pairs
	 * for all components in the SNOMED&nbsp;CT ontology on a specified {@link IBranchPath branch}. Retired members are included in the result set.
	 * @param branchPath the branch path.
	 * @param terminologyComponentId the application specific terminology component ID. See: {@link SnomedTerminologyComponentConstants#REFSET_MEMBER_NUMBER}.
	 * @return a collection of component ID - storage key pairs. 
	 * @see #getAllMemberIdStorageKeys(IBranchPath, short)
	 */
	Collection<IdStorageKeyPair> getAllComponentIdStorageKeys(final IBranchPath branchPath, final short terminologyComponentId);

	/**
	 * Returns with a collection of {@link IdStorageKeyPair#getId() UUID} - {@link IdStorageKeyPair#getStorageKey() storage key} pairs
	 * for all reference set members that belong to the given {@link SnomedRefSetType SNOMED&nbsp;CT reference set type} given as an ordinal
	 * @param branchPath the branch path.
	 * @param refSetTypeOrdinal the reference set type
	 * @return a collection of reference set member UUID - storage key pairs. 
	 */
	Collection<IdStorageKeyPair> getAllMemberIdStorageKeys(final IBranchPath branchPath, final int refSetTypeOrdinal);
	
	/**
	 * Returns with a collection of reference set member storage keys (CDO IDs) where a component given its unique {@code componentId}
	 * is either the referenced component or depending on the {@link SnomedRefSetType type ordinal} is the target component.
	 * <br>(e.g.: map target for simple map reference set member, value in case of attribute value type, etc.)  
	 * @param branchPath the branch path.
	 * @param componentId the component ID.
	 * @param typeOrdinal the ordinal of the SNOMED&nbsp;CT reference set {@link SnomedRefSetType type}.
	 * @param otherTypeOrdinal additional reference set types.
	 * @return a collection of reference set member storage keys.
	 */
	LongSet getAllReferringMembersStorageKey(final IBranchPath branchPath, final String componentId, final int typeOrdinal, final int... otherTypeOrdinal);
	
	/**
	 * Returns with the a set of SNOMED CT IDs for all description.
	 * @param branchPath the branch path.
	 * @return a set of IDs for all descriptions in the ontology.
	 */
	LongSet getAllDescriptionIds(final IBranchPath branchPath);
	
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
	 * Returns with the identifier concept IDs of all available SNOMED&nbsp;CT reference sets.
	 * @param branchPath the branch path.
	 * @return a set containing all reference set identifier concept IDs.
	 */
	LongSet getAllRefSetIds(final IBranchPath branchPath);
	
	/**
	 * Returns with a map of SNOMED&nbsp;CT concept IDs and the associated terms of the descriptions given as the description type IDs from
	 * a reference set.
	 * @param branchPath branch path.
	 * @param refSetId the reference set ID.
	 * @param descriptionTypeId the description type IDs. Optional, if omitted the PT of the concept will be returned as the term.
	 * @return a map of concept IDs and the associated description terms from a given type of descriptions.
	 */
	Map<String, String> getReferencedConceptTerms(final IBranchPath branchPath, final String refSetId, final String... descriptionTypeId);
	
	/**
	 * Returns with a pair of reference set member referenced component label and map target component label.
	 * The map target component label is optional, and could be {@code null}.
	 * @param branchPath the branch path.
	 * @param refSetId the reference set ID.
	 * @return a set of referenced component and map target component label pairs.
	 */
	Set<Pair<String, String>> getReferenceSetMemberLabels(final IBranchPath branchPath, final String refSetId);
	
	/**
	 * Returns with a pair of referenced component and target component label for a reference set member.
	 * If the reference set member does not belong to a mapping type reference set, then the map target component
	 * label could be {@code null}. It returns with {@code null} {@link Pair} if the reference set does not exist.
	 * @param branchPath the branch path.
	 * @param uuid the reference set member UUID.
	 * @return a pair of referenced component and map type component labels.
	 */
	Pair<String, String> getMemberLabel(final IBranchPath branchPath, final String uuid);
	
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
	 * Returns with a multimap of concept preferred terms and the concept IDs. The mapping will be collected 
	 * for the focus concept argument (inclusive) and all its descendant concepts.
	 * @param branchPath the branch path for the operation.
	 * @param focusConceptId the ID of the concept and its all descendant which preferred term to ID mapping has to be created.
	 */
	Multimap<String, String> getPreferredTermToIdsMapping(final IBranchPath branchPath, final String focusConceptId);
	
	/**
	 * Returns with a mapping between concept FSNs and the concept IDs for all active concepts.
	 * @param branchPath the branch path for the operation.
	 * @param languageRefSetId the ID the language reference set which FSNs will be collected.
	 */
	Multimap<String, String> getFullySpecifiedNameToIdsMapping(final IBranchPath branchPath, final String languageRefSetId);
	
	/**
	 * @param branchPath
	 * @return
	 */
	Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> getPredicates(final IBranchPath branchPath);
	
	/**
	 * @param branchPath
	 * @param conceptId
	 * @param languageRefSetId
	 * @return
	 */
	Map<String, Boolean> getDescriptionPreferabilityMap(IBranchPath branchPath, String conceptId, String languageRefSetId);
	
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
	 * Returns with a mapping between the CDO ID and identifier concept ID for all reference sets.
	 * @param branchPath branch path for the reference set visibility.
	 * @return a mapping between reference set CDO IDs and identifier concept IDs.
	 */
	Map<CDOID, String> getRefSetCdoIdIdMapping(final IBranchPath branchPath);
	
	/**
	 * Returns with a set of all core SNOMED&nbsp;CT component IDs. The returning set 
	 * includes the IDs of the retired components.
	 * @param branchPath the branch path for the component visibility.
	 * @return a set of all core component IDs.
	 */
	LongSet getAllCoreComponentIds(final IBranchPath branchPath);
	
	/**
	 * Returns with description inactivation reference set member's value ID associated with the
	 * given SNOMED&nbsp;CT description if any. May return with {@code null} if the description
	 * does not exist, active or even if no reason was specified for the inactivation process.
	 * @param branchPath the branch path for the visibility.
	 * @param descriptionId the description ID.
	 * @return the description inactivation ID or {@code null} if does not exist or the description
	 * has not been retired.
	 */
	@Nullable String getDescriptionInactivationId(final IBranchPath branchPath, final String descriptionId);
	
	/**
	 * Returns with some statistical information about the underlying ontology such as 
	 * the number of active concepts, description, defining and non-defining properties
	 * and concrete domains.
	 * @param branchPath the branch path for the ontology visibility.
	 * @return statistical information as a string.
	 */
	String getOntologyStatistics(final IBranchPath branchPath);
	
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