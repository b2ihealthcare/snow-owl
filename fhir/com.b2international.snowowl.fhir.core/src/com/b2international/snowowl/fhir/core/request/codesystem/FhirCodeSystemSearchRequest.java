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
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.TermFilter;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.fhir.core.codesystems.*;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.model.Bundle.Builder;
import com.b2international.snowowl.fhir.core.model.Meta;
import com.b2international.snowowl.fhir.core.model.ResourceResponseEntry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedCodeSystemRequestProperties;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedConceptProperty;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
		CodeSystem.Fields.TEXT
	);
	
	/**
	 * @since 8.0
	 */
	public enum OptionKey {
		URL,
		NAME,
		TITLE, 
		CONTENT,
		VERSION,
		
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
		// if any fields defined for field selection, then make sure toolingId, resourceType and id is part of the selection, so it is returned and will be available when needed
		if (!fields.isEmpty()) {
			if (!fields.contains(ResourceDocument.Fields.ID)) {
				fields.add(ResourceDocument.Fields.ID);
			}
			if (!fields.contains(ResourceDocument.Fields.RESOURCE_TYPE)) {
				fields.add(ResourceDocument.Fields.RESOURCE_TYPE);
			}
			if (!fields.contains(ResourceDocument.Fields.TOOLING_ID)) {
				fields.add(ResourceDocument.Fields.TOOLING_ID);
			}
			if (!fields.contains(ResourceDocument.Fields.CREATED_AT)) {
				fields.add(ResourceDocument.Fields.CREATED_AT);
			}
		}
		
		// remove all fields that are not part of the current Code Code System model
		fields.removeAll(EXTERNAL_FHIR_CODESYSTEM_FIELDS);
		// replace publisher with internal owner field
		if (fields.contains(CodeSystem.Fields.PUBLISHER)) {
			fields.remove(CodeSystem.Fields.PUBLISHER);
			fields.add(ResourceDocument.Fields.OWNER);
		}
		// replace identifier with internal oid field
		if (fields.contains(CodeSystem.Fields.IDENTIFIER)) {
			fields.remove(CodeSystem.Fields.IDENTIFIER);
			fields.add(ResourceDocument.Fields.OID);
		}
		
		// prepare filters
		final ExpressionBuilder codeSystemQuery = Expressions.builder()
				// CodeSystems and versions of CodeSystems
				.filter(ResourceDocument.Expressions.resourceType(com.b2international.snowowl.core.codesystem.CodeSystem.RESOURCE_TYPE)); 
		
		// resource and version doc has id field
		addIdFilter(codeSystemQuery, ResourceDocument.Expressions::ids); 
		// apply _name filter to the id fields, we use the same value for both id and name
		addFilter(codeSystemQuery, OptionKey.NAME, String.class, ResourceDocument.Expressions::ids); 
		addFilter(codeSystemQuery, OptionKey.URL, String.class, ResourceDocument.Expressions::urls);
		addFilter(codeSystemQuery, OptionKey.VERSION, String.class, VersionDocument.Expressions::versions);
		
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
		
		// in case of version fragments, extract their CodeSystem only information from the latest CodeSystem document (no need to represent older data there)
		fillCodeSystemDocumentOnlyProperties(context, internalCodeSystems, fields);
			
		return prepareBundle()
				.entry(internalCodeSystems.stream().map(codeSystem -> toFhirCodeSystemEntry(context, codeSystem)).collect(Collectors.toList()))
//				.after(internalCodeSystems.getSearchAfter())
				.total(internalCodeSystems.getTotal())
				.build();
	}

	private void fillCodeSystemDocumentOnlyProperties(RepositoryContext context, Hits<ResourceFragment> internalCodeSystems, List<String> fields) throws IOException {
		final Set<String> versionCodeSystems = internalCodeSystems.stream()
				.filter(fragment -> !CompareUtils.isEmpty(fragment.getVersion()))
				.map(fragment -> fragment.getResourceURI().getResourceId())
				.collect(Collectors.toSet());
		Map<String, ResourceFragment> internalCodeSystemsById = new HashMap<>(internalCodeSystems.getHits().size());
		internalCodeSystems.forEach(fragment -> {
			internalCodeSystemsById.put(fragment.getId(), fragment);
		});
		
		Set<String> missingCodeSystems = Sets.difference(versionCodeSystems, internalCodeSystemsById.keySet());
		if (!missingCodeSystems.isEmpty()) {
			context.service(RevisionSearcher.class)
				.search(Query.select(ResourceFragment.class)
				.from(ResourceDocument.class)
				.fields(fields)
				.where(ResourceDocument.Expressions.ids(missingCodeSystems))
				.limit(missingCodeSystems.size())
				.build())
				.forEach(missingFragment -> {
					internalCodeSystemsById.put(missingFragment.getId(), missingFragment);
				});
		}
		
		for (ResourceFragment versionFragment : internalCodeSystemsById.values()) {
			if (!CompareUtils.isEmpty(versionFragment.getVersion())) {
				ResourceFragment versionCodeSystem = internalCodeSystemsById.get(versionFragment.getResourceURI().getResourceId());
				versionFragment.status = versionCodeSystem.status;
				versionFragment.owner = versionCodeSystem.owner;
				versionFragment.copyright = versionCodeSystem.copyright;
				versionFragment.language = versionCodeSystem.language;
				versionFragment.description = versionCodeSystem.description;
				versionFragment.purpose = versionCodeSystem.purpose;
				versionFragment.oid = versionCodeSystem.oid;
			}
		}
	}
	
	private Builder prepareBundle() {
		return Bundle.builder("codesystems")
				.type(BundleType.SEARCHSET)
				.meta(Meta.builder()
						.addTag(CompareUtils.isEmpty(fields()) ? null : Coding.CODING_SUBSETTED)
						.lastUpdated(Instant.builder().instant(java.time.Instant.now()).build())
						.build());
	}

	private ResourceResponseEntry toFhirCodeSystemEntry(RepositoryContext context, ResourceFragment fragment) {
		return ResourceResponseEntry.builder().resource(toFhirCodeSystem(context, fragment)).build();
	}

	private CodeSystem toFhirCodeSystem(RepositoryContext context, ResourceFragment codeSystem) {
		CodeSystem.Builder entry = CodeSystem.builder()
				// mandatory fields
				.id(codeSystem.getId())
				.status(PublicationStatus.getByCodeValue(codeSystem.getStatus()))
				.meta(
					Meta.builder()
						// createdAt returns version creation time or latest update of the resource :gold:
						.lastUpdated(Instant.builder().instant(codeSystem.getCreatedAt()).build())
					.build()
				)
				// treat all CodeSystems complete by default, later we might add this field to the document, if needed
				.content(CodeSystemContentMode.COMPLETE)
				.toolingId(codeSystem.getToolingId()); 
		
		// optional fields
		// we are using the ID of the resource as machine readable name
		includeIfFieldSelected(CodeSystem.Fields.NAME, codeSystem::getId, entry::name);
		includeIfFieldSelected(CodeSystem.Fields.TITLE, codeSystem::getTitle, entry::title);
		includeIfFieldSelected(CodeSystem.Fields.URL, codeSystem::getUrl, entry::url);
		includeIfFieldSelected(CodeSystem.Fields.TEXT, () -> Narrative.builder().div("<div></div>").status(NarrativeStatus.EMPTY).build(), entry::text);
		includeIfFieldSelected(CodeSystem.Fields.VERSION, codeSystem::getVersion, entry::version);
//		includeIfFieldSelected(CodeSystem.Fields.IDENTIFIER, () -> {
//			if (!CompareUtils.isEmpty(codeSystem.getOid())) {
//				return Identifier.builder()
//						.use(IdentifierUse.OFFICIAL)
//						.system(codeSystem.getUrl())
//						.value(codeSystem.getOid())
//						.build();
//			} else {
//				return null;
//			}
//		}, entry::identifier);
		includeIfFieldSelected(CodeSystem.Fields.PUBLISHER, codeSystem::getOwner, entry::publisher);
		includeIfFieldSelected(CodeSystem.Fields.COPYRIGHT, codeSystem::getCopyright, entry::copyright);
		includeIfFieldSelected(CodeSystem.Fields.LANGUAGE, codeSystem::getLanguage, entry::language);
		includeIfFieldSelected(CodeSystem.Fields.DESCRIPTION, codeSystem::getDescription, entry::description);
		includeIfFieldSelected(CodeSystem.Fields.PURPOSE, codeSystem::getPurpose, entry::purpose);
		
		FhirCodeSystemResourceConverter converter = context.service(RepositoryManager.class)
				.get(codeSystem.getToolingId())
				.optionalService(FhirCodeSystemResourceConverter.class)
				.orElse(FhirCodeSystemResourceConverter.DEFAULT);
		
		includeIfFieldSelected(CodeSystem.Fields.COUNT, () -> converter.count(context, codeSystem.getResourceURI()), entry::count);
		includeIfFieldSelected(CodeSystem.Fields.CONCEPT, () -> converter.expandConcepts(context, codeSystem.getResourceURI(), locales()), entry::concepts);
		includeIfFieldSelected(CodeSystem.Fields.FILTER, () -> converter.expandFilters(context, codeSystem.getResourceURI(), locales()), entry::filters);
		includeIfFieldSelected(CodeSystem.Fields.PROPERTY, () -> converter.expandProperties(context, codeSystem.getResourceURI(), locales()), properties -> {
			properties.stream()
				.filter(p -> !(SupportedCodeSystemRequestProperties.class.isInstance(p)))
				.map(prop -> SupportedConceptProperty.builder(prop).build())
				.forEach(entry::addProperty);
		});
		
		return entry.build();
	}
	
	private <T> void includeIfFieldSelected(String field, Supplier<T> getter, Consumer<T> setter) {
		if (CompareUtils.isEmpty(fields()) || fields().contains(field)) {
			setter.accept(getter.get());
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
		
		@JsonProperty
		String version;
		
		@JsonProperty
		Long createdAt;
		
		// CodeSystem only fields, for Versions they got their values from the corresponding CodeSystem
		
		String status;
		String owner;
		String copyright;
		String language;
		String description;
		String purpose;
		String oid;
		
		public String getId() {
			return id;
		}
		
		public String getResourceType() {
			return resourceType;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getToolingId() {
			return toolingId;
		}
		
		public String getBranchPath() {
			return branchPath;
		}
		
		public String getVersion() {
			return version;
		}
		
		public Long getCreatedAt() {
			return createdAt;
		}
		
		public ResourceURI getResourceURI() {
			return ResourceURI.of(resourceType, id);
		}
		
		public String getStatus() {
			return status;
		}
		
		public String getOwner() {
			return owner;
		}
		
		public String getCopyright() {
			return copyright;
		}
		
		public String getLanguage() {
			return language;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getPurpose() {
			return purpose;
		}
		
		public String getOid() {
			return oid;
		}
		
	}
	
}
