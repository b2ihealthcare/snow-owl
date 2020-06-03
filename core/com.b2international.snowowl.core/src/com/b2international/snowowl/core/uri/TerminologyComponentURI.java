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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

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
import com.google.common.collect.ImmutableList;

/**
 * @since 7.7
 */
@JsonDeserialize(using = TerminologyComponentURI.ComponentURIDeserializer.class)
@Value.Immutable(intern = true, builder = false)
@Value.Style(allParameters = true, visibility = ImplementationVisibility.PACKAGE)
public abstract class TerminologyComponentURI implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final class ComponentURIDeserializer extends JsonDeserializer<TerminologyComponentURI> {
		@Override
		public TerminologyComponentURI deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
			return of(parser.getValueAsString());
		}
	}
	
	public static final class ComponentURIKeyDeserializer extends KeyDeserializer {
		
		@Override
		public Object deserializeKey(String key, DeserializationContext context) throws IOException, JsonProcessingException {
			return of(key);
		}
		
	}
	
	private static final Splitter SLASH_SPLITTER = Splitter.on('/');
	private static final Joiner SLASH_JOINER = Joiner.on('/');
	
	@JsonIgnore
	public static final TerminologyComponentURI UNKNOWN = TerminologyComponentURI.of("unknown", "unknown");
	
	@JsonIgnore
	public static final TerminologyComponentURI UNSPECIFIED = TerminologyComponentURI.of(TerminologyRegistry.UNSPECIFIED, "");
		
	public abstract String terminologyComponentId();
	
	public abstract String secondaryCodeSystem();
	
	@JsonIgnore
	public final boolean isUnknown() {
		return UNKNOWN == this;
	}
	
	@JsonIgnore
	public final boolean hasSecondaryCodeSystem() {
		return !Strings.isNullOrEmpty(secondaryCodeSystem());
	}
	
	@JsonIgnore
	public final boolean isUnspecified() {
		return TerminologyRegistry.UNSPECIFIED.equals(secondaryCodeSystem());
	}
	
	@Value.Lazy
	public List<String> parts() {
		return ImmutableList.of(terminologyComponentId(), secondaryCodeSystem());
	}
	
	@JsonValue
	@Override
	public String toString() {
		return SLASH_JOINER.join(parts());
	}
	
	public abstract TerminologyComponentURI withSecondaryCodeSystem(String secondaryCodeSystem);
	
	public static TerminologyComponentURI of(String terminologyComponentId, String secondaryCodeSystem) {
		return ImmutableTerminologyComponentURI.of(terminologyComponentId, secondaryCodeSystem);
	}
	
	public static TerminologyComponentURI of(String uri) {
		if (Strings.isNullOrEmpty(uri)) {
			return TerminologyComponentURI.UNKNOWN;
		}
		final List<String> parts = SLASH_SPLITTER.splitToList(uri);
		checkArgument(parts.size() == 2 || parts.size() == 1, "A terminology component uri consists of one or two parts (terminologyComponentId/secondaryCodeSystem). Arg was: %s", uri);
		return of(parts.get(0), parts.size() == 2 ? parts.get(1) : "");
	}

}
