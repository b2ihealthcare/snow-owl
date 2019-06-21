/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ql;

import static com.b2international.snowowl.snomed.ql.QLRuntimeModule.getDomain;

import java.io.IOException;

import javax.annotation.Nullable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.MatchNone;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.snomed.core.ecl.EclExpression;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter;
import com.b2international.snowowl.snomed.ql.ql.ActiveFilter;
import com.b2international.snowowl.snomed.ql.ql.CaseSignificanceFilter;
import com.b2international.snowowl.snomed.ql.ql.Conjunction;
import com.b2international.snowowl.snomed.ql.ql.Disjunction;
import com.b2international.snowowl.snomed.ql.ql.Domain;
import com.b2international.snowowl.snomed.ql.ql.DomainQuery;
import com.b2international.snowowl.snomed.ql.ql.Exclusion;
import com.b2international.snowowl.snomed.ql.ql.LanguageCodeFilter;
import com.b2international.snowowl.snomed.ql.ql.LanguageRefSetFilter;
import com.b2international.snowowl.snomed.ql.ql.ModuleFilter;
import com.b2international.snowowl.snomed.ql.ql.NestedFilter;
import com.b2international.snowowl.snomed.ql.ql.NestedQuery;
import com.b2international.snowowl.snomed.ql.ql.PreferredInFilter;
import com.b2international.snowowl.snomed.ql.ql.Query;
import com.b2international.snowowl.snomed.ql.ql.QueryConjunction;
import com.b2international.snowowl.snomed.ql.ql.QueryDisjunction;
import com.b2international.snowowl.snomed.ql.ql.QueryExclusion;
import com.b2international.snowowl.snomed.ql.ql.TermFilter;
import com.b2international.snowowl.snomed.ql.ql.TypeFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.12
 */
final class SnomedQueryEvaluationRequest implements Request<BranchContext, Promise<Expression>> {

	private static final long serialVersionUID = 8932162693072727864L;

	@JsonIgnore
	private transient final PolymorphicDispatcher<Promise<Expression>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this);

	@Nullable
	@JsonProperty
	private String expression;
	
	void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public Promise<Expression> execute(BranchContext context) {
		return evaluate(context, context.service(SnomedQueryParser.class).parse(expression));
	}
	
	private static Expression executeDescriptionSearch(BranchContext context, Expression descriptionQuery) {
		if (descriptionQuery.isMatchAll()) {
			return Expressions.matchAll();
		}
		
		final RevisionSearcher searcher = context.service(RevisionSearcher.class);
		try {
			return SnomedDocument.Expressions.ids(searcher.search(com.b2international.index.query.Query
					.select(String.class)
					.from(SnomedDescriptionIndexEntry.class)
					.fields(SnomedDescriptionIndexEntry.Fields.CONCEPT_ID)
					.where(descriptionQuery)
					.limit(Integer.MAX_VALUE)
					.build()
				).getHits());
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	private Promise<Expression> evaluate(BranchContext context, EObject expression) {
		return dispatcher.invoke(context, expression);
	}
	
	protected Promise<Expression> eval(BranchContext context, final EObject query) {
		throw new UnsupportedOperationException("Not implemented case: " + query);
	}
	
	protected Promise<Expression> eval(BranchContext context, final Void empty) {
		return Promise.immediate(MatchNone.INSTANCE);
	}
	
	protected Promise<Expression> eval(BranchContext context, final Query query) {
		return evaluate(context, query.getQuery());
	}
	
	protected Promise<Expression> eval(BranchContext context, final NestedQuery query) {
		return evaluate(context, query.getNested());
	} 
	
	protected Promise<Expression> eval(BranchContext context, final QueryConjunction conjunction) {
		return Promise.all(evaluate(context, conjunction.getLeft()), evaluate(context, conjunction.getRight()))
				.then(results -> {
					Expression left = (Expression) results.get(0);
					Expression right = (Expression) results.get(1);
					return Expressions.builder()
							.filter(left)
							.filter(right)
							.build();
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final QueryDisjunction disjunction) {
		return Promise.all(evaluate(context, disjunction.getLeft()), evaluate(context, disjunction.getRight()))
				.then(results -> {
					Expression left = (Expression) results.get(0);
					Expression right = (Expression) results.get(1);
					return Expressions.builder()
							.should(left)
							.should(right)
							.build();
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final QueryExclusion exclusion) {
		return Promise.all(evaluate(context, exclusion.getLeft()), evaluate(context, exclusion.getRight()))
				.then(results -> {
					Expression left = (Expression) results.get(0);
					Expression right = (Expression) results.get(1);
					return Expressions.builder()
							.filter(left)
							.mustNot(right)
							.build();
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final DomainQuery query) {
		final String ecl = context.service(SnomedQuerySerializer.class).serialize(query.getEcl());
		final Promise<Expression> eclExpression = SnomedRequests.prepareEclEvaluation(ecl)
				.setExpressionForm(Trees.INFERRED_FORM) //TODO support STATED mode here
				.build()
				.execute(context);
		if (query.getFilter() != null) {
			return Promise.all(evaluate(context, query.getFilter()), eclExpression).then(subExpressions -> {
				Expression domainFilter = (Expression) subExpressions.get(0);
				Expression eclFilter = (Expression) subExpressions.get(1);
				
				ExpressionBuilder bool = Expressions.builder();
				if (!eclFilter.isMatchAll()) {
					bool.filter(eclFilter);
				}
				
				Domain domain = getDomain(query.getFilter());
				switch (domain) {
				case CONCEPT:
					if (!domainFilter.isMatchAll()) {
						bool.filter(domainFilter);
					}
					break;
				case DESCRIPTION:
					Expression conceptIdFilter = executeDescriptionSearch(context, domainFilter);
					if (!conceptIdFilter.isMatchAll()) {
						bool.filter(conceptIdFilter);
					}
					break;
				default:
					throw new UnsupportedOperationException("Not implemented domain case: " + domain);
				}
				
				return bool.build();
			});
		} else {
			return eclExpression;
		}
	}
	
	protected Promise<Expression> eval(BranchContext context, final NestedFilter nestedFilter) {
		return evaluate(context, nestedFilter.getNested());
	}
	
	protected Promise<Expression> eval(BranchContext context, final ActiveFilter activeFilter) {
		return Promise.immediate(SnomedDocument.Expressions.active(activeFilter.isActive()));
	}
	
	protected Promise<Expression> eval(BranchContext context, final ModuleFilter moduleFilter) {
		return EclExpression.of(context.service(SnomedQuerySerializer.class).serialize(moduleFilter.getModuleId()), Trees.INFERRED_FORM)
				.resolve(context)
				.then(SnomedDocument.Expressions::modules);
	}
	
	protected Promise<Expression> eval(BranchContext context, final TermFilter termFilter) {
		Expression expression;
		switch (termFilter.getLexicalSearchType()) {
		case MATCH:
			expression = SnomedDescriptionIndexEntry.Expressions.termDisjunctionQuery(termFilter.getTerm());
			break;
		case REGEX:
			expression = SnomedDescriptionIndexEntry.Expressions.matchTermRegex(termFilter.getTerm());
			break;
		case EXACT:
			expression = SnomedDescriptionIndexEntry.Expressions.matchEntireTerm(termFilter.getTerm());
			break;
		default:
			throw new UnsupportedOperationException("Not implemented lexical search type: " + termFilter.getLexicalSearchType());
		}
		return Promise.immediate(expression);
	}
	
	protected Promise<Expression> eval(BranchContext context, final TypeFilter typeFilter) {
		return EclExpression.of(context.service(SnomedQuerySerializer.class).serialize(typeFilter.getType()), Trees.INFERRED_FORM)
				.resolve(context)
				.then(SnomedDescriptionIndexEntry.Expressions::types);
	}
	
	protected Promise<Expression> eval(BranchContext context, final PreferredInFilter preferredInFilter) {
		return EclExpression.of(context.service(SnomedQuerySerializer.class).serialize(preferredInFilter.getLanguageRefSetId()), Trees.INFERRED_FORM)
				.resolve(context)
				.then(SnomedDescriptionIndexEntry.Expressions::preferredIn);
	}
	
	protected Promise<Expression> eval(BranchContext context, final AcceptableInFilter acceptableInFilter) {
		return EclExpression.of(context.service(SnomedQuerySerializer.class).serialize(acceptableInFilter.getLanguageRefSetId()), Trees.INFERRED_FORM)
				.resolve(context)
				.then(SnomedDescriptionIndexEntry.Expressions::acceptableIn);
	}
	
	protected Promise<Expression> eval(BranchContext context, final LanguageRefSetFilter languageRefSetFilter) {
		return EclExpression.of(context.service(SnomedQuerySerializer.class).serialize(languageRefSetFilter.getLanguageRefSetId()), Trees.INFERRED_FORM)
				.resolve(context)
				.then(languageReferenceSetIds -> {
					return Expressions.builder()
							.should(SnomedDescriptionIndexEntry.Expressions.acceptableIn(languageReferenceSetIds))
							.should(SnomedDescriptionIndexEntry.Expressions.preferredIn(languageReferenceSetIds))
							.build();
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final CaseSignificanceFilter caseSignificanceFilter) {
		return EclExpression.of(context.service(SnomedQuerySerializer.class).serialize(caseSignificanceFilter.getCaseSignificanceId()), Trees.INFERRED_FORM)
				.resolve(context)
				.then(SnomedDescriptionIndexEntry.Expressions::caseSignificances);
	}
	
	protected Promise<Expression> eval(BranchContext context, final LanguageCodeFilter languageCodeFilter) {
		return Promise.immediate(SnomedDescriptionIndexEntry.Expressions.languageCode(languageCodeFilter.getLanguageCode()));
	}
	
	protected Promise<Expression> eval(BranchContext context, final Conjunction conjunction) {
		return Promise.all(evaluate(context, conjunction.getLeft()), evaluate(context, conjunction.getRight()))
				.then(results -> {
					Expression left = (Expression) results.get(0);
					Expression right = (Expression) results.get(1);
					return Expressions.builder()
							.filter(left)
							.filter(right)
							.build();
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final Disjunction disjunction) {
		return Promise.all(evaluate(context, disjunction.getLeft()), evaluate(context, disjunction.getRight()))
				.then(results -> {
					Expression left = (Expression) results.get(0);
					Expression right = (Expression) results.get(1);
					return Expressions.builder()
							.should(left)
							.should(right)
							.build();
				});
	}
	
	protected Promise<Expression> eval(BranchContext context, final Exclusion exclusion) {
		return Promise.all(evaluate(context, exclusion.getLeft()), evaluate(context, exclusion.getRight()))
				.then(results -> {
					Expression left = (Expression) results.get(0);
					Expression right = (Expression) results.get(1);
					return Expressions.builder()
							.filter(left)
							.mustNot(right)
							.build();
				});
	}
	
}
