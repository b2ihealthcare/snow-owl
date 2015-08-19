/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.index.query;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import com.b2international.snowowl.datastore.index.field.ComponentIdLongField;
import com.b2international.snowowl.datastore.index.field.ComponentIdStringField;
import com.b2international.snowowl.datastore.index.field.ComponentTypeField;
import com.b2international.snowowl.datastore.index.field.IndexField;

/**
 * @since 4.3
 */
public class IndexQueries {

	public static Query queryComponentById(int componentType, String id) {
		return and(new ComponentTypeField(componentType), new ComponentIdStringField(id));
	}
	
	public static Query queryComponentByLongId(int componentType, long id) {
		return and(new ComponentTypeField(componentType), new ComponentIdLongField(id));
	}
	
	public static Query queryComponentByLongId(int componentType, String id) {
		return and(new ComponentTypeField(componentType), new ComponentIdLongField(id));
	}

	public static Query and(Query...queries) {
		return build(Occur.MUST, queries);
	}
	
	public static Query and(IndexField...fields) {
		final BooleanQuery query = new BooleanQuery(true);
		for (IndexField field : fields) {
			query.add(field.toQuery(), Occur.MUST);
		}
		return query;
	}

	public static Query or(Query...queries) {
		return build(Occur.SHOULD, queries);
	}

	private static Query build(final Occur occur, Query... queries) {
		final BooleanQuery query = new BooleanQuery(true);
		for (Query q : queries) {
			query.add(q, occur);
		}
		return query;
	}
	
}
