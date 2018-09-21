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
package com.b2international.index;

import java.util.Collections;
import java.util.Map;

import com.b2international.index.query.Expression;

/**
 * @since 5.0
 */
public final class BulkUpdate<T> {
	
	private final Class<? extends T> type;
	private final Expression filter;
	private final String script;
	private final String idField;
	private final Map<String, Object> params;
	
	public BulkUpdate(Class<? extends T> type, Expression filter, String idField, String script) {
		this(type, filter, idField, script, Collections.emptyMap());
	}
	
	public BulkUpdate(Class<? extends T> type, Expression filter, String idField, String script, Map<String, Object> params) {
		this.type = type;
		this.filter = filter;
		this.idField = idField;
		this.script = script;
		this.params = params;
	}
	
	public Class<? extends T> getType() {
		return type;
	}
	
	public Expression getFilter() {
		return filter;
	}
	
	public String getScript() {
		return script;
	}

	public String getIdField() {
		return idField;
	}
	
	public Map<String, Object> getParams() {
		return params;
	}

}
