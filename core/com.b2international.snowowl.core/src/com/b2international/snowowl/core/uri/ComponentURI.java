/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.uri;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

/**
 * @since 7.7
 */
@JsonDeserialize(using = ComponentURI.ComponentURIDeserializer.class)
public final class ComponentURI implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final class ComponentURIDeserializer extends JsonDeserializer<ComponentURI> {
		@Override
		public ComponentURI deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
			return of(parser.getValueAsString());
		}
	}
	
	public static final class ComponentURIKeyDeserializer extends KeyDeserializer {
		
		@Override
		public Object deserializeKey(String key, DeserializationContext context) throws IOException, JsonProcessingException {
			return of(key);
		}
		
	}
	
	/**
	 * Keeps weak references to every created {@link ComponentURI} in this JVM.
	 */
	private static final Interner<ComponentURI> COMPONENT_URI_INTERNER = Interners.newWeakInterner();
	
	protected static final Splitter SLASH_SPLITTER = Splitter.on('/');
	protected static final Joiner SLASH_JOINER = Joiner.on('/');
		
	public static final ComponentURI UNSPECIFIED = ComponentURI.of(CodeSystem.uri(TerminologyRegistry.UNSPECIFIED), TerminologyRegistry.UNKNOWN_COMPONENT_TYPE, "");
	
	private final ResourceURI resourceUri;
	private final String componentType;
	private final String identifier;
	
	public ResourceURI resourceUri() {
		return resourceUri;
	}
	
	public String resourceId() {
		return resourceUri.getResourceId();
	}
	
	public String componentType() {
		return componentType;
	}
	
	public String identifier() {
		return identifier;
	}

	@JsonIgnore
	public final boolean isUnspecified() {
		return CodeSystem.uri(TerminologyRegistry.UNSPECIFIED).equals(resourceUri());
	}
	
	public final ComponentIdentifier toComponentIdentifier() {
		return ComponentIdentifier.of(componentType(), identifier());
	}

	private ComponentURI(ResourceURI resourceUri, String componentType, String identifier) {
		checkNotNull(resourceUri, "ResourceURI argument should not be null.");
		checkArgument(!Strings.isNullOrEmpty(componentType), "Component Type should not be null or empty. Got: '%s'.", componentType);
		checkArgument(!componentType.contains("."), "Component Type should be a single word. Got: '%s'.", componentType);
		checkArgument(CodeSystem.uri(TerminologyRegistry.UNSPECIFIED).equals(resourceUri) || !Strings.isNullOrEmpty(identifier), "Identifier should not be null or empty.");
		this.resourceUri = resourceUri;
		this.componentType = componentType;
		this.identifier = Strings.nullToEmpty(identifier);
	}
	
	public static ComponentURI of(ResourceURI resourceURI, String componentType, String identifier) {
		return getOrCache(new ComponentURI(resourceURI, componentType, identifier));
	}
	
	public static ComponentURI of(ResourceURI resourceURI, ComponentIdentifier componentIdentifier) {
		return of(resourceURI, componentIdentifier.getComponentType(), componentIdentifier.getComponentId());
	}
	
	public static ComponentURI of(String resourceURI, String componentType, String identifier) {
		return of(new ResourceURI(resourceURI), componentType, identifier);
	}
	
	private static ComponentURI getOrCache(final ComponentURI componentURI) {
		return COMPONENT_URI_INTERNER.intern(componentURI);
	}
	
	public static boolean isValid(String uriString) {
		try {
			of(uriString);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static ComponentURI of(String uri) {
		if (Strings.isNullOrEmpty(uri)) {
			return ComponentURI.UNSPECIFIED;
		}
		final List<String> parts = SLASH_SPLITTER.splitToList(uri);
		checkArgument(parts.size() >= 4, "A component uri consists of at least four parts (resourceType/resourceId/componentType/componentId). Arg was: %s", uri);
		int terminologyComponentTypeIndex = parts.size() - 2;
		int componentIdIndex = parts.size() - 1;
		ResourceURI resourceURI = new ResourceURI(SLASH_JOINER.join(parts.subList(0, terminologyComponentTypeIndex)));
		String componentType = parts.get(terminologyComponentTypeIndex);
		String componentId = parts.get(componentIdIndex);
		return new ComponentURI(resourceURI, componentType, componentId);
	}
	
	public static ComponentURI unspecified(String identifier) {
		return of(CodeSystem.uri(TerminologyRegistry.UNSPECIFIED), TerminologyRegistry.UNKNOWN_COMPONENT_TYPE, identifier);
	}
	
	public static ComponentURI unspecified(String componentType, String identifier) {
		return of(CodeSystem.uri(TerminologyRegistry.UNSPECIFIED), componentType, identifier);
	}

	@JsonValue
	@Override
	public String toString() {
		return SLASH_JOINER.join(resourceUri(), componentType(), identifier());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(resourceUri, componentType, identifier);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ComponentURI other = (ComponentURI) obj;
		return Objects.equals(resourceUri, other.resourceUri)
				&& Objects.equals(componentType, other.componentType)
				&& Objects.equals(identifier, other.identifier);
	}

}
