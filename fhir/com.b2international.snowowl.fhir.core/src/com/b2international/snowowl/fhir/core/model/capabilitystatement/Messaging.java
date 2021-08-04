/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.capabilitystatement;

import java.util.Collection;

import javax.validation.Valid;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Lists;


/**
 * FHIR Capability statement Messaging backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Messaging.Builder.class)
public class Messaging {
	
	@Valid
	@JsonProperty("endpoint")
	private final Collection<Endpoint> endpoints;
	
	@JsonProperty
	private final Integer reliableCache;
	
	@JsonProperty
	private final String documentation;
	
	@Valid
	@JsonProperty("supportedMessage")
	private final Collection<SupportedMessage> supportedMessages;
	
	Messaging(final Collection<Endpoint> endpoints,
			final Integer reliableCache,
			final String documentation,
			final Collection<SupportedMessage> supportedMessages) {
		
		this.endpoints = endpoints;
		this.reliableCache = reliableCache;
		this.documentation = documentation;
		this.supportedMessages = supportedMessages;
	}
	
	public Collection<Endpoint> getEndpoints() {
		return endpoints;
	}
	
	public Integer getReliableCache() {
		return reliableCache;
	}
	
	public String getDocumentation() {
		return documentation;
	}
	
	public Collection<SupportedMessage> getSupportedMessages() {
		return supportedMessages;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Messaging> {
		
		private Collection<Endpoint> endpoints;
		private Integer reliableCache;
		private String documentation;
		private Collection<SupportedMessage> supportedMessages;
		
		@JsonProperty("endpoint")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder endpoints(Collection<Endpoint> endpoints) {
			this.endpoints = endpoints;
			return this;
		}
		
		public Builder addEndpoint(final Endpoint endpoint) {
			if (endpoints == null) {
				endpoints = Lists.newArrayList();
			}
			endpoints.add(endpoint);
			return this;
		}
		
		public Builder reliableCache(final Integer reliableCache) {
			this.reliableCache = reliableCache;
			return this;
		}

		public Builder documentation(final String documentation) {
			this.documentation = documentation;
			return this;
		}
		
		@JsonProperty("supportedMessage")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder supportedMessages(Collection<SupportedMessage> supportedMessages) {
			this.supportedMessages = supportedMessages;
			return this;
		}
		
		public Builder addSupportedMessage(final SupportedMessage supportedMessage) {
			
			if (supportedMessages == null) {
				supportedMessages = Lists.newArrayList();
			}
			supportedMessages.add(supportedMessage);
			return this;
		}
		
		
		@Override
		protected Messaging doBuild() {
			return new Messaging(endpoints, reliableCache, documentation, supportedMessages);
		}
	}
}
