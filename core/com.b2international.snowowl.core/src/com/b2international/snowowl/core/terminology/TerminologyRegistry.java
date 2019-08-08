/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.domain.IComponent;
import com.google.common.collect.Multimap;

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
	private final Map<String, String> terminologyIdByTerminologyComponentId = newHashMap(); 
	
	private TerminologyRegistry() {
		registerTerminology(new Terminology() {
			@Override
			public List<Class<? extends IComponent>> getTerminologyComponents() {
				return Collections.emptyList();
			}
			
			@Override
			public String getName() {
				return UNSPECIFIED;
			}
			
			@Override
			public String getId() {
				return UNSPECIFIED;
			}
		});
		register(createUnspecifiedTerminologyComponent());
	}
	
	public void registerTerminology(Terminology terminology) {
		Terminology prev = terminologies.put(terminology.getId(), terminology);
		if (prev != null) {
			throw new IllegalArgumentException(String.format("A terminology is already registered with id '%s'", terminology.getId()));
		}
		for (Class<? extends IComponent> terminologyComponentType : terminology.getTerminologyComponents()) {
			checkArgument(terminologyComponentType.isAnnotationPresent(TerminologyComponent.class), "%s domain class must have a @TerminologyComponent annotation.", terminologyComponentType.getSimpleName());
			TerminologyComponent terminologyComponent = terminologyComponentType.getAnnotation(TerminologyComponent.class);
			register(terminologyComponent);
			terminologyIdByTerminologyComponentId.put(terminology.getId(), terminologyComponent.id());
		}
	}

	private void register(TerminologyComponent tcAnnotation) {
		TerminologyComponent prevAnnotation = terminologyComponentsById.put(tcAnnotation.id(), tcAnnotation);
		if (prevAnnotation != null) {
			throw new IllegalArgumentException(String.format("A terminology component is already registered with id '%s'", tcAnnotation.id()));	
		}
		terminologyComponentsByShortId.put(tcAnnotation.shortId(), tcAnnotation);
		if (tcAnnotation.docType() != null && !terminologyComponentsByDocType.containsKey(tcAnnotation.docType())) {
			terminologyComponentsByDocType.put(tcAnnotation.docType(), tcAnnotation);
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
	
	public Terminology getTerminologyByTerminologyComponentId(String terminologyComponentId) {
		checkArgument(terminologyIdByTerminologyComponentId.containsKey(terminologyComponentId), "Missing terminology component for ID '%s'.", terminologyComponentId);
		return getTerminology(terminologyIdByTerminologyComponentId.get(terminologyComponentId));
	}
	
	private static TerminologyComponent createUnspecifiedTerminologyComponent() {
		return new TerminologyComponent() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return TerminologyComponent.class;
			}
			
			@Override
			public String[] supportedRefSetTypes() {
				return new String[] {};
			}
			
			@Override
			public String[] supportedMapTargetTypes() {
				return new String[] {};
			}
			
			@Override
			public short shortId() {
				return UNSPECIFIED_NUMBER_SHORT;
			}
			
			@Override
			public String name() {
				return UNSPECIFIED;
			}
			
			@Override
			public String id() {
				return UNSPECIFIED;
			}
			
			@Override
			public Class<? extends Revision> docType() {
				return null;
			}
			
			@Override
			public ComponentCategory componentCategory() {
				return ComponentCategory.UNKNOWN;
			}
		};
	}
	
}
