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
package com.b2international.index;

import com.b2international.index.query.Expression;
import com.google.common.base.Function;

/**
 * @since 5.0
 */
public final class BulkUpdate<T extends WithId> {
	
	private final Class<? extends T> type;
	private final Expression filter;
	private final Function<T, T> update;
	
	public BulkUpdate(Class<? extends T> type, Expression filter, Function<T, T> func) {
		this.type = type;
		this.filter = filter;
		this.update = func;
	}
	
	public Class<? extends T> getType() {
		return type;
	}
	
	public Expression getFilter() {
		return filter;
	}
	
	public Function<T, T> getUpdate() {
		return update;
	}

}
