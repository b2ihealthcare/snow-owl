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

import java.util.List;

import com.b2international.snowowl.fhir.core.model.r5.ModifierElement;
import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.datatype.Extension;
import com.b2international.snowowl.fhir.core.model.r5.datatype.Narrative;

/**
 * A domain resource is a resource that:
 * 
 * <ul>
 * <li>has a human-readable XHTML representation of the content of the resource
 * <li>can contain additional related resources inside the resource
 * <li>can have additional extensions and modifierExtensions as well as the defined data
 * </ul>
 * 
 * @see <a href="https://hl7.org/fhir/R5/domainresource.html#resource">2.1.27.6.3 Resource Content</a>
 * @since 9.0
 */
public abstract class DomainResource extends Resource {

	/** Text summary of the resource, for human interpretation */
	private Narrative text;

	/** Contained, inline Resources */
	private List<Resource> contained;

	/** Additional content defined by implementations */
	private List<Extension> extension;

	/** Extensions that cannot be ignored */
	@ModifierElement
	@Summary
	private List<Extension> modifierExtension;

	public Narrative getText() {
		return text;
	}

	public void setText(Narrative text) {
		this.text = text;
	}

	public List<Resource> getContained() {
		return contained;
	}

	public void setContained(List<Resource> contained) {
		this.contained = contained;
	}

	public List<Extension> getExtension() {
		return extension;
	}

	public void setExtension(List<Extension> extension) {
		this.extension = extension;
	}

	public List<Extension> getModifierExtension() {
		return modifierExtension;
	}

	public void setModifierExtension(List<Extension> modifierExtension) {
		this.modifierExtension = modifierExtension;
	}
}
