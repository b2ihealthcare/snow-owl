/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.tree;

import java.util.Map;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.collect.Multimap;

/**
 * @since 4.6
 */
public class TerminologyTree {

	private Map<String, SnomedConceptIndexEntry> items;
	private Multimap<String, String> subTypes;
	private Multimap<String, String> superTypes;
	
	public TerminologyTree(Map<String, SnomedConceptIndexEntry> items, Multimap<String, String> subTypes, Multimap<String, String> superTypes) {
		this.items = items;
		this.subTypes = subTypes;
		this.superTypes = superTypes;
	}

	public Map<String, SnomedConceptIndexEntry> getItems() {
		return items;
	}
	
	public Multimap<String, String> getSubTypes() {
		return subTypes;
	}
	
	public Multimap<String, String> getSuperTypes() {
		return superTypes;
	}
	
}
