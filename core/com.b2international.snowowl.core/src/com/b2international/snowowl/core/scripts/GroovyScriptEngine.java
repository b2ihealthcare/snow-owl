/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.scripts;

import java.util.Map;

import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import com.b2international.commons.CompositeClassLoader;
import com.b2international.snowowl.core.plugin.Component;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * @since 6.1
 */
@Component
public final class GroovyScriptEngine implements ScriptEngine {
	
	private final LoadingCache<ClassLoader, GroovyShell> shells = CacheBuilder.newBuilder().build(new CacheLoader<ClassLoader, GroovyShell>() {
		@Override
		public GroovyShell load(ClassLoader ctx) throws Exception {
			final CompositeClassLoader classLoader = new CompositeClassLoader();
			classLoader.add(ctx);
			classLoader.add(GroovyScriptEngine.class.getClassLoader());
			return new GroovyShell(classLoader);
		}
	});
	private final LoadingCache<Pair<ClassLoader, ScriptSource>, Class<? extends Script>> scriptCache = CacheBuilder.newBuilder().build(new CacheLoader<Pair<ClassLoader, ScriptSource>, Class<? extends Script>>() {
		@Override
		public Class<? extends Script> load(Pair<ClassLoader, ScriptSource> ctxAndScript) throws Exception {
			ScriptSource source = ctxAndScript.getSecond();
			return shells.getUnchecked(ctxAndScript.getFirst()).getClassLoader().parseClass(source.getScript(), source.getScriptName());
		}
	});
	
	@Override
	public <T> T run(ClassLoader ctx, ScriptSource script, Map<String, Object> params) {
		final Script compiledScript = compile(ctx, script);
		final Binding binding = new Binding(params);
		compiledScript.setBinding(binding);
		return (T) compiledScript.run();
	}

	@Override
	public String getExtension() {
		return "groovy";
	}
	
	private Script compile(ClassLoader ctx, ScriptSource script) {
		final Class<? extends Script> scriptClass = scriptCache.getUnchecked(Tuples.pair(ctx, script));
		try {
			return scriptClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Couldn't instantiate groovy script", e);
		}
	}

}
