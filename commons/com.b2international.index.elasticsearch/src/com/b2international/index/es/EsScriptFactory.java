/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.es;

import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.script.ScriptType;

import com.b2international.index.ScriptExpression;
import com.b2international.index.mapping.DocumentMapping;

/**
 * @since 7.18.3
 */
public enum EsScriptFactory {
	INSTANCE;
	
	private static final String SCRIPTING_LANGUAGE = "painless";

	/**
	 * Converts the {@link #getScript()} and {@link #getParams()} to an inline Elasticsearch script. If the specified {@link #getScript()} is a named
	 * script it will be converted to the actual raw script value.
	 * 
	 * @param mapping - the mapping to use for named scripts
	 * @return the Elasticsearch script to send to with queries, update, scoring, etc.
	 */
	public org.elasticsearch.script.Script toEsScript(ScriptExpression expression, DocumentMapping mapping) {
		String script = expression.getScript();

		// if this is a named script then get it from the current mapping
		if (mapping.getScript(script) != null) {
			script = mapping.getScript(script).script();
		}
		
		final Map<String, Object> params = expression.getParams();
		final Map<String, Object> convertedParams = params.entrySet()
			.stream()
			.map(e -> Map.entry(e.getKey(), ScriptExpression.convertScriptParam(e.getValue())))
			.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		
		return new org.elasticsearch.script.Script(ScriptType.INLINE, SCRIPTING_LANGUAGE, script, convertedParams);
	}
}
