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

import com.b2international.snowowl.core.domain.IComponent;

/**
 * @since 7.0
 */
public enum TerminologyRegistry {

	INSTANCE;
	
	public static final String UNSPECIFIED = "UNSPECIFIED";
	public static final int UNSPECIFIED_NUMBER = -1;
	public static final short UNSPECIFIED_NUMBER_SHORT = -1;

	private final Map<String, Terminology> terminologies = newHashMap();
	private final Map<String, TerminologyComponent> terminologyComponentsById = newHashMap();
	private final Map<Short, TerminologyComponent> terminologyComponentsByShortId = newHashMap();
	private final Map<Class<?>, TerminologyComponent> terminologyComponentsByDocType = newHashMap();
	
	public void registerTerminology(Terminology terminology) {
		Terminology prev = terminologies.put(terminology.getId(), terminology);
		if (prev != null) {
			throw new IllegalArgumentException(String.format("A terminology is already registered with id '%s'", terminology.getId()));
		}
		for (Class<? extends IComponent> terminologyComponent : terminology.getTerminologyComponents()) {
			checkArgument(terminologyComponent.isAnnotationPresent(TerminologyComponent.class), "%s domain class must have a @TerminologyComponent annotation.", terminologyComponent.getSimpleName());
			TerminologyComponent tcAnnotation = terminologyComponent.getAnnotation(TerminologyComponent.class);
			TerminologyComponent prevAnnotation = terminologyComponentsById.put(tcAnnotation.id(), tcAnnotation);
			terminologyComponentsByShortId.put(tcAnnotation.shortId(), tcAnnotation);
			terminologyComponentsByDocType.put(tcAnnotation.docType(), tcAnnotation);
			if (prevAnnotation != null) {
				throw new IllegalArgumentException(String.format("A terminology component is already registered with id '%s'", tcAnnotation.id()));	
			}
		}
	}
	
	public Terminology getTerminology(String terminologyId) {
		checkArgument(terminologies.containsKey(terminologyId), "Missing terminology '%s'.", terminologyId);
		return terminologies.get(terminologyId);
	}

	public TerminologyComponent getTerminologyComponentByShortId(short shortId) {
		checkArgument(terminologyComponentsByShortId.containsKey(shortId), "Missing terminology component for short ID '%s'.", shortId);
		return terminologyComponentsByShortId.get(shortId);
	}
	
	public TerminologyComponent getTerminologyComponentById(String id) {
		checkArgument(terminologyComponentsById.containsKey(id), "Missing terminology component for ID '%s'.", id);
		return terminologyComponentsById.get(id);
	}

	public TerminologyComponent getTerminologyComponentByDocType(Class<?> docType) {
		checkArgument(terminologyComponentsByDocType.containsKey(docType), "Missing terminology component for document type '%s'.", docType);
		return terminologyComponentsByDocType.get(docType);
	}
	
}
