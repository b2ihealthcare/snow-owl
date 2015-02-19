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
package com.b2international.snowowl.terminologyregistry.core.util;

import java.util.Map;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryClientService;
import com.google.common.collect.Maps;

/**
 * @since 2.7
 */
public class TerminologyComponentIdLookupHelper {

	private final Map<String, String> cache; // TODO: keep synchronized with terminology registry changes

	public TerminologyComponentIdLookupHelper() {
		cache = Maps.newHashMap();
	}
	
	public String lookupTerminologyComponentId(String codeSystemShortName) {
		if (cache.containsKey(codeSystemShortName)) {
			return cache.get(codeSystemShortName);
		} else {
			final TerminologyRegistryClientService service = ApplicationContext.getInstance().getService(TerminologyRegistryClientService.class);
			String terminologyComponentId = service.getTerminologyComponentIdByShortName(codeSystemShortName);
			cache.put(codeSystemShortName, terminologyComponentId);
			return terminologyComponentId;
		}
	} 
	
	public void dispose() {
		cache.clear();
	}

}