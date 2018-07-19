/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.terminology;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

/**
 * @since 7.0
 */
public enum TerminologyRegistry {

	INSTANCE;

	private final Map<String, Terminology> terminologies = newHashMap();
	
	public void registerTerminology(Terminology terminology) {
		Terminology prev = terminologies.put(terminology.getId(), terminology);
		if (prev != null) {
			throw new IllegalArgumentException(String.format("A terminology is already registered with id '%s'", terminology.getId()));
		}
	}
	
	public Terminology getTerminology(String terminologyId) {
		checkArgument(terminologies.containsKey(terminologyId), "Missing terminology '%s'", terminologyId);
		return terminologies.get(terminologyId);
	}
	
}
