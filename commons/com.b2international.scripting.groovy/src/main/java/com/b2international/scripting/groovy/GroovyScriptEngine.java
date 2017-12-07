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
package com.b2international.scripting.groovy;

import java.util.Map;

import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import com.b2international.scripting.api.ScriptEngine;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * @since 6.1
 */
public final class GroovyScriptEngine implements ScriptEngine {
	
	private final LoadingCache<ClassLoader, GroovyShell> shells = CacheBuilder.newBuilder().build(new CacheLoader<ClassLoader, GroovyShell>() {
		@Override
		public GroovyShell load(ClassLoader ctx) throws Exception {
			return new GroovyShell(ctx);
		}
	});
	private final LoadingCache<Pair<ClassLoader, String>, Class<? extends Script>> scriptCache = CacheBuilder.newBuilder().build(new CacheLoader<Pair<ClassLoader, String>, Class<? extends Script>>() {
		@Override
		public Class<? extends Script> load(Pair<ClassLoader, String> ctxAndScript) throws Exception {
			return shells.getUnchecked(ctxAndScript.getFirst()).getClassLoader().parseClass(ctxAndScript.getSecond());
		}
	});
	
	@Override
	public <T> T run(ClassLoader ctx, String script, Map<String, Object> params) {
		final Script compiledScript = compile(ctx, script);
		final Binding binding = new Binding(params);
		compiledScript.setBinding(binding);
		return (T) compiledScript.run();
	}

	@Override
	public String getExtension() {
		return "groovy";
	}
	
	private Script compile(ClassLoader ctx, String script) {
		final Class<? extends Script> scriptClass = scriptCache.getUnchecked(Tuples.pair(ctx, script));
		try {
			return scriptClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Couldn't instantiate groovy script", e);
		}
	}

}
