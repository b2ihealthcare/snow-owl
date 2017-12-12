/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument.Expressions.namespaces;

import java.util.Collection;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;

/**
 * @since 5.3
 */
public abstract class SnomedComponentSearchRequest<R, D extends SnomedComponentDocument> extends SnomedSearchRequest<R, D> {
	
	enum OptionKey {
		
		/**
		 * Filters component to be members of the specified reference sets.
		 */
		ACTIVE_MEMBER_OF,
		
		/**
		 * Namespace part of the component ID to match (?)
		 */
		NAMESPACE
		
	}
	
	protected final void addActiveMemberOfClause(BranchContext context, ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.ACTIVE_MEMBER_OF)) {
			final Collection<String> refSetFilters = getCollection(OptionKey.ACTIVE_MEMBER_OF, String.class);
			
			final Collection<String> referringRefSetIds = evaluateEclFilter(context, refSetFilters);
			
			final Expression expression = Expressions
					.builder()
					.should(SnomedComponentDocument.Expressions.referringRefSets(referringRefSetIds))
					.should(SnomedComponentDocument.Expressions.referringMappingRefSets(referringRefSetIds))
					.build();
			queryBuilder.filter(expression);
		}
	}
	
	protected final void addNamespaceFilter(ExpressionBuilder queryBuilder) {
		if (containsKey(OptionKey.NAMESPACE)) {
			queryBuilder.filter(namespaces(getCollection(OptionKey.NAMESPACE, String.class)));
		}
	}

}
