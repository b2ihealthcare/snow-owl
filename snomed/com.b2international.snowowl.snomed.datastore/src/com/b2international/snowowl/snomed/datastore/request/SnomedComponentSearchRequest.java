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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.collections.longs.LongSet;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.IllegalQueryParameterException;
import com.b2international.snowowl.snomed.datastore.escg.ConceptIdQueryEvaluator2;
import com.b2international.snowowl.snomed.datastore.escg.EscgRewriter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.dsl.query.RValue;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.google.common.base.Function;

/**
 * @since 5.3
 */
public abstract class SnomedComponentSearchRequest<R> extends SnomedSearchRequest<R> {
	
	enum OptionKey {
		
		ACTIVE_MEMBER_OF
		
	}
	
	protected final void addActiveMemberOfClause(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.ACTIVE_MEMBER_OF)) {
			final String refSetId = getString(OptionKey.ACTIVE_MEMBER_OF);
			
			final Expression referringRefSetExpression = SnomedComponentDocument.Expressions.referringRefSet(refSetId);
			final Expression referringMappingRefSetExpression = SnomedComponentDocument.Expressions.referringMappingRefSet(refSetId);
			
			final Expression expression = Expressions
					.builder()
					.should(referringRefSetExpression)
					.should(referringMappingRefSetExpression)
					.build();
				
			queryBuilder.must(expression);
		}
	}
	
	/**
	 * @deprecated
	 */
	protected final void addEscgFilter(BranchContext context, final ExpressionBuilder queryBuilder, Enum<?> key, Function<LongSet, Expression> expressionProvider) {
		if (containsKey(key)) {
			try {
				final String escg = getString(key);
				final RValue expression = context.service(EscgRewriter.class).parseRewrite(escg);
				final LongSet conceptIds = new ConceptIdQueryEvaluator2(context.service(RevisionSearcher.class)).evaluate(expression);
				final Expression conceptFilter = expressionProvider.apply(conceptIds);
				queryBuilder.must(conceptFilter);
			} catch (SyntaxErrorException e) {
				throw new IllegalQueryParameterException(e.getMessage());
			}
		}
	}

}
