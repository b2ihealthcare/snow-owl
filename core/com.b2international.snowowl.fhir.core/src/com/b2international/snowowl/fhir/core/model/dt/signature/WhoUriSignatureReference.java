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
package com.b2international.snowowl.fhir.core.model.dt.signature;

import com.b2international.snowowl.fhir.core.model.dt.Uri;

/**
 * FHIR URI Signature reference
 * 
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#signature">FHIR:Data Types:Signature</a>
 * @since 6.6
 */
public class WhoUriSignatureReference extends SignatureReference<Uri> {
	
	protected WhoUriSignatureReference(final Uri value) {
		super(value);
	}

	@Override
	public String getType() {
		return "whoUri"; //$NON-NLS-N$
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends SignatureReference.Builder<Builder, WhoUriSignatureReference, Uri> {
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected WhoUriSignatureReference doBuild() {
			return new WhoUriSignatureReference(value);
		}
	}

}
