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
package com.b2international.snowowl.fhir.core.model.r5.base;

import java.util.List;

import com.b2international.snowowl.fhir.core.model.r5.ModifierElement;
import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.datatype.Extension;

/**
 * The base definition for complex elements defined as part of a resource
 * definition - that is, elements that have children that are defined in the
 * resource. {@link DataType} elements do not use this type.
 * 
 * @see <a href="https://hl7.org/fhir/R5/types.html#BackboneElement">2.1.27.0.3 BackboneElement</a>
 * @since 9.0
 */
public abstract class BackboneElement extends Element {

	/** Extensions that cannot be ignored even if unrecognized */
	@ModifierElement
	@Summary
	private List<Extension> modifierExtension;

	public List<Extension> getModifierExtension() {
		return modifierExtension;
	}

	public void setModifierExtension(List<Extension> modifierExtension) {
		this.modifierExtension = modifierExtension;
	}
}
