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
package com.b2international.snowowl.snomed.datastore.index.refset;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.Lists;

/**
 * Lucene specific query adapter for retrieving lightweight representations of SNOMED CT reference set members.
 * @deprecated - UNSUPPORTED, will be removed in 4.7
 */
public class SnomedRefSetMembershipIndexQueryAdapter {

	public static SnomedRefSetMembershipIndexQueryAdapter createFindByRefSetTypeQuery(
			final String terminologyComponentId, final Iterable<SnomedRefSetType> types, final Iterable<String> referencedComponentIds) {
		
		checkArgument(!CompareUtils.isEmpty(types), "SNOMED CT reference set type argument cannot be empty.");
		checkArgument(!CompareUtils.isEmpty(referencedComponentIds), "Referenced component identifiers argument cannot be empty.");
		checkNotNull(terminologyComponentId, "Terminology component ID argument cannot be null.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				final Query query = SnomedMappings.newQuery()
						.and(createReferencedComponentTypeQuery(terminologyComponentId))
						.and(createRefSetTypeQuery(types))
						.matchAll();
				return new FilteredQuery(query, createMemberReferencedComponentIdFilter(referencedComponentIds));
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createFindPreferredTermMembersQuery(final Iterable<String> descriptionIds, final String languageRefSetId) {
		checkArgument(!CompareUtils.isEmpty(descriptionIds), "SNOMED CT description IDs argument cannot be empty.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = -861338476226441708L;
			@Override public Query createQuery() {
				return new FilteredQuery(SnomedMappings.newQuery()
						.active()
						.memberRefSetId(languageRefSetId)
						.memberAcceptabilityId(Long.valueOf(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED))
						.matchAll(),
						createMemberReferencedComponentIdFilter(descriptionIds));
			}
		};
	}
	
	public static SnomedRefSetMembershipIndexQueryAdapter createMappingMembershipQuery(
			final String componentType, final String componentId) {
		
		checkNotNull(componentType, "Terminology component type argument cannot be null.");
		checkNotNull(componentId, "Component identifier argument cannot be null.");
		
		return new SnomedRefSetMembershipIndexQueryAdapter() {
			private static final long serialVersionUID = 615721958388015634L;
			@Override public Query createQuery() {
				
				final SnomedQueryBuilder queryBuilder = SnomedMappings.newQuery();
				
				try { // workaround to skip component IDs with alphabetic characters
					Long.parseLong(componentId);
					final Query referencedComponentQuery = SnomedMappings.newQuery()
							.memberReferencedComponentId(componentId)
							.and(createReferencedComponentTypeQuery(componentType))
							.matchAll();
					queryBuilder.and(referencedComponentQuery);
					
				} catch (final NumberFormatException e) { /* ignore */ }
				
				final Query specialFieldQuery = SnomedMappings.newQuery()
						.memberMapTargetComponentId(componentId)
						.and(createMapTargetComponentTypeQuery(componentType))
						.matchAll();

				queryBuilder.and(specialFieldQuery);
				
				return SnomedMappings.newQuery()
						.and(createRefSetTypeQuery(Lists.newArrayList(SnomedRefSetType.COMPLEX_MAP, SnomedRefSetType.SIMPLE_MAP, SnomedRefSetType.EXTENDED_MAP)))
						.and(queryBuilder.matchAny())
						.matchAll();
			}
		};
	} 
	
	private static Filter createMemberReferencedComponentIdFilter(final Iterable<String> ids) {
		final BooleanFilter idFilter = new BooleanFilter();
		for (final String id : ids) {
			final Query query = SnomedMappings.newQuery().memberReferencedComponentId(id).matchAll();
			idFilter.add(new QueryWrapperFilter(query), Occur.SHOULD);
		}
		return idFilter;
	}
	
	private static Query createRefSetTypeQuery(final Iterable<SnomedRefSetType> types) {
		final SnomedQueryBuilder qb = SnomedMappings.newQuery();
		for (final SnomedRefSetType type : types) {
			qb.memberRefSetType(type);
		}
		return qb.matchAny();
	}
	
	private static Query createReferencedComponentTypeQuery(final String terminologyComponentId) {
		final int componentTypeValue = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsInt(terminologyComponentId);
		return SnomedMappings.newQuery().memberReferencedComponentType(componentTypeValue).matchAll();
	}
	
	private static Query createMapTargetComponentTypeQuery(final String terminologyComponentId) {
		final int componentTypeValue = CoreTerminologyBroker.getInstance().getTerminologyComponentIdAsInt(terminologyComponentId);
		return SnomedMappings.newQuery().memberMapTargetComponentType(componentTypeValue).matchAll();
	}
	
}
