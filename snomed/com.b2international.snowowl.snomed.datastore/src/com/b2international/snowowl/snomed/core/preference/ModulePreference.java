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
package com.b2international.snowowl.snomed.core.preference;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedConfiguration;

/**
 * WIP - Mainly client side SNOMED CT module preference provider, but currently it is used by the server as well.
 * 
 * @since 4.5
 */
public class ModulePreference {

	private ModulePreference() {
	}

	/**
	 * Returns a preference list of SNOMED CT module IDs. They can be used in order to get the currently available module identifier.
	 * 
	 * @return
	 */
	public static List<String> getModulePreference() {
		final List<String> preference = newArrayList();
		final String selectedModuleId = checkNotNull(getSnomedConfiguration().getModuleIds().getDefaultChildKey(), "No default module configured.");
		preference.add(selectedModuleId);
		// add fall back module ID
		// FIXME do we need hard-coded fallback or can we delegate this to a user preference???
		preference.add(Concepts.MODULE_SCT_CORE);
		return preference;
	}

	private static SnomedConfiguration getSnomedConfiguration() {
		return ApplicationContext.getInstance().getService(SnomedConfiguration.class);
	}

}
