/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry.Expressions.*;

import java.util.Collection;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.converter.SnomedRelationshipConverter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.Iterables;

/**
 * @since 4.5
 */
final class SnomedRelationshipSearchRequest extends SnomedComponentSearchRequest<SnomedRelationships, SnomedRelationshipIndexEntry> {

	enum OptionKey {
		SOURCE,
		TYPE,
		DESTINATION,
		CHARACTERISTIC_TYPE, 
		GROUP_MIN,
		GROUP_MAX,
		UNION_GROUP,
		MODIFIER,
		VALUE_TYPE,
		OPERATOR,
		VALUE,
		HAS_DESTINATION_ID
	}
	
	SnomedRelationshipSearchRequest() {}

	@Override
	protected Class<SnomedRelationshipIndexEntry> getDocumentType() {
		return SnomedRelationshipIndexEntry.class;
	}
	
	@Override
	protected Expression prepareQuery(BranchContext context) {
		final ExpressionBuilder queryBuilder = Expressions.builder();
		
		addActiveClause(queryBuilder);
		addReleasedClause(queryBuilder);
		addIdFilter(queryBuilder, RevisionDocument.Expressions::ids);
		addEclFilter(context, queryBuilder, SnomedSearchRequest.OptionKey.MODULE, SnomedDocument.Expressions::modules);
		addNamespaceFilter(queryBuilder);
		addNamespaceConceptIdFilter(context, queryBuilder);
		addEffectiveTimeClause(queryBuilder);
		addActiveMemberOfClause(context, queryBuilder);
		addMemberOfClause(context, queryBuilder);
		addEclFilter(context, queryBuilder, OptionKey.SOURCE, SnomedRelationshipIndexEntry.Expressions::sourceIds);
		addEclFilter(context, queryBuilder, OptionKey.TYPE, SnomedRelationshipIndexEntry.Expressions::typeIds);
		addEclFilter(context, queryBuilder, OptionKey.DESTINATION, SnomedRelationshipIndexEntry.Expressions::destinationIds);
		addEclFilter(context, queryBuilder, OptionKey.CHARACTERISTIC_TYPE, SnomedRelationshipIndexEntry.Expressions::characteristicTypeIds);
		addEclFilter(context, queryBuilder, OptionKey.MODIFIER, SnomedRelationshipIndexEntry.Expressions::modifierIds);
		
		if (containsKey(OptionKey.GROUP_MIN) || containsKey(OptionKey.GROUP_MAX)) {
			final int from = containsKey(OptionKey.GROUP_MIN) ? get(OptionKey.GROUP_MIN, Integer.class) : 0;
			final int to = containsKey(OptionKey.GROUP_MAX) ? get(OptionKey.GROUP_MAX, Integer.class) : Integer.MAX_VALUE;
			queryBuilder.filter(relationshipGroup(from, to));
		}
		
		if (containsKey(OptionKey.UNION_GROUP)) {
			queryBuilder.filter(unionGroup(get(OptionKey.UNION_GROUP, Integer.class)));
		}
		
		if (containsKey(OptionKey.HAS_DESTINATION_ID)) {
			// No need to check the value for the option key here, as it can only be set to "true" in the builder
			queryBuilder.filter(hasDestinationId());
		}
		
		if (containsKey(OptionKey.VALUE_TYPE)) {
			final Collection<RelationshipValueType> valueTypes = getCollection(OptionKey.VALUE_TYPE, RelationshipValueType.class);
			queryBuilder.filter(valueTypes(valueTypes));
		}
		
		if (containsKey(OptionKey.VALUE)) {
			final SearchResourceRequest.Operator op = get(OptionKey.OPERATOR, SearchResourceRequest.Operator.class);
			final Collection<RelationshipValue> values = getCollection(OptionKey.VALUE, RelationshipValue.class);
			
			switch (op) {
				case EQUALS:
					queryBuilder.filter(values(values));
					break;
				case NOT_EQUALS:
					queryBuilder.mustNot(values(values));
					break;
				case LESS_THAN:
					checkRangeValue(values);
					queryBuilder.filter(valueLessThan(Iterables.getOnlyElement(values), false));
					break;
				case LESS_THAN_EQUALS:
					checkRangeValue(values);
					queryBuilder.filter(valueLessThan(Iterables.getOnlyElement(values), true));
					break;
				case GREATER_THAN:
					checkRangeValue(values);
					queryBuilder.filter(valueGreaterThan(Iterables.getOnlyElement(values), false));
					break;
				case GREATER_THAN_EQUALS:
					checkRangeValue(values);
					queryBuilder.filter(valueGreaterThan(Iterables.getOnlyElement(values), true));
					break;
				default: 
					throw new NotImplementedException("Unsupported concrete value operator %s", op);
			}
		}
		
		return queryBuilder.build();
	}
	
	@Override
	protected SnomedRelationships toCollectionResource(BranchContext context, Hits<SnomedRelationshipIndexEntry> hits) {
		final int totalHits = hits.getTotal();
		if (limit() < 1 || totalHits < 1) {
			return new SnomedRelationships(limit(), totalHits);
		} else {
			return new SnomedRelationshipConverter(context, expand(), locales()).convert(hits);
		}
	}
	
	@Override
	protected SnomedRelationships createEmptyResult(int limit) {
		return new SnomedRelationships(limit, 0);
	}

	private static void checkRangeValue(final Collection<RelationshipValue> values) {
		if (values.size() != 1) {
			throw new BadRequestException("Exactly one relationship value is required for range queries");
		}
	}
}
