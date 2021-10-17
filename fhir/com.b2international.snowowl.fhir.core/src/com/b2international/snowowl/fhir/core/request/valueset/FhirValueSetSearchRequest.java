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
package com.b2international.snowowl.fhir.core.request.valueset;

import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet.Builder;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequest;

/**
 * @since 8.0
 */
final class FhirValueSetSearchRequest extends FhirResourceSearchRequest<ValueSet.Builder, ValueSet> {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getResourceType() {
		return "valuesets";
	}

	@Override
	protected ValueSet.Builder createResourceBuilder() {
		return ValueSet.builder();
	}
	
	@Override
	protected void expandResourceSpecificFields(RepositoryContext context, Builder entry, ResourceFragment resource) {
		includeIfFieldSelected(CodeSystem.Fields.COPYRIGHT, resource::getCopyright, entry::copyright);
		
		FhirValueSetResourceConverter converter = context.service(RepositoryManager.class)
				.get(resource.getToolingId())
				.optionalService(FhirValueSetResourceConverter.class)
				.orElse(FhirValueSetResourceConverter.DEFAULT);
		
		includeIfFieldSelected(ValueSet.Fields.COMPOSE, () -> converter.expandCompose(context, resource.getResourceURI()), entry::compose);
	}
	
}
