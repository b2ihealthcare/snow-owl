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
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.TermFilter;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.CodeSystemContentMode;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Bundle.Builder;
import com.b2international.snowowl.fhir.core.model.ResourceEntry;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.fasterxml.jackson.annotation.JsonProperty;
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
		CodeSystem.Fields.VERSION,
		CodeSystem.Fields.TEXT
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
		// apply proper field selection
		List<String> fields = Lists.newArrayList(fields());
		// if any fields defined for field selection, then make sure toolingId is part of the selection, so it is returned and will be available when needed
		if (!fields.isEmpty() && !fields.contains(ResourceDocument.Fields.TOOLING_ID)) {
			fields.add(ResourceDocument.Fields.TOOLING_ID);
		}
		
		// remove all fields that are not part of the current Code Code System model
		fields.removeAll(EXTERNAL_FHIR_CODESYSTEM_FIELDS);
		// replace publisher with internal owner field
		if (fields.contains(CodeSystem.Fields.PUBLISHER)) {
			fields.remove(CodeSystem.Fields.PUBLISHER);
			fields.add(ResourceDocument.Fields.OWNER);
		}
		
		// prepare filters
		final ExpressionBuilder codeSystemQuery = Expressions.builder();
		
		addIdFilter(codeSystemQuery, ResourceDocument.Expressions::ids); // resource and version doc has id field
		addFilter(codeSystemQuery, OptionKey.NAME, String.class, ResourceDocument.Expressions::ids); // apply _name filter to the id fields, we use the same value for both id and name
		
		if (containsKey(OptionKey.TITLE)) {
			codeSystemQuery.must(ResourceDocument.Expressions.defaultTitleDisjunctionQuery(TermFilter.defaultTermMatch(getString(OptionKey.TITLE))));
		}
		
		Hits<ResourceFragment> internalCodeSystems = context.service(RevisionSearcher.class)
				.search(Query.select(ResourceFragment.class)
				.from(ResourceDocument.class, VersionDocument.class)
				.fields(fields)
				.where(codeSystemQuery.build())
				.searchAfter(searchAfter())
				.limit(limit())
				.sortBy(querySortBy(context))
				.build());
		
		// extract resource IDs and fetch all related core Resources
			
		return prepareBundle()
				.entry(internalCodeSystems.stream().map(codeSystem -> toFhirCodeSystemEntry(context, codeSystem)).collect(Collectors.toList()))
//				.after(internalCodeSystems.getSearchAfter())
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

	private ResourceEntry toFhirCodeSystemEntry(RepositoryContext context, ResourceFragment fragment) {
		return ResourceEntry.builder().resource(toFhirCodeSystem(context, fragment)).build();
	}

	private CodeSystem toFhirCodeSystem(RepositoryContext context, ResourceFragment codeSystem) {
		CodeSystem.Builder entry = CodeSystem.builder()
				// mandatory fields
				.id(codeSystem.id)
				.status(PublicationStatus.UNKNOWN) // TODO support status on versions
				.meta(
					Meta.builder()
						// TODO lastUpdated placeholder value for now, compute based on whether this is a version of the resource or the NEXT/HEAD version of the resource
						.lastUpdated(Instant.builder().instant(java.time.Instant.now()).build())
					.build()
				)
				.content(CodeSystemContentMode.COMPLETE); // treat all CodeSystems complete by default, later we might add this field to the document, if needed
		
		// optional fields
		// we are using the ID of the resource as machine readable name
//		includeIfFieldSelected(CodeSystem.Fields.NAME, codeSystem::getId, entry::name);
//		includeIfFieldSelected(CodeSystem.Fields.TITLE, codeSystem::getTitle, entry::title);
//		includeIfFieldSelected(CodeSystem.Fields.URL, codeSystem::getUrl, entry::url);
//		includeIfFieldSelected(CodeSystem.Fields.TEXT, () -> Narrative.builder().div("<div></div>").status(NarrativeStatus.EMPTY).build(), entry::text);
//		includeIfFieldSelected(CodeSystem.Fields.PUBLISHER, codeSystem::getOwner, entry::publisher);
//		includeIfFieldSelected(CodeSystem.Fields.COPYRIGHT, codeSystem::getCopyright, entry::copyright);
//		includeIfFieldSelected(CodeSystem.Fields.LANGUAGE, codeSystem::getLanguage, entry::language);
//		includeIfFieldSelected(CodeSystem.Fields.DESCRIPTION, codeSystem::getDescription, entry::description);
//		includeIfFieldSelected(CodeSystem.Fields.PURPOSE, codeSystem::getPurpose, entry::purpose);
		
		FhirCodeSystemResourceConverter converter = context.service(RepositoryManager.class)
			.get(codeSystem.toolingId)
			.optionalService(FhirCodeSystemResourceConverter.class)
			.orElse(FhirCodeSystemResourceConverter.DEFAULT);
		
//		includeIfFieldSelected(CodeSystem.Fields.COUNT, () -> converter.count(context, codeSystem.getResourceURI()), entry::count);
		
//		converter.expand(context, entry, codeSystem);
		
		return entry.build();
	}
	
	private <T> void includeIfFieldSelected(String field, Supplier<T> getter, Function<T, ?> setter) {
		if (CompareUtils.isEmpty(fields()) || fields().contains(field)) {
			setter.apply(getter.get());
		}
	}
	
	private static class ResourceFragment {
		
		@JsonProperty
		String resourceType;
		
		@JsonProperty
		String id;
		
		@JsonProperty
		String url;
		
		@JsonProperty
		String title;
		
		@JsonProperty
		String toolingId;
		
		@JsonProperty
		String branchPath;
		
	}
	
}
