/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
		
	@JsonIgnore
	public static final ComponentURI UNSPECIFIED = ComponentURI.of(TerminologyRegistry.UNSPECIFIED, TerminologyRegistry.UNSPECIFIED_NUMBER_SHORT, "");
	
	private final CodeSystemURI codeSystemUri;
	private final short terminologyComponentId;
	private final String identifier;
	
	public CodeSystemURI codeSystemUri() {
		return codeSystemUri;
	}
	
	public String codeSystem() {
		return codeSystemUri.getCodeSystem();
	}
	
	public short terminologyComponentId() {
		return terminologyComponentId;
	}
	
	public String identifier() {
		return identifier;
	}

	@JsonIgnore
	public final boolean isUnspecified() {
		return TerminologyRegistry.UNSPECIFIED.equals(codeSystem());
	}
	
	public final ComponentIdentifier toComponentIdentifier() {
		return ComponentIdentifier.of(terminologyComponentId(), identifier());
	}

	private ComponentURI(CodeSystemURI codeSystemURI, short terminologyComponentId, String identifier) {
		checkNotNull(codeSystemURI, "CodeSystemURI argument should not be null.");
		checkArgument(terminologyComponentId >= TerminologyRegistry.UNSPECIFIED_NUMBER_SHORT, 
				"TerminologyComponentId should be either unspecified (-1) or greater than zero. Got: '%s'.", terminologyComponentId);
		checkArgument(TerminologyRegistry.UNSPECIFIED.equals(codeSystemURI.getCodeSystem()) || !Strings.isNullOrEmpty(identifier), "Identifier should not be null or empty.");
		this.codeSystemUri = codeSystemURI;
		this.terminologyComponentId = terminologyComponentId;
		this.identifier = Strings.nullToEmpty(identifier);
	}
	
	public static ComponentURI of(CodeSystemURI codeSystemURI, short terminologyComponentId, String identifier) {
		return getOrCache(new ComponentURI(codeSystemURI, terminologyComponentId, identifier));
	}
	
	public static ComponentURI of(CodeSystemURI codeSystemURI, ComponentIdentifier componentIdentifier) {
		return of(codeSystemURI, componentIdentifier.getTerminologyComponentId(), componentIdentifier.getComponentId());
	}
	
	public static ComponentURI of(String codeSystemUri, short terminologyComponentId, String identifier) {
		return of(new CodeSystemURI(codeSystemUri), terminologyComponentId, identifier);
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
		checkArgument(parts.size() >= 3, "A component uri consists of three parts (codeSystemURI/componentType/componentId). Arg was: %s", uri);
		int terminologyComponentTypeIndex = parts.size()-2;
		int componentIdIndex = parts.size()-1;
		CodeSystemURI codeSystemURI = new CodeSystemURI(SLASH_JOINER.join(parts.subList(0, terminologyComponentTypeIndex)));
		Short terminologyComponentId = Short.valueOf(parts.get(terminologyComponentTypeIndex));
		String componentId = parts.get(componentIdIndex);
		return new ComponentURI(codeSystemURI, terminologyComponentId, componentId);
	}

	@JsonValue
	@Override
	public String toString() {
		return SLASH_JOINER.join(codeSystem(), terminologyComponentId(), identifier());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(codeSystemUri, terminologyComponentId, identifier);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ComponentURI other = (ComponentURI) obj;
		return Objects.equals(codeSystemUri, other.codeSystemUri)
				&& terminologyComponentId == other.terminologyComponentId
				&& Objects.equals(identifier, other.identifier);
	}

}
