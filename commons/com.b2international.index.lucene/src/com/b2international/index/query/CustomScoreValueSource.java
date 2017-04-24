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
package com.b2international.index.query;

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.valuesource.VectorValueSource;

import com.google.common.collect.ImmutableList;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * @since 5.10
 */
public class CustomScoreValueSource extends VectorValueSource {

	private final Map<String, ValueSource> sources;
	private final GroovyShell shell;
	private final String script;

	public CustomScoreValueSource(String script, Map<String, ValueSource> sources) {
		super(ImmutableList.copyOf(sources.values()));
		this.script = script;
		this.sources = sources;
		this.shell = new GroovyShell();
	}
	
	@Override
	public FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException {
		final Map<String, FunctionValues> fieldValues = newHashMap();
		for (String field : sources.keySet()) {
			fieldValues.put(field, sources.get(field).getValues(context, readerContext));
		}
		final Script compiledScript = shell.parse(script);
		return new FloatDocValues(this) {
			@Override
			public float floatVal(int doc) {
				final Map<String, Object> ctx = newHashMap();
				final Map<String, Object> _source = newHashMap();
				
				for (String field : sources.keySet()) {
					final FunctionValues value = fieldValues.get(field);
					if ("_score".equals(field)) {
						ctx.put(field, value.objectVal(doc));
					} else {
						_source.put(field, value.objectVal(doc));
					}
				}
				ctx.put("doc", _source);
				ctx.putAll(context);
				
				final Binding binding = new Binding(ctx);
				compiledScript.setBinding(binding);
				return (float) compiledScript.run();
			}
		};
	}

}
