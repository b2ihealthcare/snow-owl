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

import javax.validation.Valid;

import com.b2international.snowowl.fhir.core.codesystems.MessageTransport;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Lists;

/**
 * FHIR Capability statement Endpoint backbone definition.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Endpoint.Builder.class)
public class Endpoint {
	
	@Mandatory
	@Valid
	@JsonProperty
	private final Coding protocol;
	
	@Mandatory
	@Valid
	@JsonProperty
	private final Uri address;
	
	Endpoint(final Coding protocol, final Uri address) {
		this.protocol = protocol;
		this.address = address;
	}
	
	public Coding getProtocol() {
		return protocol;
	}
	
	public Uri getAddress() {
		return address;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends ValidatingBuilder<Endpoint> {
		
		private Coding protocol;
		private Uri address;
		
		@JsonProperty
		public Builder protocol(final Coding protocol) {
			this.protocol = protocol;
			return this;
		}
		
		@JsonIgnore
		public Builder protocol(MessageTransport transport, String version) {
			
			Coding transportCoding = Coding.builder()
							.code(transport.getCode())
							.display(transport.getDisplayName())
							.system(transport.getCodeSystemUri())
							.version(version)
							.build();
			
			this.protocol = transportCoding;
			return this;
		}

		public Builder address(final String address) {
			this.address = new Uri(address);
			return this;
		}
		
		public Builder address(final Uri address) {
			this.address = address;
			return this;
		}
		
		@Override
		protected Endpoint doBuild() {
			return new Endpoint(protocol, address);
		}
	}

}
