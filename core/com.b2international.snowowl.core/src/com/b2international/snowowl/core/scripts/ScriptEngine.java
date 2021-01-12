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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.google.common.collect.Maps;

/**
 * @since 6.1
 */
public interface ScriptEngine {
	
	final class Registry {
		
		private final Map<String, ScriptEngine> engines;
		
		public Registry(ClassPathScanner scanner) {
			engines = Maps.uniqueIndex(scanner.getComponentsByInterface(ScriptEngine.class), ScriptEngine::getExtension);
		}
		
		public ScriptEngine getEngine(String extension) {
			checkArgument(engines.containsKey(extension), "Missing script engine '%s'.", extension);
			return engines.get(extension);
		}
		
		public <T> T run(String extension, ClassLoader classLoader, String script, Map<String, Object> arguments) {
			return run(extension, classLoader, new ScriptSource(IDs.sha1(script), script), arguments);
		}
		
		public <T> T run(String extension, ClassLoader classLoader, ScriptSource script, Map<String, Object> arguments) {
			return getEngine(extension).run(classLoader, script, arguments);
		}
		
	}
	
	<T> T run(ClassLoader ctx, ScriptSource script, Map<String, Object> arguments);
	
	String getExtension();

}
