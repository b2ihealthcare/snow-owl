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

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.search.Summary;

/**
 * FHIR Signature complex datatype
 * 
 * A Signature holds an electronic representation of a signature and its supporting context in a FHIR accessible form. 
 *
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#signature">FHIR:Data Types:Signature</a>
 * @since 6.6
 */
public class Signature extends Element {
	
	protected Signature(String id, Collection<Extension> extensions) {
		super(id, extensions);
	}

	@Summary
	@Valid
	@NotEmpty
	private Collection<Coding> type;
	
	@Summary
	@Valid
	@NotNull
	private Instant when;
	
	@SuppressWarnings("rawtypes")
	@Summary
	@Valid
	@NotNull
	private SignatureReference who;
	
	@Summary
	@Valid
	@SuppressWarnings("rawtypes")
	private SignatureReference onBehalfOf;
	
	@Summary
	@Valid
	private Code contentType;
	
	@Valid
	private Byte[] blob;
	

}
