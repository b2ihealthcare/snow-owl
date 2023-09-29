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
import com.b2international.snowowl.fhir.core.model.r5.base.Base;
import com.b2international.snowowl.fhir.core.model.r5.datatype.IdType;
import com.b2international.snowowl.fhir.core.model.r5.datatype.Meta;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.CodeType;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.UriType;

/**
 * The base resource type.
 * <p>
 * The following optional elements and properties are defined for all resources:
 * 
 * <ul>
 * <li>An identity
 * <li>Meta data
 * <li>A base language
 * <li>A reference to "Implicit Rules"
 * </ul>
 * 
 * @see <a href="https://hl7.org/fhir/R5/resource.html#resource">2.1.27.5.3 Resource Content</a>
 * @since 9.0
 */
public abstract class Resource extends Base {

	/** Logical id of this artifact */
	@Summary
	private IdType id;

	/** Metadata about the resource */
	@Summary
	private Meta meta;

	/** A set of rules under which this content was created */
	@ModifierElement
	@Summary
	private List<UriType> implicitRules;
	
	/** Language of the resource content */
	private CodeType language;

	public IdType getId() {
		return id;
	}

	public void setId(IdType id) {
		this.id = id;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public List<UriType> getImplicitRules() {
		return implicitRules;
	}

	public void setImplicitRules(List<UriType> implicitRules) {
		this.implicitRules = implicitRules;
	}

	public CodeType getLanguage() {
		return language;
	}

	public void setLanguage(CodeType language) {
		this.language = language;
	}
}
