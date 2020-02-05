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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.domain.IComponent;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
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
	private final Map<String, String> terminologyIdByTerminologyComponentId = newHashMap();
	private final Multimap<String, String> terminologyComponentIdsByTerminology = HashMultimap.create();
	
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
			public String getId() {
				return UNSPECIFIED;
			}
		});
		register(createUnspecifiedTerminologyComponent());
	}
	
	public Set<String> getTerminologies() {
		return ImmutableSet.copyOf(terminologies.keySet());
	}
	
	public void register(Terminology terminology) {
		Terminology prev = terminologies.put(terminology.getId(), terminology);
		if (prev != null) {
			throw new IllegalArgumentException(String.format("A terminology is already registered with id '%s'", terminology.getId()));
		}
		for (Class<? extends IComponent> terminologyComponentType : terminology.getTerminologyComponents()) {
			TerminologyComponent terminologyComponent = Terminology.getAnnotation(terminologyComponentType);
			register(terminologyComponent);
			terminologyIdByTerminologyComponentId.put(terminologyComponent.id(), terminology.getId());
			terminologyComponentIdsByTerminology.put(terminology.getId(), terminologyComponent.id());
			// XXX This will inject the necessary values in the underlying document mapping caches 
			DocumentMapping.getType(terminologyComponent.docType());
		}
	}

	private void register(TerminologyComponent tcAnnotation) {
		TerminologyComponent prevAnnotation = terminologyComponentsById.put(tcAnnotation.id(), tcAnnotation);
		if (prevAnnotation != null) {
			throw new IllegalArgumentException(String.format("A terminology component is already registered with id '%s'", tcAnnotation.id()));	
		}
		terminologyComponentsByShortId.put(tcAnnotation.shortId(), tcAnnotation);
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

	public Terminology getTerminologyByTerminologyComponentId(String terminologyComponentId) {
		checkArgument(terminologyIdByTerminologyComponentId.containsKey(terminologyComponentId), "Missing terminology component for ID '%s'.", terminologyComponentId);
		return getTerminology(terminologyIdByTerminologyComponentId.get(terminologyComponentId));
	}
	
	public Collection<String> getTerminologyComponentIdsByTerminology(String terminologyId) {
		checkArgument(terminologyComponentIdsByTerminology.containsKey(terminologyId), "Missing terminology '%s'.", terminologyId);
		return ImmutableSet.copyOf(terminologyComponentIdsByTerminology.get(terminologyId));
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
