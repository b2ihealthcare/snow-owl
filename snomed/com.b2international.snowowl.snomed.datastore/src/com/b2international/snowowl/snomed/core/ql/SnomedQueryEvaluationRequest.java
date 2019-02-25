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
import java.util.Deque;

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
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ql.ql.ActiveFilter;
import com.b2international.snowowl.snomed.ql.ql.Conjunction;
import com.b2international.snowowl.snomed.ql.ql.Constraint;
import com.b2international.snowowl.snomed.ql.ql.Disjunction;
import com.b2international.snowowl.snomed.ql.ql.Domain;
import com.b2international.snowowl.snomed.ql.ql.Exclusion;
import com.b2international.snowowl.snomed.ql.ql.ModuleFilter;
import com.b2international.snowowl.snomed.ql.ql.NestedFilter;
import com.b2international.snowowl.snomed.ql.ql.Query;
import com.b2international.snowowl.snomed.ql.ql.TermFilter;
import com.b2international.snowowl.snomed.ql.ql.TypeFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Queues;

/**
 * @since 6.12
 */
final class SnomedQueryEvaluationRequest implements Request<BranchContext, Promise<Expression>> {

	private static final long serialVersionUID = 8932162693072727864L;

	@JsonIgnore
	private transient final PolymorphicDispatcher<Promise<Void>> dispatcher = PolymorphicDispatcher.createForSingleTarget("eval", 2, 2, this);

	@Nullable
	@JsonProperty
	private String expression;

	@JsonIgnore
	private transient Deque<Expression> conceptQuery;
	
	@JsonIgnore
	private transient Deque<Expression> descriptionQuery;
	
	void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public Promise<Expression> execute(BranchContext context) {
		conceptQuery = Queues.newLinkedBlockingDeque();
		descriptionQuery = Queues.newLinkedBlockingDeque();
		
		final Query query = context.service(SnomedQueryParser.class).parse(expression);
		if (query == null || query.getEcl() == null) {
			return Promise.immediate(MatchNone.INSTANCE);
		}
		
		final String ecl = context.service(SnomedQuerySerializer.class).serialize(query.getEcl());
		final Promise<Expression> eclExpression = SnomedRequests.prepareEclEvaluation(ecl).build().execute(context);
		if (query.getConstraint() != null) {
			Promise<Void> eval = evaluate(context, query.getConstraint());
			if (eval != null) {
				// wait for any subexpression evaluation before we return the resulting expression
				eval.getSync();
			}
		}
		
		Expression conceptPart = getStackExpression(conceptQuery);
		Expression descriptionPart;
		try {
			descriptionPart = executeDescriptionSearch(context, getStackExpression(descriptionQuery));
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		
		return eclExpression.then(eclPart -> {
			ExpressionBuilder bool = Expressions.builder();
			
			if (!eclPart.isMatchAll()) {
				bool.filter(eclPart);
			}
			
			if (!conceptPart.isMatchAll()) {
				bool.filter(conceptPart);
			}

			if (!descriptionPart.isMatchAll()) {
				bool.filter(descriptionPart);
			}
			
			return bool.build();
		});
	}
	
	private static Expression executeDescriptionSearch(BranchContext context, Expression descriptionQuery) throws IOException {
		if (descriptionQuery.isMatchAll()) {
			return Expressions.matchAll();
		}
		
		final RevisionSearcher searcher = context.service(RevisionSearcher.class);
		return SnomedDocument.Expressions.ids(searcher.search(com.b2international.index.query.Query
			.select(String.class)
			.from(SnomedDescriptionIndexEntry.class)
			.fields(SnomedDescriptionIndexEntry.Fields.CONCEPT_ID)
			.where(descriptionQuery)
			.limit(Integer.MAX_VALUE)
			.build()
		).getHits());
	}

	private static Expression getStackExpression(Deque<Expression> stack) {
		if (stack.isEmpty()) {
			return Expressions.matchAll();
		} else if (stack.size() == 1) {
			return stack.pop();
		} else {
			throw new IllegalStateException("Illegal internal stack state: " + stack);
		}
	}

	private Promise<Void> evaluate(BranchContext context, EObject expression) {
		return dispatcher.invoke(context, expression);
	}
	
	protected Promise<Void> eval(BranchContext context, final EObject constraint) {
		throw new UnsupportedOperationException("Not implemented constraint: " + constraint);
	}
	
	protected Promise<Void> eval(BranchContext context, final NestedFilter nestedFilter) {
		return evaluate(context, nestedFilter.getConstraint());
	}
	
	protected Promise<Void> eval(BranchContext context, final ActiveFilter activeFilter) {
		getStack(activeFilter).push(SnomedDocument.Expressions.active(activeFilter.isActive()));
		return null;
	}
	
	protected Promise<Void> eval(BranchContext context, final ModuleFilter moduleFilter) {
		return EclExpression.of(context.service(SnomedQuerySerializer.class).serialize(moduleFilter.getModuleId())).resolve(context)
			.then(moduleIds -> {
				getStack(moduleFilter).push(SnomedDocument.Expressions.modules(moduleIds));
				return null;
			});
	}
	
	protected Promise<Void> eval(BranchContext context, final TermFilter termFilter) {
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
		getStack(termFilter).push(expression);
		return null;
	}
	
	protected Promise<Void> eval(BranchContext context, final TypeFilter typeFilter) {
		return EclExpression.of(context.service(SnomedQuerySerializer.class).serialize(typeFilter.getType())).resolve(context)
				.then(typeIds -> {
					getStack(typeFilter).push(SnomedDescriptionIndexEntry.Expressions.types(typeIds));
					return null;
				});
	}
	
//	protected Promise<Void> eval(BranchContext context, final PreferredInFilter preferredInFilter) {
//		return EclExpression.of(context.service(SnomedQuerySerializer.class).serialize(preferredInFilter.getLanguageRefSetId())).resolve(context)
//				.then(typeIds -> {
//					getStack(typeFilter).push(SnomedDescriptionIndexEntry.Expressions.types(typeIds));
//					return null;
//				});
//		descriptionSearch.filterByPreferredIn(context.service(EclSerializer.class).serializeWithoutTerms(preferredInFilter.getLanguageRefSetId()));
//		return null;
//	}
	
//	protected Promise<Void> eval(BranchContext context, final AcceptableInFilter preferredInFilter) {
//		descriptionSearch.filterByPreferredIn(context.service(EclSerializer.class).serializeWithoutTerms(preferredInFilter.getLanguageRefSetId()));
//		return null;
//	}
	
//	protected Promise<Void> eval(BranchContext context, final LanguageRefSetFilter preferredInFilter) {
//		descriptionSearch.filterByPreferredIn(context.service(EclSerializer.class).serializeWithoutTerms(preferredInFilter.getLanguageRefSetId()));
//		return null;
//	}
	
	protected Promise<Void> eval(BranchContext context, final Conjunction conjunction) {
		// eval both sides
		evaluate(context, conjunction.getLeft());
		evaluate(context, conjunction.getRight());
		// pop Expressions from deques based on domain
		Deque<Expression> stack = getStack(conjunction);

		// invocation order matters to get the left and right side properly out of deque
		Expression right = stack.pop();
		Expression left = stack.pop();
		
		stack.push(Expressions.builder()
			.filter(left)
			.filter(right)
			.build());
		
		return null;
	}
	
	protected Promise<Void> eval(BranchContext context, final Disjunction disjunction) {
		// eval both sides
		evaluate(context, disjunction.getLeft());
		evaluate(context, disjunction.getRight());
		// pop Expressions from deques based on domain
		Deque<Expression> stack = getStack(disjunction);
		
		// invocation order matters to get the left and right side properly out of deque
		Expression right = stack.pop();
		Expression left = stack.pop();
		
		stack.push(Expressions.builder()
			.should(left)
			.should(right)
			.build());
		
		return null;
	}
	
	protected Promise<Expression> eval(BranchContext context, final Exclusion exclusion) {
		// eval both sides
		evaluate(context, exclusion.getLeft());
		evaluate(context, exclusion.getRight());
		// pop Expressions from deques based on domain
		Deque<Expression> stack = getStack(exclusion);

		// invocation order matters to get the left and right side properly out of deque
		Expression right = stack.pop();
		Expression left = stack.pop();
		
		stack.push(Expressions.builder()
			.filter(left)
			.mustNot(right)
			.build());
		
		return null;
	}
	
	private Deque<Expression> getStack(Constraint constraint) {
		final Domain domain = getDomain(constraint);
		switch (domain) {
		case CONCEPT:
			return conceptQuery;
		case DESCRIPTION:
			return descriptionQuery;
		default:
			throw new UnsupportedOperationException("Not supported domain stack: " + domain);
		}
	}
	
}
