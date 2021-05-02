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
import java.util.stream.Collectors;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
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
	public static final ResourceURI UNSPECIFIED_URI = ResourceURI.of("resource", "UNSPECIFIED");
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
			public String getToolingId() {
				return UNSPECIFIED;
			}
		});
		register(UNSPECIFIED, createUnspecifiedTerminologyComponent());
	}
	
	public Set<String> getTerminologies() {
		return ImmutableSet.copyOf(terminologies.keySet());
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
		TerminologyComponent prevAnnotation = terminologyComponentsById.put(terminologyComponent.id(), terminologyComponent);
		if (prevAnnotation != null) {
			throw new IllegalArgumentException(String.format("A terminology component is already registered with id '%s'", terminologyComponent.id()));	
		}
		terminologyComponentsByShortId.put(terminologyComponent.shortId(), terminologyComponent);
		terminologyIdByTerminologyComponentId.put(terminologyComponent.id(), terminologyId);
		terminologyComponentIdsByTerminology.put(terminologyId, terminologyComponent.id());
		if (!UNSPECIFIED.equals(terminologyId)) {
			// XXX This will inject the necessary values in the underlying document mapping caches
			DocumentMapping.getType(terminologyComponent.docType());
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
	
	public boolean hasTerminologyComponentByShortId(short shortId) {
		return terminologyComponentsByShortId.containsKey(shortId);
	}
	
	public TerminologyComponent getTerminologyComponentById(String id) {
		checkArgument(terminologyComponentsById.containsKey(id), "Missing terminology component for ID '%s'.", id);
		return terminologyComponentsById.get(id);
	}

	public Terminology getTerminologyByTerminologyComponentId(String terminologyComponentId) {
		checkArgument(terminologyIdByTerminologyComponentId.containsKey(terminologyComponentId), "No terminology has been registered for terminology component '%s'.", terminologyComponentId);
		return getTerminology(terminologyIdByTerminologyComponentId.get(terminologyComponentId));
	}
	
	public Collection<String> getTerminologyComponentIdsByTerminology(String terminologyId) {
		checkArgument(terminologyComponentIdsByTerminology.containsKey(terminologyId), "Missing terminology '%s'.", terminologyId);
		return ImmutableSet.copyOf(terminologyComponentIdsByTerminology.get(terminologyId));
	}
	
	/**
	 * Returns the primary terminology component ID (short) for a given terminology.
	 * It is usually the 'Concept' type.
	 * @param terminologyId
	 * @return the primary component short identifier 
	 */
	public short getConceptTerminologyComponentIdByTerminology(String terminologyId) {
		
		Collection<String> componentIds = getTerminologyComponentIdsByTerminology(terminologyId);
		
		if (componentIds.isEmpty()) {
			throw new SnowowlRuntimeException("Could not find registered terminology component IDs for " + terminologyId);
		}
		
		Set<TerminologyComponent> terminologyComponents = componentIds.stream()
				.map(componentId -> getTerminologyComponentById(componentId))
				.collect(Collectors.toSet());
		
		if (terminologyComponents.isEmpty()) {
			throw new SnowowlRuntimeException("Could not find registered terminology component for " + terminologyId);
		} else if (terminologyComponents.size() == 1) {
			//Return the the only registered component, even if it is not marked as primary
			return terminologyComponents.iterator().next().shortId();
		}
			
		Set<TerminologyComponent> primaryComponents = terminologyComponents.stream()
				.filter(t -> t.componentCategory() == ComponentCategory.CONCEPT)
				.collect(Collectors.toSet());
		
		if (primaryComponents.size() > 1) {
			throw new SnowowlRuntimeException("There is more than one primary terminology component registered for " + terminologyId);
		} else if (primaryComponents.isEmpty()) {
			// check for the first SET category before throwing an exception
			primaryComponents = terminologyComponents.stream()
					.filter(t -> t.componentCategory() == ComponentCategory.SET)
					.collect(Collectors.toSet());
			if (primaryComponents.isEmpty()) {
				throw new SnowowlRuntimeException("There is no primary terminology component (either CONCEPT or SET) registered for " + terminologyId);
			}
		}
		return primaryComponents.iterator().next().shortId();
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
			public boolean allowedAsMapTarget() {
				return false;
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
