/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.conceptmap;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Concept map unmapped backbone element
 * <br> When no match in mappings
 * @since 6.10
 */
public class UnMapped {

	@Valid
	@JsonProperty
	private final Uri url;

	@JsonProperty
	private final String display;

	@Valid
	@JsonProperty
	private final Code code;

	@Valid
	@NotNull
	@JsonProperty
	private final Code mode;


	UnMapped(Uri url, String display, Code code, Code mode) {
		this.url = url;
		this.display = display;
		this.code = code;
		this.mode = mode;
	}


	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ValidatingBuilder<UnMapped> {


		private Uri url;

		private String display;

		private Code code;

		private Code mode;


		public Builder url(final Uri url) {
			this.url = url;
			return this;
		}

		public Builder url(final String urlString) {
			this.url = new Uri(urlString);
			return this;
		}
		
		public Builder display(final String display) {
			this.display = display;
			return this;
		}

		public Builder code(final Code code) {
			this.code = code;
			return this;
		}
		
		public Builder code(final String codeString) {
			this.code = new Code(codeString);
			return this;
		}

		public Builder mode(final Code mode) {
			this.mode = mode;
			return this;
		}
		
		public Builder mode(final String modeString) {
			this.mode = new Code(modeString);
			return this;
		}

		@Override
		protected UnMapped doBuild() {

			return new UnMapped(url, display, code, mode);
		}

	}

}
