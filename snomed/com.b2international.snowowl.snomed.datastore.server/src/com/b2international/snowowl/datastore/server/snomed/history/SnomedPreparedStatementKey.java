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
package com.b2international.snowowl.datastore.server.snomed.history;

import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.ASSOCIATION;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.ATTRIBUTE_VALUE;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.COMPLEX_MAP;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.CONCRETE_DATA_TYPE;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.DESCRIPTION_TYPE;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.EXTENDED_MAP;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.LANGUAGE;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.MODULE_DEPENDENCY;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.QUERY;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.SIMPLE;
import static com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType.SIMPLE_MAP;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage.Literals.SNOMED_MAPPING_REF_SET;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage.Literals.SNOMED_REGULAR_REF_SET;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage.Literals.SNOMED_STRUCTURAL_REF_SET;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableMap;

import java.util.EnumMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.datastore.server.history.PreparedStatementKey;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;

/**
 * Enumeration of SNOMED&nbsp;CT history query prepared statement keys.
 *
 */
public enum SnomedPreparedStatementKey implements PreparedStatementKey {

	//for core components
	CONCEPT_CHANGES,
	CONCEPT_PT_CHANGES,
	DESCRIPTION_CHANGES,
	RELATIONSHIP_CHANGES,
	RELATED_REFERENCE_SET_MEMBER_CHANGES,
	
	//for reference set members
	SIMPLE_MEMBER_CHANGES,
	SIMPLE_MAP_MEMBER_CHANGES,
	LANGUAGE_MEMBER_CHANGES,
	ATTRIBUTE_VALUE_MEMBER_CHANGES,
	QUERY_MEMBER_CHANGES,
	COMPLEX_MAP_MEMBER_CHANGES,
	DESCRIPTION_MEMBER_CHANGES,
	CONCRETE_DATA_TYPE_MEMBER_CHANGES,
	ASSOCIATION_MEMBER_CHANGES,
	MODULE_DEPENDENCY_MEMBER_CHANGES,
	
	//for reference sets
	MAPPING_REFSET_CHANGES,
	REGULAR_REFSET_CHANGES,
	STRUCTURAL_REFSET_CHANGES;
	
	private static final Map<SnomedRefSetType, PreparedStatementKey> TYPE_TO_MEMBER_STATEMENT_KEY_MAP;
	private static final Map<EClass, PreparedStatementKey> CLASS_TO_REFSET_STATEMENT_KEY_MAP;
	
	static {
		final Map<SnomedRefSetType, PreparedStatementKey> memberMap = new EnumMap<>(SnomedRefSetType.class);
		memberMap.put(SIMPLE, SIMPLE_MEMBER_CHANGES);
		memberMap.put(SIMPLE_MAP, SIMPLE_MAP_MEMBER_CHANGES);
		memberMap.put(LANGUAGE, LANGUAGE_MEMBER_CHANGES);
		memberMap.put(ATTRIBUTE_VALUE, ATTRIBUTE_VALUE_MEMBER_CHANGES);
		memberMap.put(QUERY, QUERY_MEMBER_CHANGES);
		memberMap.put(COMPLEX_MAP, COMPLEX_MAP_MEMBER_CHANGES);
		memberMap.put(DESCRIPTION_TYPE, DESCRIPTION_MEMBER_CHANGES);
		memberMap.put(CONCRETE_DATA_TYPE, CONCRETE_DATA_TYPE_MEMBER_CHANGES);
		memberMap.put(ASSOCIATION, ASSOCIATION_MEMBER_CHANGES);
		memberMap.put(MODULE_DEPENDENCY, MODULE_DEPENDENCY_MEMBER_CHANGES);
		memberMap.put(EXTENDED_MAP, COMPLEX_MAP_MEMBER_CHANGES);
		TYPE_TO_MEMBER_STATEMENT_KEY_MAP = unmodifiableMap(memberMap);
		
		final Map<EClass, PreparedStatementKey> refSetMap = newHashMap();
		refSetMap.put(SNOMED_MAPPING_REF_SET, MAPPING_REFSET_CHANGES);
		refSetMap.put(SNOMED_STRUCTURAL_REF_SET, STRUCTURAL_REFSET_CHANGES);
		refSetMap.put(SNOMED_REGULAR_REF_SET, REGULAR_REFSET_CHANGES);
		CLASS_TO_REFSET_STATEMENT_KEY_MAP = unmodifiableMap(refSetMap);
	}
	
	/**
	 * Returns with {@code true} if the statement key argument stands for a reference set.
	 * Otherwise {@code false}. 
	 * @param statementKey the key to check.
	 * @return {@code true} if stands for reference set, otherwise {@code false}.
	 */
	public static boolean isRefSetStatementKey(final PreparedStatementKey statementKey) {
		return MAPPING_REFSET_CHANGES.equals(statementKey)
			|| REGULAR_REFSET_CHANGES.equals(statementKey)
			|| STRUCTURAL_REFSET_CHANGES.equals(statementKey);
	}
	
	/**
	 * Returns with the statement key for a reference set member based on the reference 
	 * set type argument.
	 * @param type the reference set member type.
	 * @return the prepared statement key for the reference set member change.
	 */
	public static PreparedStatementKey getMemberStatementKey(final SnomedRefSetType type) {
		return TYPE_TO_MEMBER_STATEMENT_KEY_MAP.get(checkNotNull(type, "type"));
	}
	
	/**
	 * Returns with the statement key for a reference set based on the reference 
	 * set class argument.
	 * @param eClass the class of the reference set.
	 * @return the prepared statement key for the reference set change.
	 */
	public static PreparedStatementKey getRefSetStatementKey(final EClass eClass) {
		return CLASS_TO_REFSET_STATEMENT_KEY_MAP.get(checkNotNull(eClass, "eClass"));
	}
	
}