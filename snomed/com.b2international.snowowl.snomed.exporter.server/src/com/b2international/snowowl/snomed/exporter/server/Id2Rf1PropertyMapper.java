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
package com.b2international.snowowl.snomed.exporter.server;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import javax.annotation.Nullable;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.collect.Maps;

/**
 * Class for mapping CDO IDs or SNOMED&nbsp;CT concept identifiers to RF1 properties.
 */
public class Id2Rf1PropertyMapper {

	private Map<String, String> conceptStatusMap;
	private Map<String, String> descriptionStatusMap;
	private Map<String, String> conceptDefinitionStatusMap;
	private Map<String, String> initialCapitalStatusMap;
	private Map<String, String> descriptionTypeMap;
	private Map<String, String> relationshipTypeMap;
	private Map<String, String> refinabilityTypeMap;
	private Map<String, String> extendedDescriptionTypeMap;
	
	/**
	 * Creates a new mapper instance with a given branch ID.
	 * @param branchPath the branch ID where the mapper instance will be used.
	 */
	public Id2Rf1PropertyMapper() {
		conceptDefinitionStatusMap = initDefinitionStatusCache();
		refinabilityTypeMap = initRefinabilityTypeCache();
		relationshipTypeMap = initRelationshipTypeCache();
		descriptionTypeMap = initDescriptionTypeCache();
		extendedDescriptionTypeMap = initExtendedDescriptionTypeCache();
		initialCapitalStatusMap = initInitialiCapitalStatusCache();
		conceptStatusMap = initConceptStatusCache();
		descriptionStatusMap = initDescriptionStatusCache();
	}

	@Nullable
	public String getConceptStatusProperty(@Nullable final String conceptId) {
		return conceptStatusMap.get(conceptId);
	}
	
	@Nullable
	public String getDescriptionStatusProperty(@Nullable final String conceptId) {
		return descriptionStatusMap.get(conceptId);
	}
	
	@Nullable
	public String getConceptDefinitionStatus(@Nullable final String storageKey) {
		return conceptDefinitionStatusMap.get(storageKey);
	}
	
	@Nullable
	public String getInitialCapitalStatus(@Nullable final String storageKey) {
		return initialCapitalStatusMap.get(storageKey);
	}

	@Nullable
	public String getDescriptionType(@Nullable final String storageKey) {
		return descriptionTypeMap.get(storageKey);
	}
	
	@Nullable
	public String getExtendedDescriptionType(@Nullable final String conceptId) {
		return extendedDescriptionTypeMap.get(conceptId);
	}
	
	@Nullable
	public String getRelationshipType(@Nullable final String storageKey) {
		return relationshipTypeMap.get(storageKey);
	}
	
	@Nullable
	public String getRefinabilityType(@Nullable final String conceptId) {
		final String refinabilityType = refinabilityTypeMap.get(conceptId);
		return null == refinabilityType ? "0" : refinabilityType;
	}
	
	/*creates and initialize the concept status cache*/
	private Map<String, String> initDescriptionStatusCache() {
		descriptionStatusMap = Maps.newHashMap();
		descriptionStatusMap.put("0", "0");
		descriptionStatusMap.put("1", "1");
		descriptionStatusMap.put(Concepts.DUPLICATE, "2");
		descriptionStatusMap.put(Concepts.OUTDATED, "3");
		descriptionStatusMap.put(Concepts.ERRONEOUS, "5");
		descriptionStatusMap.put(Concepts.LIMITED, "6");
		descriptionStatusMap.put(Concepts.INAPPROPRIATE, "7");
		descriptionStatusMap.put(Concepts.CONCEPT_NON_CURRENT, "8");
		descriptionStatusMap.put(Concepts.MOVED_ELSEWHERE, "10");
		descriptionStatusMap.put(Concepts.PENDING_MOVE, "11");
		return descriptionStatusMap;
	}
	
	/*creates and initialize the concept status cache*/
	private Map<String, String> initConceptStatusCache() {
		conceptStatusMap = Maps.newHashMap();
		conceptStatusMap.put("0", "0");
		conceptStatusMap.put("1", "1");
		conceptStatusMap.put(Concepts.DUPLICATE, "2");
		conceptStatusMap.put(Concepts.OUTDATED, "3");
		conceptStatusMap.put(Concepts.AMBIGUOUS, "4");
		conceptStatusMap.put(Concepts.ERRONEOUS, "5");
		conceptStatusMap.put(Concepts.LIMITED, "6");
		conceptStatusMap.put(Concepts.MOVED_ELSEWHERE, "10");
		conceptStatusMap.put(Concepts.PENDING_MOVE, "11");
		return conceptStatusMap;
	}

	/*creates and initialize the description case significance cache for RF1 mapping*/
	private Map<String, String> initInitialiCapitalStatusCache() {
		initialCapitalStatusMap = Maps.newHashMap();
		initialCapitalStatusMap.put(Concepts.ENTIRE_TERM_CASE_INSENSITIVE, "0");
		initialCapitalStatusMap.put(Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE, "0");
		initialCapitalStatusMap.put(Concepts.ENTIRE_TERM_CASE_SENSITIVE, "1");
		return initialCapitalStatusMap;
	}

	/*creates and initialize the description type cache for RF1 mapping*/
	private Map<String, String> initDescriptionTypeCache() {
		descriptionTypeMap = Maps.newHashMap();
		descriptionTypeMap.put(Concepts.SYNONYM, "2");
		descriptionTypeMap.put(Concepts.FULLY_SPECIFIED_NAME, "3");
		return descriptionTypeMap;
	}
	
	private Map<String, String> initExtendedDescriptionTypeCache() {
		extendedDescriptionTypeMap = newHashMap();
		extendedDescriptionTypeMap.put(Concepts.SYNONYM, "2");
		extendedDescriptionTypeMap.put(Concepts.FULLY_SPECIFIED_NAME, "3");
		extendedDescriptionTypeMap.put(Concepts.FULL_NAME, "4");
		extendedDescriptionTypeMap.put(Concepts.ABBREVIATION, "5");
		extendedDescriptionTypeMap.put(Concepts.PRODUCT_TERM, "6");
		extendedDescriptionTypeMap.put(Concepts.SHORT_NAME, "7");
		extendedDescriptionTypeMap.put(Concepts.PREFERRED_PLURAL, "8");
		extendedDescriptionTypeMap.put(Concepts.NOTE, "9");
		extendedDescriptionTypeMap.put(Concepts.SEARCH_TERM, "10");
		extendedDescriptionTypeMap.put(Concepts.ABBREVIATION_PLURAL, "11");
		extendedDescriptionTypeMap.put(Concepts.PRODUCT_TERM_PLURAL, "12");
		return extendedDescriptionTypeMap; 
	}

	/*creates and initialize the relationship type cache for RF1 mapping*/
	private Map<String, String> initRelationshipTypeCache() {
		relationshipTypeMap = Maps.newHashMap();
		relationshipTypeMap.put(Concepts.DEFINING_RELATIONSHIP, "0");
		relationshipTypeMap.put(Concepts.STATED_RELATIONSHIP, "0");
		relationshipTypeMap.put(Concepts.INFERRED_RELATIONSHIP, "0");
		relationshipTypeMap.put(Concepts.QUALIFYING_RELATIONSHIP, "1");
		relationshipTypeMap.put(Concepts.ADDITIONAL_RELATIONSHIP, "3");
		return relationshipTypeMap;
	}

	/*initialize the relationship refianability type cache.*/
	private Map<String, String> initRefinabilityTypeCache() {
		refinabilityTypeMap = Maps.newHashMap();
		refinabilityTypeMap.put(Concepts.NOT_REFINABLE, "0");
		refinabilityTypeMap.put(Concepts.OPTIONAL_REFINABLE, "1");
		refinabilityTypeMap.put(Concepts.MANDATORY_REFINABLE, "2");
		return refinabilityTypeMap;
	}

	/*initialize the concept definition status cache.*/
	private Map<String, String> initDefinitionStatusCache() {
		conceptDefinitionStatusMap = Maps.newHashMap();
		conceptDefinitionStatusMap.put(Concepts.FULLY_DEFINED, "0");
		conceptDefinitionStatusMap.put(Concepts.PRIMITIVE, "1");
		return conceptDefinitionStatusMap;
	}
	
}