/*
 * Copyright 2020-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.SyntaxException;
import com.b2international.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.ecl.EclParser;
import com.b2international.snowowl.core.ecl.EclSerializer;
import com.b2international.snowowl.core.request.ecl.EclRewriter;
import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.collect.*;

/**
 * @since 7.7
 */
public final class QueryExpressionDiff implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final Equivalence<QueryExpression> EXPRESSION_EQUIVALENCE = new Equivalence<QueryExpression>() {
		
		@Override
		protected boolean doEquivalent(final QueryExpression a, final QueryExpression b) {
			return (a.isPinned() == b.isPinned()) && Objects.equals(a.getQuery(), b.getQuery());
		}
		
		@Override
		protected int doHash(final QueryExpression ex) {
			return Objects.hash(ex.isPinned(), ex.getQuery());
		}
	};

	private final List<QueryExpression> addToInclusion;
	private final List<QueryExpression> addToExclusion;
	private final List<QueryExpression> remove;
	
	public static QueryExpressionDiff create(
		final ServiceProvider serviceProvider,
		final Collection<QueryExpression> sourceIncludes,
		final Collection<QueryExpression> sourceExcludes,
		final Collection<QueryExpression> targetIncludes,
		final Collection<QueryExpression> targetExcludes
	) {
		final List<QueryExpression> removedExpressions = newArrayList();
		final List<QueryExpression> addedInclusions = compareContent(serviceProvider, sourceIncludes, targetIncludes, removedExpressions);
		final List<QueryExpression> addedExclusions = compareContent(serviceProvider, sourceExcludes, targetExcludes, removedExpressions);
		
		return new QueryExpressionDiff(addedInclusions, addedExclusions, removedExpressions);
	}

	private static List<QueryExpression> compareContent(
		final ServiceProvider serviceProvider,
		final Collection<QueryExpression> sourceExpressions,
		final Collection<QueryExpression> targetExpressions, 
		final List<QueryExpression> removedExpressions
	) {
		final Map<String, QueryExpression> sourceById = Maps.uniqueIndex(sourceExpressions, ex -> ex.getId());
		final Multimap<Wrapper<QueryExpression>, String> sourceByContent = indexByContent(serviceProvider, sourceExpressions);
		final Multimap<Wrapper<QueryExpression>, String> targetByContent = indexByContent(serviceProvider, targetExpressions);
	
		final Set<Wrapper<QueryExpression>> removedFromSource = Sets.difference(sourceByContent.keySet(), targetByContent.keySet());
		removedFromSource.stream()
			.flatMap(w -> sourceByContent.get(w).stream())
			.map(id -> sourceById.get(id))
			.forEachOrdered(removedExpressions::add);
			
		final Set<Wrapper<QueryExpression>> addedToTarget = Sets.difference(targetByContent.keySet(), sourceByContent.keySet());
		return addedToTarget.stream()
			.map(w -> w.get())
			.collect(Collectors.toList());
	}

	private static HashMultimap<Wrapper<QueryExpression>, String> indexByContent(
		final ServiceProvider serviceProvider, 
		final Collection<QueryExpression> sourceExpressions
	) {
		final EclParser parser = serviceProvider.service(EclParser.class);
		final EclRewriter rewriter = serviceProvider.service(EclRewriter.class);
		final EclSerializer serializer = serviceProvider.service(EclSerializer.class);
		
		return sourceExpressions.stream().collect(Multimaps.toMultimap(
			ex -> EXPRESSION_EQUIVALENCE.wrap(removeLabels(parser, rewriter, serializer, ex)), 
			ex -> ex.getId(), 
			() -> HashMultimap.create()));
	}

	private static QueryExpression removeLabels(
		final EclParser parser, 
		final EclRewriter rewriter, 
		final EclSerializer serializer, 
		final QueryExpression ex
	) {
		try {
			
			final ExpressionConstraint constraint = parser.parse(ex.getQuery());
			// Terminology-dependent normalization stage: remove labels, make ID sets unique, etc.
			final ExpressionConstraint rewrittenConstraint = rewriter.rewrite(constraint);
			return new QueryExpression(ex.getId(), serializer.serialize(rewrittenConstraint), ex.isPinned());
			
		} catch (final SyntaxException e) {
			// We could not parse this expression
			return ex;
		}
	}

	public QueryExpressionDiff(List<QueryExpression> addToInclusion, List<QueryExpression> addToExclusion, List<QueryExpression> remove) {
		this.addToInclusion = addToInclusion;
		this.addToExclusion = addToExclusion;
		this.remove = remove;
	}

	public List<QueryExpression> getAddToInclusion() {
		return addToInclusion;
	}
	
	public List<QueryExpression> getAddToExclusion() {
		return addToExclusion;
	}
	
	public List<QueryExpression> getRemove() {
		return remove;
	}
}
