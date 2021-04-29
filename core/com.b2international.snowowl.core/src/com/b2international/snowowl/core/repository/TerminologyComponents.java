/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.repository;

import static com.google.common.base.Preconditions.checkArgument;

import org.slf4j.Logger;

import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @since 7.2
 */
public final class TerminologyComponents {

	private final Logger log;
	private final BiMap<Short, Class<?>> terminologyComponentIdToDocuments = HashBiMap.create();
	
	public TerminologyComponents(Logger log) {
		this.log = log;
		this.terminologyComponentIdToDocuments.put(CodeSystemVersionEntry.TERMINOLOGY_COMPONENT_ID, CodeSystemVersionEntry.class);
	}
	
	public void add(TerminologyComponent component) {
		add(component.shortId(), component.docType());
	}
	
	public void add(short terminologyComponentId, Class<?> docType) {
		if (terminologyComponentIdToDocuments.containsValue(docType)) {
			log.warn("Preferring primary '{}' terminologyComponentId over '{}'", getTerminologyComponentId(docType), terminologyComponentId);
			return;
		}
		terminologyComponentIdToDocuments.put(terminologyComponentId, docType);
	}
	
	public Class<?> getDocType(short terminologyComponentId) {
		checkArgument(terminologyComponentIdToDocuments.containsKey(terminologyComponentId));
		return terminologyComponentIdToDocuments.get(terminologyComponentId);
	}
	
	public short getTerminologyComponentId(Class<?> docType) {
		checkArgument(terminologyComponentIdToDocuments.inverse().containsKey(docType));
		return terminologyComponentIdToDocuments.inverse().get(docType);
	}
	
}
