/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.r5.resource;

import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.element.valueset.Compose;
import com.b2international.snowowl.fhir.core.model.r5.element.valueset.Expansion;
import com.b2international.snowowl.fhir.core.model.r5.element.valueset.Scope;

/**
 * Value sets have 2 aspects:
 * 
 * <ul>
 * <li><code>compose</code>: A definition of which codes are intended to be in 
 * the value set ("intension")
 * <li><code>expansion</code>: The list of codes that are actually in the value 
 * set under a given set of conditions ("extension")
 * </ul>
 * 
 * @see <a href="https://hl7.org/fhir/R5/valueset.html#resource">4.9.5 Resource Content</a>
 * @since 9.0
 */
public class ValueSet extends MetadataResource {

	/** Indicates whether or not any change to the content logical definition may occur. */
	@Summary
	private Boolean immutable;
	
	/** Logical definition of the value set */
	private Compose compose;
	
	/** Enumerated collection of codes that are members of the value set */
	private Expansion expansion;
	
	/** Criteria describing which concepts or codes should be included/excluded and why */
	private Scope scope;

	public Boolean getImmutable() {
		return immutable;
	}

	public void setImmutable(Boolean immutable) {
		this.immutable = immutable;
	}

	public Compose getCompose() {
		return compose;
	}

	public void setCompose(Compose compose) {
		this.compose = compose;
	}

	public Expansion getExpansion() {
		return expansion;
	}

	public void setExpansion(Expansion expansion) {
		this.expansion = expansion;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
}
