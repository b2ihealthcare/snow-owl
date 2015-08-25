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
package com.b2international.snowowl.snomed.datastore.browser;

import static com.b2international.snowowl.datastore.index.IndexUtils.longToPrefixCoded;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_STRUCTURAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.CONCRETE_DATA_TYPE_VALUE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.LANGUAGE_VALUE;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.b2international.snowowl.datastore.index.field.ComponentTypeField;
import com.b2international.snowowl.datastore.index.field.IntIndexField;
import com.b2international.snowowl.datastore.index.query.IndexQueries;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;

/**
 * Common Lucene index queries for reusability.
 * 
 * @since 3.7
 */
public class SnomedIndexQueries {

	private SnomedIndexQueries() {}
	
	// SNOMED CT index field names
	public static final String RELATIONSHIP_CHARACTERISTIC_TYPE_ID = "relationship_characteristic_type_id";
	
	public static final Query ACTIVE_COMPONENT_QUERY = new IntIndexField(COMPONENT_ACTIVE, 1).toQuery();
	public static final Query INACTIVE_COMPONENT_QUERY = new IntIndexField(COMPONENT_ACTIVE, 0).toQuery();
	
	// component type queries
	public static final Query RELATIONSHIP_TYPE_QUERY = new ComponentTypeField(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER).toQuery();
	public static final Query CONCEPT_TYPE_QUERY = new ComponentTypeField(SnomedTerminologyComponentConstants.CONCEPT_NUMBER).toQuery();
	public static final Query DESCRIPTION_TYPE_QUERY = new ComponentTypeField(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER).toQuery(); 
	public static final Query REFSET_TYPE_QUERY = new ComponentTypeField(SnomedTerminologyComponentConstants.REFSET_NUMBER).toQuery();
	public static final Query PREDICATE_TYPE_QUERY = new ComponentTypeField(PredicateUtils.PREDICATE_TYPE_ID).toQuery();
			
	// active component type queries
	public static final Query ACTIVE_CONCEPTS_QUERY = IndexQueries.and(ACTIVE_COMPONENT_QUERY, CONCEPT_TYPE_QUERY);
	public static final Query ACTIVE_DESCRIPTIONS_QUERY = IndexQueries.and(ACTIVE_COMPONENT_QUERY, DESCRIPTION_TYPE_QUERY);
	public static final Query ACTIVE_RELATIONSHIPS_QUERY = IndexQueries.and(ACTIVE_COMPONENT_QUERY, RELATIONSHIP_TYPE_QUERY);
	
	public static final Query CONCRETE_DOMAIN_MEMBER_TYPE_QUERY = toRefSetMemberTypeQuery(CONCRETE_DATA_TYPE_VALUE);
	public static final Query LANGUAGE_REFSET_MEMBER_TYPE_QUERY = toRefSetMemberTypeQuery(LANGUAGE_VALUE);
	public static final Query ACTIVE_LANGUAGE_REFSET_MEMBER_TYPE_QUERY = IndexQueries.and(LANGUAGE_REFSET_MEMBER_TYPE_QUERY, ACTIVE_COMPONENT_QUERY);
	public static final Query NOT_STRUCTURAL_REFSET_QUERY = new IntIndexField(REFERENCE_SET_STRUCTURAL, 0).toQuery();
	
	// relationship characteristic type queries
	public static final Query STATED_RELATIONSHIP_CHARACTERISTIC_TYPE_QUERY = toRelationshipCharacteristicTypeQuery(Concepts.STATED_RELATIONSHIP);
	public static final Query INFERRED_RELATIONSHIP_CHARACTERISTIC_TYPE_QUERY = toRelationshipCharacteristicTypeQuery(Concepts.INFERRED_RELATIONSHIP);
	public static final Query DEFINING_RELATIONSHIP_CHARACTERISTIC_TYPE_QUERY = toRelationshipCharacteristicTypeQuery(Concepts.DEFINING_RELATIONSHIP);

	// reference set member characteristic type queries
	public static final Query ADDITIONAL_MEMBER_CHARACTERISTIC_TYPE_QUERY = toMemberCharacteristicTypeQuery(Concepts.ADDITIONAL_RELATIONSHIP);
	// relationship type queries
	public static final Query ISA_TYPE_QUERY = toRelationshipTypeQuery(Concepts.IS_A);
	
	// compound queries
	public static final Query ACTIVE_ISA_RELATIONSHIPS = IndexQueries.and(ACTIVE_COMPONENT_QUERY, ISA_TYPE_QUERY);
	
	public static Query toRefSetMemberTypeQuery(int typeOrdinal) {
		return new IntIndexField(REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE, typeOrdinal).toQuery();
	}

	public static Query toRefSetReferencedComponentTypeQuery(int componentType) {
		return new IntIndexField(REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE, componentType).toQuery();
	}
	
	public static Query toRelationshipTypeQuery(String typeId) {
		return new TermQuery(new Term(RELATIONSHIP_ATTRIBUTE_ID, longToPrefixCoded(typeId)));
	}

	public static Query toRelationshipCharacteristicTypeQuery(String characteristicType) {
		return new TermQuery(new Term(RELATIONSHIP_CHARACTERISTIC_TYPE_ID, longToPrefixCoded(characteristicType)));
	}
	
	public static Query toMemberCharacteristicTypeQuery(String additionalRelationship) {
		return new TermQuery(new Term(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID, longToPrefixCoded(additionalRelationship)));
	}

}