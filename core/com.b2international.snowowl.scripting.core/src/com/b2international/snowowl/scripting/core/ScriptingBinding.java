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
package com.b2international.snowowl.scripting.core;

import groovy.lang.Binding;

import java.util.Map;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.scripting.core.bindings.ScriptBinding;
import com.google.common.collect.Maps;

/**
 * Groovy specific binding, supporting injectable variables to the script instance.
 *
 */
public class ScriptingBinding extends Binding {

	/**
	 * Creates a new binding instance.
	 */
	public ScriptingBinding() {
		super(initBindingContext());
	}
	
	/*initialize the variables for the current binding context as a map and returns with it*/
	private static Map<?, ?> initBindingContext() {
		
		final Map<Object, Object> variables = Maps.newHashMap();
		
		for (final ScriptBinding binding : Extensions.getExtensions(ScriptBinding.SCRIPT_BINDING_EXTENSION_POINT_ID, ScriptBinding.class)) {
			final Object variable = binding.createVariable();
			variables.put(binding.getVariableName(), variable);
		}
		
		return variables;
		
	}
	
}