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
package com.b2international.snowowl.snomed.core.ecl;

import static com.b2international.snomed.ecl.Ecl.isAnyExpression;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.activeMemberOf;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Fields.ACTIVE_MEMBER_OF;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.options.Options;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snomed.ecl.ecl.*;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.ecl.EclEvaluationRequest;
import com.b2international.snowowl.core.request.search.MatchTermFilter;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionUtils;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberSearchRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import jakarta.validation.constraints.NotNull;

/**
 * Evaluates the given ECL expression {@link String} or parsed {@link ExpressionConstraint} to an executable {@link Expression query expression}.
 * <p>
 * <i>NOTE: This request implementation is currently not working in remote environments, when the request need to be sent over the network, because
 * the {@link Expression expression API} is not serializable.</i>
 * </p>
 * 
 * @since 5.4
 */
final class SnomedEclEvaluationRequest extends EclEvaluationRequest<BranchContext> {

	private static final long serialVersionUID = 5891665196136989183L;
	
	private static final Map<String, String> ACCEPTABILITY_ID_TO_FIELD = Map.of(
		Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED, "preferredIn",
		Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE, "acceptableIn"
	);

	@NotNull
	@JsonProperty
	private String expressionForm = Trees.INFERRED_FORM;

	SnomedEclEvaluationRequest() {
	}

	void setExpressionForm(String expressionForm) {
		this.expressionForm = expressionForm;
	}

	/**
	 * Handles MemberOf simple expression constraints
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.1+Simple+Expression+Constraints
	 */
	protected Promise<Expression> eval(BranchContext context, MemberOf memberOf) {
		List<String> refsetFields = Collections3.toImmutableList(memberOf.getRefsetFields());
		if (refsetFields.size() > 1 || (!refsetFields.isEmpty() && !refsetFields.contains(SnomedRf2Headers.FIELD_REFERENCED_COMPONENT_ID))) {
			return throwUnsupported(memberOf, "Unsupported refsetFieldName selection: " + refsetFields);
		}
		
		final ExpressionConstraint inner = memberOf.getConstraint();
		if (inner instanceof EclConceptReference) {
			final EclConceptReference concept = (EclConceptReference) inner;
			return Promise.immediate(activeMemberOf(concept.getId()));
		} else if (isAnyExpression(inner)) {
			return Promise.immediate(Expressions.exists(ACTIVE_MEMBER_OF));
		} else if (inner instanceof NestedExpression) {
			return EclExpression.of(inner, expressionForm)
					.resolve(context)
					.then(ids -> activeMemberOf(ids));
		} else {
			return throwUnsupported(inner);
		}
	}
	
	/**
	 * Delegates evaluation of Refinement expression constraints to {@link SnomedEclRefinementEvaluator}.
	 */
	protected Promise<Expression> eval(final BranchContext context, final RefinedExpressionConstraint refined) {
		return new SnomedEclRefinementEvaluator(EclExpression.of(refined.getConstraint(), expressionForm)).evaluate(context, refined.getRefinement());
	}
	
	/**
	 * Handles dotted expression constraints (reversed attribute refinement with dot notation)
	 * @see https://confluence.ihtsdotools.org/display/DOCECL/6.2+Refinements
	 */
	protected Promise<Expression> eval(BranchContext context, DottedExpressionConstraint dotted) {
		final Promise<Set<String>> focusConceptIds = SnomedEclRefinementEvaluator.evalToConceptIds(context, dotted.getConstraint(), expressionForm);
		final Promise<Set<String>> typeConceptIds = SnomedEclRefinementEvaluator.evalToConceptIds(context, dotted.getAttribute(), expressionForm);
		return Promise.all(focusConceptIds, typeConceptIds)
			.thenWith(responses -> {
				Set<String> focusConcepts = (Set<String>) responses.get(0);
				Set<String> typeConcepts = (Set<String>) responses.get(1);
				return SnomedEclRefinementEvaluator.evalStatements(context, focusConcepts, typeConcepts, null /* ANY */, false, expressionForm);
			})
			.then(input -> input.stream().map(SnomedEclRefinementEvaluator.Property::getValue).filter(String.class::isInstance).map(String.class::cast).collect(Collectors.toSet()))
			.then(matchIdsOrNone());
	}
	
	@Override
	protected Promise<Expression> evaluateFilterExpressionOnDomain(BranchContext context, Promise<Expression> filterExpression, Domain filterDomain) {
		if (Domain.CONCEPT.equals(filterDomain)) {
			// default case just return the expression as is
			return filterExpression;
		} else if (Domain.DESCRIPTION.equals(filterDomain)) {
			// Find concepts that match the description expression, then use the resulting concept IDs as the expression
			return filterExpression.then(ex -> executeDescriptionSearch(context, ex));
		} else if (Domain.MEMBER.equals(filterDomain)) {
			// Find concepts that match the member expression, then use the resulting concept IDs as the expression
			return filterExpression.then(ex -> executeMemberSearch(context, ex));
		} else {
			throw new BadRequestException("Not supported ECL domain type: %s", filterDomain);
		}
	}
	
	private static Expression executeDescriptionSearch(BranchContext context, Expression descriptionExpression) {
		if (descriptionExpression.isMatchAll()) {
			return Expressions.matchAll();
		} else if (descriptionExpression.isMatchNone()) {
			return SnomedDocument.Expressions.ids(Set.of());
		}
		
		final RevisionSearcher searcher = context.service(RevisionSearcher.class);
			
		final Hits<String> descriptionHits = Query.select(String.class)
			.from(SnomedDescriptionIndexEntry.class)
			.fields(SnomedDescriptionIndexEntry.Fields.CONCEPT_ID)
			.where(descriptionExpression)
			.limit(Integer.MAX_VALUE)
			.build()
			.search(searcher);
		
		final Set<String> conceptIds = Set.copyOf(descriptionHits.getHits());
		return SnomedDocument.Expressions.ids(conceptIds);
	}
	
	private static Expression executeMemberSearch(BranchContext context, Expression memberExpression) {
		if (memberExpression.isMatchAll()) {
			return Expressions.matchAll();
		} else if (memberExpression.isMatchNone()) {
			return SnomedDocument.Expressions.ids(Set.of());
		}
		
		final RevisionSearcher searcher = context.service(RevisionSearcher.class);
		final Hits<String> memberHits = Query.select(String.class)
			.from(SnomedRefSetMemberIndexEntry.class)
			.fields(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
			.where(memberExpression)
			.limit(Integer.MAX_VALUE)
			.build()
			.search(searcher);
		
		final Set<String> conceptIds = Set.copyOf(memberHits.getHits());
		return SnomedDocument.Expressions.ids(conceptIds);
	}

	protected Promise<Expression> eval(BranchContext context, final ActiveFilter activeFilter) {
		final Operator op = Operator.fromString(activeFilter.getOp());
		Expression expression = SnomedDocument.Expressions.active(activeFilter.isActive());
		if (op == Operator.NOT_EQUALS) {
			expression = Expressions.bool()
					.mustNot(expression)
					.build();
		}
		return Promise.immediate(expression);
	}
	
	protected Promise<Expression> eval(BranchContext context, final ModuleFilter moduleFilter) {
		final Operator op = Operator.fromString(moduleFilter.getOp());
		final FilterValue moduleId = moduleFilter.getModuleId();
		return evaluate(context, moduleId)
			.then(resolveIds(context))
			.then((moduleIds) -> {
				Expression expression = SnomedDocument.Expressions.modules(moduleIds);
				if (op == Operator.NOT_EQUALS) {
					expression = Expressions.bool()
							.mustNot(expression)
							.build();
				}
				return expression;
			});
	}
	
	protected Promise<Expression> eval(BranchContext context, final EffectiveTimeFilter effectiveFilter) {
		final String effectiveTimeAsString = effectiveFilter.getEffectiveTime();
		final long effectiveTime = EffectiveTimes.getEffectiveTime(effectiveTimeAsString, DateFormats.SHORT);
		final Operator op = Operator.fromString(effectiveFilter.getOp());
		
		final Expression expression;
		switch (op) {
			case EQUALS:
				expression = SnomedDocument.Expressions.effectiveTime(effectiveTime);
				break;
			case GT:
				expression = SnomedDocument.Expressions.effectiveTime(effectiveTime, Long.MAX_VALUE, false, true);
				break;
			case GTE:
				expression = SnomedDocument.Expressions.effectiveTime(effectiveTime, Long.MAX_VALUE, true, true);
				break;
			case LT:
				expression = SnomedDocument.Expressions.effectiveTime(Long.MIN_VALUE, effectiveTime, true, false);
				break;
			case LTE:
				expression = SnomedDocument.Expressions.effectiveTime(Long.MIN_VALUE, effectiveTime, true, true);
				break;
			case NOT_EQUALS:
				expression = Expressions.bool()
					.mustNot(SnomedDocument.Expressions.effectiveTime(effectiveTime))
					.build();
				break;
			default:
				throw new IllegalStateException("Unhandled ECL search operator '" + op + "'.");
		}

		return Promise.immediate(expression);
	}

	protected Promise<Expression> eval(BranchContext context, final DefinitionStatusIdFilter definitionStatusIdFilter) {
		final String op = definitionStatusIdFilter.getOp();
		final Operator eclOperator = Operator.fromString(op);
		final FilterValue definitionStatus = definitionStatusIdFilter.getDefinitionStatus();
		
		return evalDefinitionStatus(evaluate(context, definitionStatus)
				.then(resolveIds(context)),
				Operator.NOT_EQUALS.equals(eclOperator));
	}
	
	protected Promise<Expression> eval(BranchContext context, final DefinitionStatusTokenFilter definitionStatusTokenFilter) {
		final String op = definitionStatusTokenFilter.getOp();
		final Operator eclOperator = Operator.fromString(op);

		final Set<String> definitionStatusIds = definitionStatusTokenFilter.getDefinitionStatusTokens()
			.stream()
			.map(DefinitionStatusToken::fromString)
			.filter(Predicates.notNull())
			.map(DefinitionStatusToken::getConceptId)
			.collect(Collectors.toSet());
		
		return evalDefinitionStatus(Promise.immediate(definitionStatusIds), 
				Operator.NOT_EQUALS.equals(eclOperator));
	}
	
	private Promise<Expression> evalDefinitionStatus(final Promise<? extends Iterable<String>> definitionStatusIds, final boolean negate) {
		return definitionStatusIds				
			.then(SnomedConceptDocument.Expressions::definitionStatusIds)
			.then(expression -> negate
				? Expressions.bool().mustNot(expression).build()
				: expression);
	}

	protected Promise<Expression> eval(BranchContext context, final SemanticTagFilter semanticTagFilter) {
		final String op = semanticTagFilter.getOp();
		final Operator eclOperator = Operator.fromString(op);
		
		// XXX: Both concept and description documents support the same field and query
		Expression expression = SnomedDescriptionIndexEntry.Expressions.semanticTags(List.of(semanticTagFilter.getSemanticTag()));
		if (Operator.NOT_EQUALS.equals(eclOperator)) {
			expression = Expressions.bool()
				.mustNot(expression)
				.build();
		}
		
		return Promise.immediate(expression);
	}

	protected Promise<Expression> eval(BranchContext context, final TypeTokenFilter typeTokenFilter) {
		final Set<String> typeIds = typeTokenFilter.getTokens()
			.stream()
			.map(DescriptionTypeToken::fromString)
			.filter(Predicates.notNull())
			.map(DescriptionTypeToken::getConceptId)
			.collect(Collectors.toSet());
		
		return Promise.immediate(SnomedDescriptionIndexEntry.Expressions.types(typeIds));
	}
	
	protected Promise<Expression> eval(BranchContext context, final TypeIdFilter typeIdFilter) {
		final FilterValue type = typeIdFilter.getType();
		return evaluate(context, type)
			.then(resolveIds(context))
			.then(SnomedDescriptionIndexEntry.Expressions::types);
	}
	
	protected Promise<Expression> eval(BranchContext context, final PreferredInFilter preferredInFilter) {
		final FilterValue languageRefSetId = preferredInFilter.getLanguageRefSetId();
		return evaluate(context, languageRefSetId)
			.then(resolveIds(context))
			.then(SnomedDescriptionIndexEntry.Expressions::preferredIn);
	}
	
	protected Promise<Expression> eval(BranchContext context, final AcceptableInFilter acceptableInFilter) {
		final FilterValue languageRefSetId = acceptableInFilter.getLanguageRefSetId();
		return evaluate(context, languageRefSetId)
			.then(resolveIds(context))
			.then(SnomedDescriptionIndexEntry.Expressions::acceptableIn);
	}
	
	protected Promise<Expression> eval(BranchContext context, final LanguageRefSetFilter languageRefSetFilter) {
		final FilterValue languageRefSetId = languageRefSetFilter.getLanguageRefSetId();
		return evaluate(context, languageRefSetId)
			.then(resolveIds(context))
			.then(languageRefsetIds -> {
				return Expressions.bool()
					.should(SnomedDescriptionIndexEntry.Expressions.acceptableIn(languageRefsetIds))
					.should(SnomedDescriptionIndexEntry.Expressions.preferredIn(languageRefsetIds))
					.build();
			});
	}
	
	protected Promise<Expression> eval(BranchContext context, final CaseSignificanceFilter caseSignificanceFilter) {
		final FilterValue caseSignificanceId = caseSignificanceFilter.getCaseSignificanceId();
		return evaluate(context, caseSignificanceId)
			.then(resolveIds(context))
			.then(SnomedDescriptionIndexEntry.Expressions::caseSignificances);
	}
	
	protected Promise<Expression> eval(BranchContext context, final LanguageFilter languageCodeFilter) {
		return Promise.immediate(SnomedDescriptionIndexEntry.Expressions.languageCodes(languageCodeFilter.getLanguageCodes()));
	}
	
	protected Promise<Expression> eval(BranchContext context, final ConjunctionFilter conjunction) {
		return Promise.all(evaluate(context, conjunction.getLeft()), evaluate(context, conjunction.getRight()))
				.then(results -> {
					Expression left = (Expression) results.get(0);
					Expression right = (Expression) results.get(1);
					return Expressions.bool()
							.filter(left)
							.filter(right)
							.build();
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final DisjunctionFilter disjunction) {
		return Promise.all(evaluate(context, disjunction.getLeft()), evaluate(context, disjunction.getRight()))
				.then(results -> {
					Expression left = (Expression) results.get(0);
					Expression right = (Expression) results.get(1);
					return Expressions.bool()
							.should(left)
							.should(right)
							.build();
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final DialectAliasFilter dialectAliasFilter) {
		final ListMultimap<String, String> languageMapping = SnomedDescriptionUtils.getLanguageMapping(context);
		final Multimap<String, String> languageRefSetsByAcceptabilityField = HashMultimap.create();
		final ExpressionBuilder dialectQuery = Expressions.bool();
		for (DialectAlias alias : dialectAliasFilter.getDialects()) {
			final Set<String> acceptabilityFields = getAcceptabilityFields(alias.getAcceptability());
			final Collection<String> languageRefsetIds = languageMapping.get(alias.getAlias());
			
			// empty acceptabilities or empty language reference set IDs mean that none of the provided values were valid so no match should be returned
			if (acceptabilityFields.isEmpty() || languageRefsetIds.isEmpty()) {
				return Promise.immediate(Expressions.matchNone());
			}
			
			for (String acceptabilityField : acceptabilityFields) {
				languageRefSetsByAcceptabilityField.putAll(acceptabilityField, languageRefsetIds);
			}
		}
		
		languageRefSetsByAcceptabilityField.asMap().forEach((key, values) -> {
			Operator op = Operator.fromString(dialectAliasFilter.getOp());
			switch (op) {
				case EQUALS: 
					dialectQuery.should(Expressions.matchAny(key, values));
					break;
				case NOT_EQUALS:
					dialectQuery.mustNot(Expressions.matchAny(key, values));
					break;
				default: 
					throw new BadRequestException("Unsupported dialectAliasFilter operator '%s'", dialectAliasFilter.getOp());
			}
		});
		
		return Promise.immediate(dialectQuery.build());
	}
	
	protected Promise<Expression> eval(BranchContext context, final DialectIdFilter dialectIdFilter) {
		final Multimap<String, String> languageRefSetsByAcceptability = HashMultimap.create();
		final ExpressionBuilder dialectQuery = Expressions.bool();
		for (Dialect dialect : dialectIdFilter.getDialects()) {
			final ExpressionConstraint languageRefSetId = dialect.getLanguageRefSetId();
			final Set<String> evaluatedIds = EclExpression.of(languageRefSetId, expressionForm)
				.resolve(context)
				.getSync();

			final Set<String> acceptabilitiesToMatch = getAcceptabilityFields(dialect.getAcceptability());
			// empty set means that acceptability values are not valid and it should not match any descriptions/concepts
			if (acceptabilitiesToMatch.isEmpty()) {
				return Promise.immediate(Expressions.matchNone());
			}
			
			for (String acceptability : acceptabilitiesToMatch) {
				languageRefSetsByAcceptability.putAll(acceptability, evaluatedIds);
			}
		}
		
		languageRefSetsByAcceptability.asMap().forEach((key, values) -> {
			Operator op = Operator.fromString(dialectIdFilter.getOp());
			switch (op) {
			case EQUALS: 
				dialectQuery.should(Expressions.matchAny(key, values));
				break;
			case NOT_EQUALS:
				dialectQuery.mustNot(Expressions.matchAny(key, values));
				break;
			default: 
				throw new BadRequestException("Unsupported dialectIdFilter operator '%s'", dialectIdFilter.getOp());
			}
		});
		
		return Promise.immediate(dialectQuery.build());
	}
	
	protected Promise<Expression> eval(BranchContext context, final MemberFieldFilter memberFieldFilter) {
		final Comparison comparison = memberFieldFilter.getComparison();
		
		final List<Object> values;
		if (comparison instanceof AttributeComparison) {
			values = List.copyOf(EclExpression.of(((AttributeComparison) comparison).getValue(), expressionForm).resolve(context).getSync(3, TimeUnit.MINUTES));
		} else if (comparison instanceof BooleanValueComparison) {
			values = List.of(((BooleanValueComparison) comparison).isValue());
		} else if (comparison instanceof StringValueComparison) {
			StringValueComparison stringValueComparison = (StringValueComparison) comparison;
			SearchTerm searchTerm = stringValueComparison.getValue();
			if (searchTerm instanceof TypedSearchTerm) {
				values = List.of(extractTerm(((TypedSearchTerm) searchTerm).getClause()));
			} else if (searchTerm instanceof TypedSearchTermSet) {
				values = ((TypedSearchTermSet) searchTerm).getClauses().stream().map(SnomedEclEvaluationRequest::extractTerm).collect(Collectors.toList());
			} else {
				return SnomedEclEvaluationRequest.throwUnsupported(searchTerm);
			}
		} else if (comparison instanceof IntegerValueComparison) {
			values = List.of(((IntegerValueComparison) comparison).getValue());
		} else if (comparison instanceof DecimalValueComparison) {
			values = List.of(((DecimalValueComparison) comparison).getValue());
		} else {
			return SnomedEclEvaluationRequest.throwUnsupported(comparison);
		}
		
		ExpressionBuilder queryBuilder = Expressions.bool();
		
		// FIXME it would be better to use a static method or some other class type that has the necessary logic to evaluate multivalued ECL expressions properly 
		((SnomedRefSetMemberSearchRequest) SnomedRequests.prepareSearchMember()
			.setEclExpressionForm(expressionForm)
			.build())
			.prepareRefsetMemberFieldQuery(context, queryBuilder, Options.builder()
				.put(memberFieldFilter.getRefsetFieldName(), values)
				.put(SearchResourceRequest.operator(memberFieldFilter.getRefsetFieldName()), toSearchOperator(memberFieldFilter.getComparison().getOp()))
				.build());
		
		return Promise.immediate(queryBuilder.build());
	}
	
	protected Promise<Expression> eval(BranchContext context, final SupplementExpressionConstraint supplementExpression) {
		final RevisionSearcher searcher = context.service(RevisionSearcher.class);
		
		if (supplementExpression.getSupplement() == null || supplementExpression.getSupplement() instanceof HistorySupplement) {
			HistorySupplement historySupplement = (HistorySupplement) supplementExpression.getSupplement();
			
			final Set<String> refsetIds = evaluateProfile(context, historySupplement).getSync(3, TimeUnit.MINUTES);
			
			return evaluate(context, supplementExpression.getConstraint())
					.then(resolveIds(context))
					.then(focusConceptIds -> {
						if (!focusConceptIds.isEmpty() || !refsetIds.isEmpty()) {
							final Collection<String> historicalIds = Query.select(String.class)
									.from(SnomedRefSetMemberIndexEntry.class)
									.fields(SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID)
									.where(Expressions.bool()
											.filter(SnomedRefSetMemberIndexEntry.Expressions.active())
											.filter(SnomedRefSetMemberIndexEntry.Expressions.refsetIds(refsetIds))
											.filter(SnomedRefSetMemberIndexEntry.Expressions.targetComponentIds(focusConceptIds))
											.build())
									.limit(Integer.MAX_VALUE)
									.build()
									.search(searcher)
									.getHits();
							return ImmutableSet.<String>builder()
									.addAll(focusConceptIds)
									.addAll(historicalIds)
									.build();
						} else {
							// no focus concept set (or historical association refsets) => no historical concepts, return empty collection
							return Collections.<String>emptySet();
						}
						
					})
					.then(matchIdsOrNone());
		} else {
			return throwUnsupported(supplementExpression);
		}
		
	}

	private Promise<Set<String>> evaluateProfile(BranchContext context, HistorySupplement historySupplement) {
		EObject historyProfile = historySupplement.getHistory();
		if (historyProfile == null || historyProfile instanceof HistoryProfile) {
			HistoryProfileType historyProfileType = historyProfile == null ? null : HistoryProfileType.fromString(((HistoryProfile) historyProfile).getProfile());
			if (historyProfileType == null) {
				historyProfileType = HistoryProfileType.MAX;
			}
			
			switch (historyProfileType) {
			case MAX:
				return EclExpression.of("<" + Concepts.REFSET_HISTORICAL_ASSOCIATION, expressionForm).resolve(context);
			case MOD:
				return Promise.immediate(Set.of(
					Concepts.REFSET_SAME_AS_ASSOCIATION,
					Concepts.REFSET_REPLACED_BY_ASSOCIATION,
					Concepts.REFSET_WAS_A_ASSOCIATION,
					Concepts.REFSET_PARTIALLY_EQUIVALENT_TO_ASSOCIATION
				));
			case MIN:
				return Promise.immediate(Set.of(Concepts.REFSET_SAME_AS_ASSOCIATION));
			default: throw new UnsupportedOperationException("Unsupported history profile: " + historyProfileType);
			}
		} else if (historyProfile instanceof NestedExpression) {
			return evaluate(context, historyProfile).then(resolveIds(context));
		} else {
			throw new BadRequestException("Unsupported history supplement profile: %s", historyProfile);
		}
		
	}

	private SearchResourceRequest.Operator toSearchOperator(String operator) {
		Operator op = Operator.fromString(operator);
		if (op == null) {
			return SearchResourceRequest.Operator.EQUALS;
		}
		switch (op) {
		case EQUALS: return SearchResourceRequest.Operator.EQUALS;
		case NOT_EQUALS: return SearchResourceRequest.Operator.NOT_EQUALS;
		case GT: return SearchResourceRequest.Operator.GREATER_THAN;
		case GTE: return SearchResourceRequest.Operator.GREATER_THAN_EQUALS;
		case LT: return SearchResourceRequest.Operator.LESS_THAN;
		case LTE: return SearchResourceRequest.Operator.LESS_THAN_EQUALS;
		default: throw new BadRequestException("Unknown ECL operator '%s'", op);
		}
	}

	private Set<String> getAcceptabilityFields(Acceptability acceptability) {
		// in case of acceptability not defined accept any known acceptability value
		if (acceptability == null) {
			return Set.of("preferredIn", "acceptableIn");
		} else {
			return acceptability.getAcceptabilities()
				.getConcepts()
				.stream()
				.map(EclConceptReference::getId)
				.map(id -> {
					// resolve acceptability token aliases first, then fall back to SCTIDs
					AcceptabilityToken acceptabilityToken = AcceptabilityToken.fromString(id);
					if (acceptabilityToken != null) {
						id = acceptabilityToken.getConceptId();
					}
					return ACCEPTABILITY_ID_TO_FIELD.get(id);
				})
				.filter(Predicates.notNull())
				.collect(Collectors.toSet());
		}
	}

	@Override
	protected Promise<? extends Iterable<? extends IComponent>> resolveConcepts(BranchContext context, ExpressionConstraint constraint) {
		return EclExpression.of(constraint, expressionForm).resolveConcepts(context);
	}
	
	@Override
	protected Expression parentsExpression(Set<String> ids) {
		return Trees.INFERRED_FORM.equals(expressionForm) ? SnomedConceptDocument.Expressions.parents(ids) : SnomedConceptDocument.Expressions.statedParents(ids);
	}

	@Override
	protected Expression ancestorsExpression(Set<String> ids) {
		return Trees.INFERRED_FORM.equals(expressionForm) ? SnomedConceptDocument.Expressions.ancestors(ids) : SnomedConceptDocument.Expressions.statedAncestors(ids);
	}
	
	@Override
	protected Expression termMatchExpression(MatchTermFilter termFilter) {
		return termFilter.toExpression(SnomedDescriptionIndexEntry.Fields.TERM);
	}
	
	@Override
	protected Expression termRegexExpression(String regex, boolean caseInsensitive) {
		return Expressions.regexp(SnomedDescriptionIndexEntry.Fields.TERM, regex, caseInsensitive);
	}
	
	@Override
	protected Expression termWildExpression(String wild, boolean caseInsensitive) {
		return Expressions.wildcard(SnomedDescriptionIndexEntry.Fields.TERM, wild, caseInsensitive);
	}
	
	@Override
	protected Expression termCaseInsensitiveExpression(String term) {
		return com.b2international.snowowl.core.request.search.TermFilter.exact().term(term).caseSensitive(false).build().toExpression(SnomedDescriptionIndexEntry.Fields.TERM);
	}
	
	@Override
	protected Class<?> getDocumentType() {
		return SnomedConceptDocument.class;
	}

	@Override
	protected void addParentIds(IComponent concept, Set<String> collection) {
		if (concept instanceof SnomedConcept) {
			SnomedConcept snomedConcept = (SnomedConcept) concept;
			if (Trees.INFERRED_FORM.equals(expressionForm)) {
				if (snomedConcept.getParentIds() != null) {
					for (long parent : snomedConcept.getParentIds()) {
						if (IComponent.ROOT_IDL != parent) {
							collection.add(Long.toString(parent));
						}
					}
				}
			} else {
				if (snomedConcept.getStatedParentIds() != null) {
					for (long statedParent : snomedConcept.getStatedParentIds()) {
						if (IComponent.ROOT_IDL != statedParent) {
							collection.add(Long.toString(statedParent));
						}
					}
				}
			}
		} else {
			super.addParentIds(concept, collection);
		}
	}
	
	@Override
	protected void addAncestorIds(IComponent concept, Set<String> collection) {
		if (concept instanceof SnomedConcept) {
			SnomedConcept snomedConcept = (SnomedConcept) concept;
			if (Trees.INFERRED_FORM.equals(expressionForm)) {
				if (snomedConcept.getAncestorIds() != null) {
					for (long ancestor : snomedConcept.getAncestorIds()) {
						if (IComponent.ROOT_IDL != ancestor) {
							collection.add(Long.toString(ancestor));
						}
					}
				}
			} else {
				if (snomedConcept.getStatedAncestorIds() != null) {
					for (long statedAncestor : snomedConcept.getStatedAncestorIds()) {
						if (IComponent.ROOT_IDL != statedAncestor) {
							collection.add(Long.toString(statedAncestor));
						}
					}
				}
			}
		} else {
			super.addAncestorIds(concept, collection);
		}
	}
	
	/*package*/ static String extractTerm(TypedSearchTermClause clause) {
		LexicalSearchType searchType = LexicalSearchType.fromString(clause.getLexicalSearchType());
		if (searchType != null && LexicalSearchType.EXACT != searchType) {
			throw new BadRequestException("Not implemented ECL feature: match, wild and regex lexical search types are not supported in (concrete value, refset member field) string matching.");
		}
		return clause.getTerm();
	}
}
