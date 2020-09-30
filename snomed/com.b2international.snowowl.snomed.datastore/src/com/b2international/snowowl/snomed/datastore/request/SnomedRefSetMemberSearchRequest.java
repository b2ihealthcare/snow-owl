/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Expressions.*;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.IllegalQueryParameterException;
import com.b2international.commons.exceptions.NotImplementedException;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.collect.Iterables;

/**
 * @since 4.5
 */
final class SnomedRefSetMemberSearchRequest extends SnomedSearchRequest<SnomedReferenceSetMembers, SnomedRefSetMemberIndexEntry> {

	private static final long serialVersionUID = 1L;

	/**
	 * @since 4.5
	 */
	enum OptionKey {
		/**
		 * Filter by containing reference set
		 */
		REFSET,

		/**
		 * Filter by referenced component
		 */
		REFERENCED_COMPONENT,
		
		/**
		 * The referenced component type to match
		 */
		REFERENCED_COMPONENT_TYPE,
		
		/**
		 * Filter by refset type
		 */
		REFSET_TYPE,
		
		/**
		 * Filter by member specific props, the value should be a {@link Map} of RF2 headers and their corresponding values.
		 */
		PROPS,
		
		/**
		 * Matches reference set members where either the referenced component or map target matches the given value.
		 */
		COMPONENT
	}

	SnomedRefSetMemberSearchRequest() {}
	
	@Override
	protected Class<SnomedRefSetMemberIndexEntry> getDocumentType() {
		return SnomedRefSetMemberIndexEntry.class;
	}
	
	@Override
	protected Expression prepareQuery(BranchContext context) {
		final Collection<String> referencedComponentIds = getCollection(OptionKey.REFERENCED_COMPONENT, String.class);
		final Collection<SnomedRefSetType> refSetTypes = getCollection(OptionKey.REFSET_TYPE, SnomedRefSetType.class);
		final Options propsFilter = getOptions(OptionKey.PROPS);
		
		ExpressionBuilder queryBuilder = Expressions.builder();
		
		addActiveClause(queryBuilder);
		addReleasedClause(queryBuilder);
		addEclFilter(context, queryBuilder, SnomedSearchRequest.OptionKey.MODULE, SnomedDocument.Expressions::modules);
		addIdFilter(queryBuilder, RevisionDocument.Expressions::ids);
		addEffectiveTimeClause(queryBuilder);
		addEclFilter(context, queryBuilder, OptionKey.REFSET, SnomedRefSetMemberIndexEntry.Expressions::referenceSetId);
		addComponentClause(queryBuilder);
		
		if (containsKey(OptionKey.REFERENCED_COMPONENT_TYPE)) {
			queryBuilder.filter(referencedComponentTypes(getCollection(OptionKey.REFERENCED_COMPONENT_TYPE, Short.class)));
		}
		
		if (!referencedComponentIds.isEmpty()) {
			queryBuilder.filter(referencedComponentIds(referencedComponentIds));
		}
		
		if (!refSetTypes.isEmpty()) {
			queryBuilder.filter(refSetTypes(refSetTypes));
		}
		
		if (!propsFilter.isEmpty()) {
			final Set<String> propKeys = newHashSet(propsFilter.keySet());
			if (propKeys.remove(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID)) {
				queryBuilder.filter(acceptabilityIds(propsFilter.getCollection(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, String.class)));
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP)) {
				final String operatorKey = SearchResourceRequest.operator(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP);
				SearchResourceRequest.Operator op;
				if (propKeys.remove(operatorKey)) {
					op = propsFilter.get(operatorKey, Operator.class);
				} else {
					op = SearchResourceRequest.Operator.EQUALS;
				}
				switch (op) {
				case EQUALS:
					queryBuilder.filter(relationshipGroup(propsFilter.get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, Integer.class)));
					break;
				case NOT_EQUALS:
					queryBuilder.mustNot(relationshipGroup(propsFilter.get(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, Integer.class)));
					break;
				default: throw new NotImplementedException("Unsupported relationship group operator %s", op);
				}
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, String.class), SnomedRefSetMemberIndexEntry.Expressions::characteristicTypeIds);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_CORRELATION_ID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_CORRELATION_ID, String.class), SnomedRefSetMemberIndexEntry.Expressions::correlationIds);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT, String.class), SnomedRefSetMemberIndexEntry.Expressions::descriptionFormats);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, String.class), SnomedRefSetMemberIndexEntry.Expressions::mapCategoryIds);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID, String.class), SnomedRefSetMemberIndexEntry.Expressions::targetComponents);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_TARGET_COMPONENT)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_TARGET_COMPONENT, String.class), SnomedRefSetMemberIndexEntry.Expressions::targetComponents);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MAP_TARGET)) {
				queryBuilder.filter(mapTargets(propsFilter.getCollection(SnomedRf2Headers.FIELD_MAP_TARGET, String.class)));
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION)) {
				queryBuilder.filter(mapTargetDescriptions(propsFilter.getCollection(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION, String.class)));
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MAP_GROUP)) {
				queryBuilder.filter(mapGroups(propsFilter.getCollection(SnomedRf2Headers.FIELD_MAP_GROUP, Integer.class)));
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MAP_PRIORITY)) {
				queryBuilder.filter(mapPriority(propsFilter.getCollection(SnomedRf2Headers.FIELD_MAP_PRIORITY, Integer.class)));
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MAP_BLOCK)) {
				queryBuilder.filter(mapBlock(propsFilter.getCollection(SnomedRf2Headers.FIELD_MAP_BLOCK, Integer.class)));
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_VALUE_ID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_VALUE_ID, String.class), SnomedRefSetMemberIndexEntry.Expressions::valueIds);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_TYPE_ID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_TYPE_ID, String.class), SnomedRefSetMemberIndexEntry.Expressions::typeIds);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, String.class), SnomedRefSetMemberIndexEntry.Expressions::domainIds);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, String.class), SnomedRefSetMemberIndexEntry.Expressions::contentTypeIds);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, String.class), SnomedRefSetMemberIndexEntry.Expressions::ruleStrengthIds);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, String.class), SnomedRefSetMemberIndexEntry.Expressions::ruleRefSetIds);
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_MRCM_GROUPED)) {
				queryBuilder.filter(grouped(propsFilter.getBoolean(SnomedRf2Headers.FIELD_MRCM_GROUPED)));
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_OWL_EXPRESSION)) {
				queryBuilder.filter(Expressions.exactMatch(SnomedRf2Headers.FIELD_OWL_EXPRESSION, propsFilter.getString(SnomedRf2Headers.FIELD_OWL_EXPRESSION)));
			}
			if (propKeys.remove(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_CONCEPTID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_CONCEPTID, String.class), SnomedRefSetMemberIndexEntry.Expressions::owlExpressionConcept);
			}
			if (propKeys.remove(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_DESTINATIONID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_DESTINATIONID, String.class), SnomedRefSetMemberIndexEntry.Expressions::owlExpressionDestination);
			}
			if (propKeys.remove(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_TYPEID)) {
				addEclFilter(context, queryBuilder, propsFilter.getCollection(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_TYPEID, String.class), SnomedRefSetMemberIndexEntry.Expressions::owlExpressionType);
			}
			if (propKeys.remove(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_GCI)) {
				queryBuilder.filter(gciAxiom(propsFilter.getBoolean(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_GCI)));
			}
			final Collection<DataType> dataTypes = propsFilter.getCollection(SnomedRefSetMemberIndexEntry.Fields.DATA_TYPE, DataType.class);
			if (propKeys.remove(SnomedRefSetMemberIndexEntry.Fields.DATA_TYPE)) {
				queryBuilder.filter(dataTypes(dataTypes));
			}
			if (propKeys.remove(SnomedRf2Headers.FIELD_VALUE)) {
				if (dataTypes.size() != 1)  {
					throw new BadRequestException("DataType filter must be specified if filtering by value");
				}
				final DataType dataType = Iterables.getOnlyElement(dataTypes);
				final String operatorKey = SearchResourceRequest.operator(SnomedRf2Headers.FIELD_VALUE);
				SearchResourceRequest.Operator op;
				if (propKeys.remove(operatorKey)) {
					op = propsFilter.get(operatorKey, Operator.class);
				} else {
					op = SearchResourceRequest.Operator.EQUALS;
				}
				final Collection<Object> attributeValues = propsFilter.getCollection(SnomedRf2Headers.FIELD_VALUE, Object.class);
				switch (op) {
				case EQUALS:
					queryBuilder.filter(values(dataType, attributeValues));
					break;
				case NOT_EQUALS:
					queryBuilder.mustNot(values(dataType, attributeValues));
					break;
				case LESS_THAN:
					checkRangeValue(attributeValues);
					queryBuilder.filter(valueRange(dataType, null, Iterables.getOnlyElement(attributeValues), false, false));
					break;
				case LESS_THAN_EQUALS:
					checkRangeValue(attributeValues);
					queryBuilder.filter(valueRange(dataType, null, Iterables.getOnlyElement(attributeValues), false, true));
					break;
				case GREATER_THAN:
					checkRangeValue(attributeValues);
					queryBuilder.filter(valueRange(dataType, Iterables.getOnlyElement(attributeValues), null, false, false));
					break;
				case GREATER_THAN_EQUALS:
					checkRangeValue(attributeValues);
					queryBuilder.filter(valueRange(dataType, Iterables.getOnlyElement(attributeValues), null, true, false));
					break;
				default: throw new NotImplementedException("Unsupported concrete domain value operator %s", op);
				}
			}
			if (!propKeys.isEmpty()) {
				throw new IllegalQueryParameterException("Unsupported property filter(s), %s", propKeys);
			}
		}
		
		return queryBuilder.build();
	}

	@Override
	protected SnomedReferenceSetMembers toCollectionResource(BranchContext context, Hits<SnomedRefSetMemberIndexEntry> hits) {
		if (limit() < 1 || hits.getTotal() < 1) {
			return new SnomedReferenceSetMembers(limit(), hits.getTotal());
		} else {
			return SnomedConverters.newMemberConverter(context, expand(), locales()).convert(hits.getHits(), hits.getSearchAfter(), limit(), hits.getTotal());
		}
	}

	private static void checkRangeValue(final Collection<Object> attributeValues) {
		if (attributeValues.size() != 1) {
			throw new BadRequestException("Exactly one attribute value is required for range queries");
		}
	}
	
	private void addComponentClause(ExpressionBuilder builder) {
		if (containsKey(OptionKey.COMPONENT)) {
			final Collection<String> componentIds = getCollection(OptionKey.COMPONENT, String.class);
			builder.filter(
				Expressions.builder()
					.should(referencedComponentIds(componentIds))
					.should(mapTargets(componentIds))
				.build()
			);
		}
	}
	
	@Override
	protected SnomedReferenceSetMembers createEmptyResult(int limit) {
		return new SnomedReferenceSetMembers(limit, 0);
	}
	
}
