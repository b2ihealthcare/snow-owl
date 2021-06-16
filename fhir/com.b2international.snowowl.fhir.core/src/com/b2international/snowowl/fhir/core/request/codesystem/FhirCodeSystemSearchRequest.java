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
package com.b2international.snowowl.fhir.core.request.codesystem;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.codesystem.CodeSystems;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Bundle.Builder;
import com.b2international.snowowl.fhir.core.model.Entry;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.google.common.collect.Lists;

/**
 * @since 8.0
 */
final class FhirCodeSystemSearchRequest extends SearchResourceRequest<RepositoryContext, Bundle> {

	private static final long serialVersionUID = 1L;
	private static final Collection<?> EXTERNAL_FHIR_CODESYSTEM_FIELDS = Set.of(
		CodeSystem.Fields.NAME,
		CodeSystem.Fields.COUNT,
		CodeSystem.Fields.CONTENT,
		CodeSystem.Fields.CONCEPT,
		CodeSystem.Fields.FILTER,
		CodeSystem.Fields.PROPERTY,
		CodeSystem.Fields.DATE,
		CodeSystem.Fields.IDENTIFIER,
		CodeSystem.Fields.META,
		CodeSystem.Fields.VERSION
	);
	
	/**
	 * @since 8.0
	 */
	public enum OptionKey {
		NAME,
		TITLE, 
		CONTENT,
		
		LAST_UPDATED, 
	}
	
	@Override
	protected Bundle createEmptyResult(int limit) {
		return prepareBundle().total(0).build();
	}

	@Override
	protected Bundle doExecute(RepositoryContext context) throws IOException {
		// TODO if one of the given ID filters specify versions, then query both Code System and Version
		
		final Collection<String> idFilter;
		if (componentIds() != null) {
			// TODO if _name filter defined along with _id then raise an error or warning
			idFilter = componentIds();
		} else if (containsKey(OptionKey.NAME)) {
			idFilter = getCollection(OptionKey.NAME, String.class);
		} else {
			idFilter = null;
		}
		
		// apply proper field selection
		List<String> fields = Lists.newArrayList(fields());
		// remove all fields that are not part of the current Code Code System model
		fields.removeAll(EXTERNAL_FHIR_CODESYSTEM_FIELDS);
		// replace publisher with internal owner field
		if (fields.contains(CodeSystem.Fields.PUBLISHER)) {
			fields.remove(CodeSystem.Fields.PUBLISHER);
			fields.add(ResourceDocument.Fields.OWNER);
		}
		
		CodeSystems internalCodeSystems = CodeSystemRequests.prepareSearchCodeSystem()
				.filterByIds(idFilter)
//				.filterByIdsOrUrls(componentIds()) // XXX componentId in FHIR Search Request can be either logicalId or url so either of them can match, apply to the search
				.filterByTitle(getString(OptionKey.TITLE))
				.setLimit(limit())
				.setSearchAfter(searchAfter())
				.setExpand(expand())
				.setFields(fields)
				.setLocales(locales())
				.sortBy(sortBy())
				.build()
				.execute(context);
		
		return prepareBundle()
				.entry(internalCodeSystems.stream().map(codeSystem -> toFhirCodeSystemEntry(context, codeSystem)).collect(Collectors.toList()))
				.total(internalCodeSystems.getTotal())
				.build();
	}
	
	private Builder prepareBundle() {
		return Bundle.builder(UUID.randomUUID().toString())
				.type(BundleType.SEARCHSET)
				.meta(Meta.builder()
						.addTag(CompareUtils.isEmpty(fields()) ? null : Coding.CODING_SUBSETTED)
						.lastUpdated(Instant.builder().instant(java.time.Instant.now()).build())
						.build());
	}

	private Entry toFhirCodeSystemEntry(RepositoryContext context, com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
		return new Entry(null, toFhirCodeSystem(context, codeSystem));
	}

	private CodeSystem toFhirCodeSystem(RepositoryContext context, com.b2international.snowowl.core.codesystem.CodeSystem codeSystem) {
		CodeSystem.Builder entry = CodeSystem.builder()
				// mandatory fields
				.id(codeSystem.getId())
				.status(PublicationStatus.getByCodeValue(codeSystem.getStatus()))
				.meta(
					Meta.builder()
						// TODO lastUpdated placeholder value for now, compute based on whether this is a version of the resource or the NEXT/HEAD version of the resource
						.lastUpdated(Instant.builder().instant(java.time.Instant.now()).build())
					.build()
				)
				.content(CodeSystemContentMode.COMPLETE) // treat all CodeSystems complete by default, later we might add this field to the document, if needed
				
				// summary fields
				.url(codeSystem.getUrl())
				.publisher(codeSystem.getOwner())
				.name(codeSystem.getId()) // we are using the ID of the resource as machine readable name
				.title(codeSystem.getTitle());
		// TODO fill out Code System specific properties, if required use tooling specific extensions to fill all fields
		
		// optional fields
		includeIfFieldSelected(CodeSystem.Fields.COPYRIGHT, codeSystem::getCopyright, entry::copyright);
		includeIfFieldSelected(CodeSystem.Fields.LANGUAGE, codeSystem::getLanguage, entry::language);
		includeIfFieldSelected(CodeSystem.Fields.DESCRIPTION, codeSystem::getDescription, entry::description);
		includeIfFieldSelected(CodeSystem.Fields.PURPOSE, codeSystem::getPurpose, entry::purpose);
		// TODO compute count based on the current number of concepts
		includeIfFieldSelected(CodeSystem.Fields.COUNT, () -> 0, entry::count);
		
		return entry.build();
	}
	
	private <T> void includeIfFieldSelected(String field, Supplier<T> getter, Function<T, ?> setter) {
		if (CompareUtils.isEmpty(fields()) || fields().contains(field)) {
			setter.apply(getter.get());
		}
	}
	
}
