/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.*;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.domain.IComponent;

/**
 * @since 7.0
 */
public enum TerminologyRegistry {

	INSTANCE;
	
	public static final String UNSPECIFIED = "UNSPECIFIED";
	public static final int UNSPECIFIED_NUMBER = -1;
	public static final String UNKNOWN_COMPONENT_TYPE = "__UNKNOWN__";
	
	private final Map<String, Terminology> terminologies = newHashMap();
	private final TreeMap<String, TerminologyComponent> terminologyComponentsById = new TreeMap<>();
	
	private TerminologyRegistry() {
		register(new Terminology() {
			@Override
			public List<Class<? extends IComponent>> getTerminologyComponents() {
				return Collections.emptyList();
			}
			
			@Override
			public String getName() {
				return UNSPECIFIED;
			}
			
			@Override
			public String getToolingId() {
				return UNSPECIFIED;
			}
		});
		register(UNSPECIFIED, createUnspecifiedTerminologyComponent());
	}
	
	public Set<String> getTerminologies() {
		return Set.copyOf(terminologies.keySet());
	}
	
	public void register(Terminology terminology) {
		Terminology prev = terminologies.put(terminology.getToolingId(), terminology);
		if (prev != null) {
			throw new IllegalArgumentException(String.format("A terminology is already registered with id '%s'", terminology.getToolingId()));
		}
		for (Class<? extends IComponent> terminologyComponentType : terminology.getTerminologyComponents()) {
			TerminologyComponent terminologyComponent = Terminology.getAnnotation(terminologyComponentType);
			register(terminology.getToolingId(), terminologyComponent);
		}
	}

	public void register(String terminologyId, TerminologyComponent terminologyComponent) {
		String terminologyComponentId = getTerminologyComponentId(terminologyId, terminologyComponent);
		TerminologyComponent prevAnnotation = terminologyComponentsById.put(terminologyComponentId, terminologyComponent);
		if (prevAnnotation != null) {
			throw new IllegalArgumentException(String.format("A terminology component is already registered with id '%s'", terminologyComponentId));	
		}
		terminologyComponentsById.put(terminologyComponentId, terminologyComponent);
	}
	
	private String getTerminologyComponentId(String terminologyId, TerminologyComponent terminologyComponent) {
		checkArgument(terminologyComponent.docType() != null || !terminologyComponent.id().isBlank(), "Either docType() or id() field must be specified on TerminologyComponent annotations");
		if (!terminologyComponent.id().isBlank()) {
			return String.join(".", terminologyId, terminologyComponent.id());
		} else {
			return String.join(".", terminologyId, DocumentMapping.getDocType(terminologyComponent.docType()));
		}
	}

	public Terminology getTerminology(String terminologyId) {
		checkArgument(terminologies.containsKey(terminologyId), "Missing terminology '%s'.", terminologyId);
		return terminologies.get(terminologyId);
	}

	public TerminologyComponent getTerminologyComponentById(String terminologyComponentId) {
		checkArgument(terminologyComponentsById.containsKey(terminologyComponentId), "Missing terminology component for ID '%s'.", terminologyComponentId);
		return terminologyComponentsById.get(terminologyComponentId);
	}
	
	private static TerminologyComponent createUnspecifiedTerminologyComponent() {
		return new TerminologyComponent() {
			
			@Override
			public String id() {
				return UNKNOWN_COMPONENT_TYPE;
			}
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return TerminologyComponent.class;
			}
			
			@Override
			public String[] supportedRefSetTypes() {
				return new String[] {};
			}
			
			@Override
			public boolean allowedAsMapTarget() {
				return false;
			}
			
			@Override
			public String name() {
				return "Unspecified";
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
