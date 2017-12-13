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
package com.b2international.scripting.api;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.ServiceLoader;

import com.google.common.collect.Maps;

/**
 * @since 6.1
 */
public interface ScriptEngine {
	
	enum Registry {
		
		INSTANCE;
		
		
		private final Map<String, ScriptEngine> engines;
		
		private Registry() {
			engines = Maps.uniqueIndex(ServiceLoader.load(ScriptEngine.class, getClass().getClassLoader()), ScriptEngine::getExtension);
		}
		
		public ScriptEngine getEngine(String extension) {
			checkArgument(engines.containsKey(extension), "Missing script engine '%s'.", extension);
			return engines.get(extension);
		}
		
	}
	
	<T> T run(ClassLoader ctx, String script, Map<String, Object> arguments);
	
	String getExtension();

	static <T> T run(String extension, ClassLoader classLoader, String script, Map<String, Object> arguments) {
		return Registry.INSTANCE.getEngine(extension).run(classLoader, script, arguments);
	}
	
}
