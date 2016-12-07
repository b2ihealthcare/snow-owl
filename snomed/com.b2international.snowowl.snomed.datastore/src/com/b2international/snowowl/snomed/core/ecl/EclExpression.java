/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.Ecl;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.4
 */
final class EclExpression {

	private final String ecl;
	
	private Promise<Set<String>> promise;
	private Promise<Expression> expressionPromise;
	private Promise<SnomedConcepts> conceptPromise;

	private EclExpression(String ecl) {
		this.ecl = ecl.trim();
	}
	
	public String getEcl() {
		return ecl;
	}
	
	public boolean isAnyExpression() {
		return Ecl.ANY.equals(ecl);
	}
	
	public Promise<Set<String>> resolve(final BranchContext context) {
		if (promise == null) {
			promise = SnomedRequests.prepareSearchConcept()
					.all()
					.setFields(ImmutableSet.of(SnomedConceptDocument.Fields.ID))
					.filterByEcl(ecl)
					.build(context.id(), context.branch().path())
					.execute(context.service(IEventBus.class))
					.then(new Function<SnomedConcepts, Set<String>>() {
						@Override
						public Set<String> apply(SnomedConcepts input) {
							return FluentIterable.from(input).transform(IComponent.ID_FUNCTION).toSet();
						}
					});
		}
		return promise;
	}
	
	public Promise<SnomedConcepts> resolveConcepts(final BranchContext context) {
		if (conceptPromise == null) {
			conceptPromise = SnomedRequests.prepareSearchConcept()
					.all()
					.filterByEcl(ecl)
					.build(context.id(), context.branch().path())
					.execute(context.service(IEventBus.class));
		}
		return conceptPromise;
	}

	public Promise<Expression> resolveToExpression(final BranchContext context) {
		if (expressionPromise == null) {
			expressionPromise = SnomedRequests.prepareEclEvaluation(ecl)
					.build(context.id(), context.branch().path())
					.execute(context.service(IEventBus.class))
					.thenWith(result -> result);
		}
		return expressionPromise;
	}
	
	public static EclExpression of(String ecl) {
		return new EclExpression(ecl);
	}

	public Promise<Expression> resolveToExclusionExpression(final BranchContext context, final Set<String> excludedMatches) {
		return resolveToExpression(context)
				.then(new Function<Expression, Expression>() {
					@Override
					public Expression apply(Expression it) {
						if (!excludedMatches.isEmpty()) {
							return Expressions.builder().must(it).mustNot(RevisionDocument.Expressions.ids(excludedMatches)).build();
						} else {
							return it;
						}
					}
				});
	}
	
}
