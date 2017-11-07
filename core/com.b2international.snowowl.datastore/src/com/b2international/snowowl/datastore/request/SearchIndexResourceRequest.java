/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.request;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.SearchResourceRequest;

/**
 * @since 5.11
 */
public abstract class SearchIndexResourceRequest<C extends ServiceProvider, B> extends SearchResourceRequest<C, B> {

	/**
	 * Special field name for sorting based on the document's natural occurrence (document order). 
	 */
	public static final SortField DOC_ID = new SortField(DocumentMapping._ID, true);
	
	/**
	 * Special field name for sorting based on the document score (relevance).
	 */
	public static final SortField SCORE = new SortField(SortBy.FIELD_SCORE, false);
	
	protected final ExpressionBuilder addIdFilter(ExpressionBuilder queryBuilder, Function<Collection<String>, Expression> expressionFactory) {
		return applyIdFilter(queryBuilder, (qb, ids) -> qb.filter(expressionFactory.apply(ids)));
	}
	
	protected final SortBy sortBy() {
		if (containsKey(OptionKey.SORT_BY)) {
			List<SortField> fields = getList(OptionKey.SORT_BY, SortField.class);
			SortBy.Builder builder = SortBy.builder();
			for (SortField sortField : fields) {
				builder.add(sortField.getField(), sortField.isAscending() ? Order.ASC : Order.DESC);
			}
			return builder.build();
		} else {
			return SortBy.DOC_ID;
		}		
	}
	
	protected final <T> QueryBuilder<T> select(Class<T> select) {
		return Query.select(select).fields(fields());
	}
	
}
