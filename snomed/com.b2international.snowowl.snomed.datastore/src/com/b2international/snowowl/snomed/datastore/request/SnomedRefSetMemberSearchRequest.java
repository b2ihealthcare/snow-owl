/*
 * Copyright 2011-2024 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.SortedMap;

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
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.converter.SnomedReferenceSetMemberConverter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.collect.*;

/**
 * @since 4.5
 */
public final class SnomedRefSetMemberSearchRequest extends SnomedSearchRequest<SnomedReferenceSetMembers, SnomedRefSetMemberIndexEntry> {

	private static final long serialVersionUID = 1L;

	// Requesting the "value" field should load "booleanValue", "decimalValue", "integerValue", "stringValue" and "dataType" instead
	private static final Multimap<String, String> REPLACE_VALUE_FIELD = ImmutableMultimap.<String, String>builder()
		.putAll(SnomedRf2Headers.FIELD_VALUE, 
			SnomedRefSetMemberIndexEntry.Fields.BOOLEAN_VALUE, 
			SnomedRefSetMemberIndexEntry.Fields.DECIMAL_VALUE, 
			SnomedRefSetMemberIndexEntry.Fields.INTEGER_VALUE, 
			SnomedRefSetMemberIndexEntry.Fields.STRING_VALUE, 
			SnomedRefSetMemberIndexEntry.Fields.DATA_TYPE)
		.build();
	
	private static final SortedMap<String, SnomedRefsetMemberFieldQueryHandler<?>> SUPPORTED_MEMBER_FIELDS = ImmutableSortedMap.<String, SnomedRefsetMemberFieldQueryHandler<?>>naturalOrder()
			// String types, ECL support
			.put(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::referencedComponentIds, true))
			.put(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::acceptabilityIds, true))
			.put(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::characteristicTypeIds, true))
			.put(SnomedRf2Headers.FIELD_CORRELATION_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::correlationIds, true))
			.put(SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::descriptionFormats, true))
			.put(SnomedRf2Headers.FIELD_MAP_CATEGORY_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::mapCategoryIds, true))
			.put(SnomedRf2Headers.FIELD_TARGET_COMPONENT_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::targetComponentIds, true))
			.put(SnomedRf2Headers.FIELD_VALUE_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::valueIds, true))
			.put(SnomedRf2Headers.FIELD_TYPE_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::typeIds, true))
			.put(SnomedRf2Headers.FIELD_MRCM_DOMAIN_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::domainIds, true))
			.put(SnomedRf2Headers.FIELD_MRCM_CONTENT_TYPE_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::contentTypeIds, true))
			.put(SnomedRf2Headers.FIELD_MRCM_RULE_STRENGTH_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::ruleStrengthIds, true))
			.put(SnomedRf2Headers.FIELD_MRCM_RULE_REFSET_ID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::ruleRefSetIds, true))
			
			// String types, ECL support, special non-RF2 index only fields
			.put(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_CONCEPTID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::owlExpressionConcept, true))
			.put(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_DESTINATIONID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::owlExpressionDestination, true))
			.put(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_TYPEID, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::owlExpressionType, true))
			
			// String types, no ECL support
			.put(SnomedRf2Headers.FIELD_MAP_TARGET, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::mapTargets, false))
			.put(SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::mapTargetDescriptions, false))
			.put(SnomedRf2Headers.FIELD_MAP_SOURCE, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::mapSources, false))
			.put(SnomedRf2Headers.FIELD_MRCM_RANGE_CONSTRAINT, new SnomedRefsetMemberFieldQueryHandler<>(String.class, values -> SnomedRefSetMemberIndexEntry.Expressions.rangeConstraint(Iterables.getOnlyElement(values)), false))
			.put(SnomedRf2Headers.FIELD_OWL_EXPRESSION, new SnomedRefsetMemberFieldQueryHandler<>(String.class, SnomedRefSetMemberIndexEntry.Expressions::owlExpressions, false))
			
			// Integer types
			.put(SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP, new SnomedRefsetMemberFieldQueryHandler<>(Integer.class, SnomedRefSetMemberIndexEntry.Expressions::relationshipGroups, false))
			.put(SnomedRf2Headers.FIELD_MAP_GROUP, new SnomedRefsetMemberFieldQueryHandler<>(Integer.class, SnomedRefSetMemberIndexEntry.Expressions::mapGroups, false))
			.put(SnomedRf2Headers.FIELD_MAP_PRIORITY, new SnomedRefsetMemberFieldQueryHandler<>(Integer.class, SnomedRefSetMemberIndexEntry.Expressions::mapPriority, false))
			.put(SnomedRf2Headers.FIELD_MAP_BLOCK, new SnomedRefsetMemberFieldQueryHandler<>(Integer.class, SnomedRefSetMemberIndexEntry.Expressions::mapBlock, false))
			
			// Boolean types
			.put(SnomedRf2Headers.FIELD_MRCM_GROUPED, new SnomedRefsetMemberFieldQueryHandler<>(Boolean.class, values -> SnomedRefSetMemberIndexEntry.Expressions.grouped(Iterables.getOnlyElement(values)), false))
			.put(SnomedRefSetMemberSearchRequestBuilder.OWL_EXPRESSION_GCI, new SnomedRefsetMemberFieldQueryHandler<>(Boolean.class, values -> SnomedRefSetMemberIndexEntry.Expressions.gciAxiom(Iterables.getOnlyElement(values)), false))
			.build();
	
	private static final Set<SnomedRefSetType> REVERSE_MAP_TYPES = Set.of(SnomedRefSetType.SIMPLE_MAP_TO);
	private static final Set<SnomedRefSetType> FORWARD_MAP_TYPES = Set.copyOf(Sets.difference(Set.copyOf(SnomedRefSetUtil.getMapTypeRefSets()), REVERSE_MAP_TYPES));
	
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
		COMPONENT,
		
		/**
		 * Filter by map source, irrespective of the RF2 field name
		 */
		MAP_SOURCE,
		
		/**
		 * Filter by map target, irrespective of the RF2 field name 
		 */
		MAP_TARGET,
	}

	SnomedRefSetMemberSearchRequest() {}
	
	@Override
	protected Class<SnomedRefSetMemberIndexEntry> getDocumentType() {
		return SnomedRefSetMemberIndexEntry.class;
	}
	
	@Override
	protected Multimap<String, String> collectFieldsToLoadReplacements() {
		return REPLACE_VALUE_FIELD;
	}
	
	@Override
	protected Expression prepareQuery(BranchContext context) {
		final Collection<String> referencedComponentIds = getCollection(OptionKey.REFERENCED_COMPONENT, String.class);
		final Collection<SnomedRefSetType> refSetTypes = getCollection(OptionKey.REFSET_TYPE, SnomedRefSetType.class);
		final Options propsFilter = getOptions(OptionKey.PROPS);
		
		ExpressionBuilder queryBuilder = Expressions.bool();
		
		addActiveClause(queryBuilder);
		addReleasedClause(queryBuilder);
		addEclFilter(context, queryBuilder, SnomedSearchRequest.OptionKey.MODULE, SnomedDocument.Expressions::modules);
		addIdFilter(queryBuilder, RevisionDocument.Expressions::ids);
		addEffectiveTimeClause(queryBuilder);
		addEclFilter(context, queryBuilder, OptionKey.REFSET, SnomedRefSetMemberIndexEntry.Expressions::refsetIds);
		addComponentClause(queryBuilder);
		addMapClauses(queryBuilder);
		
		if (containsKey(OptionKey.REFERENCED_COMPONENT_TYPE)) {
			queryBuilder.filter(referencedComponentTypes(getCollection(OptionKey.REFERENCED_COMPONENT_TYPE, String.class)));
		}
		
		if (!referencedComponentIds.isEmpty()) {
			queryBuilder.filter(referencedComponentIds(referencedComponentIds));
		}
		
		if (!refSetTypes.isEmpty()) {
			queryBuilder.filter(refSetTypes(refSetTypes));
		}
		
		prepareRefsetMemberFieldQuery(context, queryBuilder, propsFilter);
		
		return queryBuilder.build();
	}

	public void prepareRefsetMemberFieldQuery(BranchContext context, ExpressionBuilder queryBuilder, Options propsFilter) {
		if (!propsFilter.isEmpty()) {
			final Set<String> propKeys = newHashSet(propsFilter.keySet());
			
			for (String refsetFieldName : SUPPORTED_MEMBER_FIELDS.keySet()) {
				final String operatorKey = SearchResourceRequest.operator(refsetFieldName);
				SearchResourceRequest.Operator op;
				
				// always remove the operatorKey even if the corresponding refset key is not defined
				if (propKeys.remove(operatorKey)) {
					op = propsFilter.get(operatorKey, Operator.class);
				} else {
					// if no operator defined then fall back to equals
					op = SearchResourceRequest.Operator.EQUALS;
				}
				
				if (propKeys.remove(refsetFieldName)) {
					SnomedRefsetMemberFieldQueryHandler<?> handler = SUPPORTED_MEMBER_FIELDS.get(refsetFieldName);
					handler.prepareQuery(queryBuilder, op, propsFilter.getCollection(refsetFieldName, (Class) handler.getFieldType()), values -> evaluateEclFilter(context, values));
				}
			}
			
			// special concrete domain refset member handling, both data type and value
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
			
			// if any property left unhandled, raise error
			if (!propKeys.isEmpty()) {
				throw new IllegalQueryParameterException("Unsupported property filter(s), %s", propKeys);
			}
		}		
	}

	@Override
	protected SnomedReferenceSetMembers toCollectionResource(BranchContext context, Hits<SnomedRefSetMemberIndexEntry> hits) {
		if (limit() < 1 || hits.getTotal() < 1) {
			return new SnomedReferenceSetMembers(limit(), hits.getTotal());
		} else {
			return new SnomedReferenceSetMemberConverter(context, expand(), locales()).convert(hits);
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
				Expressions.bool()
					.should(referencedComponentIds(componentIds))
					.should(mapTargets(componentIds))
					.should(mapSources(componentIds))
				.build()
			);
		}
	}
	
	private void addMapClauses(ExpressionBuilder builder) {
		if (containsKey(OptionKey.MAP_SOURCE)) {
			final Collection<String> mapSourceIds = getCollection(OptionKey.MAP_SOURCE, String.class);
			
			builder.filter(Expressions.bool()
				.should(Expressions.bool()
					.filter(refSetTypes(REVERSE_MAP_TYPES))
					.filter(mapSources(mapSourceIds))
					.build())
				.should(Expressions.bool()
					.filter(refSetTypes(FORWARD_MAP_TYPES))
					.filter(referencedComponentIds(mapSourceIds))
					.build())
				.build());
		}
		
		if (containsKey(OptionKey.MAP_TARGET)) {
			final Collection<String> mapTargetIds = getCollection(OptionKey.MAP_TARGET, String.class);
			
			builder.filter(Expressions.bool()
				.should(Expressions.bool()
					.filter(refSetTypes(REVERSE_MAP_TYPES))
					.filter(referencedComponentIds(mapTargetIds))
					.build())
				.should(Expressions.bool()
					.filter(refSetTypes(FORWARD_MAP_TYPES))
					.filter(mapTargets(mapTargetIds))
					.build())
				.build());
		}
	}

	@Override
	protected SnomedReferenceSetMembers createEmptyResult(int limit) {
		return new SnomedReferenceSetMembers(limit, 0);
	}
}
