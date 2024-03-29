/*
 * Copyright 2011-2020 B2i Healthcare, https://b2ihealthcare.com
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
public final class BulkUpdate<T> extends BulkOperation<T> implements ScriptExpression {
	
	private final String script;
	private final Map<String, Object> params;
	
	public BulkUpdate(Class<? extends T> type, Expression filter, String script) {
		this(type, filter, script, Collections.emptyMap());
	}
	
	public BulkUpdate(Class<? extends T> type, Expression filter, String script, Map<String, Object> params) {
		super(type, filter);
		this.script = script;
		this.params = params;
	}
	
	@Override
	public String getScript() {
		return script;
	}

	@Override
	public Map<String, Object> getParams() {
		return params;
	}

	@Override
	public String toString() {
		return String.format("update with script '%s'", getScript());
	}
}
