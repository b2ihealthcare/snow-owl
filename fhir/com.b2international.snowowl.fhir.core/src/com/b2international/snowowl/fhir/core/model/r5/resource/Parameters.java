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

import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.element.parameters.Parameter;

/**
 * This resource is used to pass information into and back from an operation
 * (whether invoked directly from REST or within a messaging environment). It is
 * not persisted or allowed to be referenced by other resources.
 *
 * @see <a href="https://hl7.org/fhir/R5/parameters.html#resource">2.11.4 Resource Content</a>
 * @since 9.0
 */
public class Parameters extends DomainResource {

	/** Operation parameters */
	@Summary
	private List<Parameter> parameter;

	public List<Parameter> getParameter() {
		return parameter;
	}

	public void setParameter(List<Parameter> parameter) {
		this.parameter = parameter;
	}
}
