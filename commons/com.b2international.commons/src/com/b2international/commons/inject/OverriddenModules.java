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
package com.b2international.commons.inject;

import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * Utility without additional dependencies to override modules.
 * 
 * @see org.eclipse.xtext.util.Modules2
 * 
 * @since 2.8
 */
public class OverriddenModules {

	public static Module mixin(Module... m) {
		if (m.length == 0)
			return null;
		Module current = m[0];
		for (int i = 1; i < m.length; i++) {
			current = Modules.override(current).with(m[i]);
		}
		return current;
	}

}