/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.request;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionBranchPoint;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.search.TermFilter;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.*;
import com.b2international.snowowl.fhir.core.model.Bundle.Builder;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.ContactPoint;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @since 8.0
 */
public abstract class FhirResourceSearchRequest<B extends MetadataResource.Builder<B, T>, T extends FhirResource> extends SearchResourceRequest<RepositoryContext, Bundle> {

	private static final long serialVersionUID = 1L;

	private static final Set<String> EXTERNAL_FHIR_RESOURCE_FIELDS = Set.of(
		MetadataResource.Fields.NAME,
		MetadataResource.Fields.META,
		MetadataResource.Fields.TEXT
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
	protected final Bundle doExecute(RepositoryContext context) throws IOException {
		// apply proper field selection
		List<String> fields = replaceFieldsToLoad(fields());
		
		// prepare filters
		final ExpressionBuilder resourcesQuery = Expressions.bool()
				// the current resource type and versions of that resource type
				.filter(ResourceDocument.Expressions.resourceType(getResourceType())); 
		
		// resource and version doc has id field
		addIdFilter(resourcesQuery, ids -> Expressions.bool()
				.should(ResourceDocument.Expressions.ids(ids))
				.should(ResourceDocument.Expressions.urls(ids))
				.build()); 
		// apply _name filter to the id fields, we use the same value for both id and name
		addFilter(resourcesQuery, OptionKey.NAME, String.class, ResourceDocument.Expressions::ids); 
		addFilter(resourcesQuery, OptionKey.URL, String.class, ResourceDocument.Expressions::urls);
		addFilter(resourcesQuery, OptionKey.VERSION, String.class, VersionDocument.Expressions::versions);
		
		if (containsKey(OptionKey.TITLE)) {
			resourcesQuery.must(TermFilter.match().term(getString(OptionKey.TITLE)).build().toExpression(ResourceDocument.Fields.TITLE));
		}
		
		Hits<ResourceFragment> internalResources = context.service(RevisionSearcher.class)
				.search(Query.select(ResourceFragment.class)
				.from(ResourceDocument.class, VersionDocument.class)
				.fields(fields)
				.where(resourcesQuery.build())
				.searchAfter(searchAfter())
				.limit(limit())
				.sortBy(querySortBy(context))
				.build());
		
		// in case of version fragments, extract their CodeSystem only information from the latest CodeSystem document (no need to represent older data there)
		fillResourceOnlyProperties(context, internalResources, fields);
			
		return prepareBundle()
				.entry(internalResources.stream().map(codeSystem -> toFhirResourceEntry(context, codeSystem)).collect(Collectors.toList()))
//				.after(internalResources.getSearchAfter())
				.total(internalResources.getTotal())
				.build();
	}

	private final List<String> replaceFieldsToLoad(List<String> fields) {
		List<String> fieldsToLoad = Lists.newArrayList(fields);
		// if any fields defined for field selection, then make sure toolingId, resourceType and id is part of the selection, so it is returned and will be available when needed
		if (!fieldsToLoad.isEmpty()) {
			if (!fieldsToLoad.contains(ResourceDocument.Fields.ID)) {
				fieldsToLoad.add(ResourceDocument.Fields.ID);
			}
			if (!fieldsToLoad.contains(ResourceDocument.Fields.RESOURCE_TYPE)) {
				fieldsToLoad.add(ResourceDocument.Fields.RESOURCE_TYPE);
			}
			if (!fieldsToLoad.contains(ResourceDocument.Fields.TOOLING_ID)) {
				fieldsToLoad.add(ResourceDocument.Fields.TOOLING_ID);
			}
			if (!fieldsToLoad.contains(ResourceDocument.Fields.CREATED_AT)) {
				fieldsToLoad.add(ResourceDocument.Fields.CREATED_AT);
			}
			if (!fieldsToLoad.contains(ResourceDocument.Fields.UPDATED_AT)) {
				fieldsToLoad.add(ResourceDocument.Fields.UPDATED_AT);
			}
		}
		
		// remove all fields that are not part of the current resource model
		fieldsToLoad.removeAll(EXTERNAL_FHIR_RESOURCE_FIELDS);
		fieldsToLoad.removeAll(getExternalFhirResourceFields());
		// replace publisher with internal settings field (publisher is stored within resource metadata)
		if (fieldsToLoad.contains(MetadataResource.Fields.PUBLISHER)) {
			fieldsToLoad.remove(MetadataResource.Fields.PUBLISHER);
			fieldsToLoad.add(ResourceDocument.Fields.SETTINGS);
		}
		// replace identifier with internal oid field
		if (fieldsToLoad.contains(CodeSystem.Fields.IDENTIFIER)) {
			fieldsToLoad.remove(CodeSystem.Fields.IDENTIFIER);
			fieldsToLoad.add(ResourceDocument.Fields.OID);
		}
		
		// for all supported FHIR Metadata Resources replace the incoming date property with the effectiveTime when requesting it
		if (fieldsToLoad.contains(MetadataResource.Fields.DATE)) {
			fieldsToLoad.remove(MetadataResource.Fields.DATE);
			fieldsToLoad.add(VersionDocument.Fields.EFFECTIVE_TIME);
		}
		
		// support any specific field selection changes
		configureFieldsToLoad(fieldsToLoad);
		
		return fieldsToLoad;
	}

	/**
	 * Subclasses may optionally override the current field selection list of they know that an index field is not present or named differently in their specific model.
	 * @param fields
	 */
	protected void configureFieldsToLoad(List<String> fields) {
	}

	protected final Builder prepareBundle() {
		return Bundle.builder(getResourceType())
				.type(BundleType.SEARCHSET)
				.meta(Meta.builder()
						.addTag(CompareUtils.isEmpty(fields()) ? null : Coding.CODING_SUBSETTED)
						.lastUpdated(Instant.builder().instant(java.time.Instant.now()).build())
						.build());
	}

	/**
	 * @return the Snow Owl resource type representation to search for the appropriate documents in the underlying index
	 */
	protected abstract String getResourceType();
	
	protected Set<String> getExternalFhirResourceFields() {
		return Set.of();
	}
	
	private void fillResourceOnlyProperties(RepositoryContext context, Hits<ResourceFragment> internalResources, List<String> fields) throws IOException {
		for (final ResourceFragment fragment : internalResources) {
			if (CompareUtils.isEmpty(fragment.getVersion())) {
				// This fragment was created from a resource document, set resourceDescription and continue
				fragment.setResourceDescription(fragment.getDescription());
				continue;
			}
			
			if (!CompareUtils.isEmpty(fragment.getStatus())) {
				// This fragment was created from a version with snapshot (it has a resource status on it)
				continue;
			}
			
			// Retrieve resource representation with the same "created" branch timestamp (we defer to the low-level Searcher for this) 
			final Hits<ResourceFragment> resourceFragments = context.service(RevisionSearcher.class)
				.searcher()
				.search(Query.select(ResourceFragment.class)
				.from(ResourceDocument.class)
				.fields(fields)
				.where(Expressions.bool()
					.filter(ResourceDocument.Expressions.id(fragment.getResourceURI().getResourceId()))
					.filter(ResourceDocument.Expressions.validAsOf(fragment.getCreatedAt()))
					.build())
				.limit(1)
				.build());
			
			final ResourceFragment resourceSnapshot = Iterables.getFirst(resourceFragments, null);
			if (resourceSnapshot != null) {
				fragment.setTitle(resourceSnapshot.getTitle());
				fragment.setStatus(resourceSnapshot.getStatus());
				fragment.setContact(resourceSnapshot.getContact());
				fragment.setCopyright(resourceSnapshot.getCopyright());
				fragment.setLanguage(resourceSnapshot.getLanguage());
				fragment.setPurpose(resourceSnapshot.getPurpose());
				fragment.setOid(resourceSnapshot.getOid());
				fragment.setSettings(resourceSnapshot.getSettings());
				fragment.setResourceDescription(resourceSnapshot.getDescription());
			}
		}
	}
	
	protected final ResourceResponseEntry toFhirResourceEntry(RepositoryContext context, ResourceFragment fragment) {
		return ResourceResponseEntry.builder().resource(toFhirResource(context, fragment)).build();
	}
	
	protected T toFhirResource(RepositoryContext context, ResourceFragment resource) {
		B entry = createResourceBuilder()
				// mandatory fields
				.id(resource.getId())
				.status(PublicationStatus.getByCodeValue(resource.getStatus()))
				.meta(
					Meta.builder()
						// updatedAt returns version creation time (createdAt and updatedAt is the same) or latest updateAt value from the resource :gold:
						.lastUpdated(Optional.ofNullable(resource.getUpdatedAt())
								// fall back to createdAt if updatedAt is not present
								.or(() -> Optional.ofNullable(resource.getCreatedAt()))
								.map(lastUpdated -> Instant.builder().instant(lastUpdated).build())
								// or null if none of them
								.orElse(null)
								)
					.build()
				)
				.toolingId(resource.getToolingId()); 
		
		// optional fields
		// we are using the ID of the resource as machine readable name
		includeIfFieldSelected(MetadataResource.Fields.NAME, resource::getId, entry::name);
		includeIfFieldSelected(MetadataResource.Fields.TITLE, resource::getTitle, entry::title);
		includeIfFieldSelected(MetadataResource.Fields.URL, resource::getUrl, entry::url);
		includeIfFieldSelected(DomainResource.Fields.TEXT, () -> Narrative.builder().div("<div></div>").status(NarrativeStatus.EMPTY).build(), entry::text);
		includeIfFieldSelected(MetadataResource.Fields.VERSION, resource::getVersion, entry::version);
		includeIfFieldSelected(MetadataResource.Fields.PUBLISHER, () -> getPublisher(resource), entry::publisher);
		includeIfFieldSelected(FhirResource.Fields.LANGUAGE, resource::getLanguage, entry::language);
		includeIfFieldSelected(MetadataResource.Fields.DATE, () -> resource.getEffectiveTime() == null ? null : new Date(resource.getEffectiveTime()), entry::date);
		// XXX: use the resource's description in all cases
		includeIfFieldSelected(MetadataResource.Fields.DESCRIPTION, resource::getResourceDescription, entry::description);
		includeIfFieldSelected(MetadataResource.Fields.PURPOSE, resource::getPurpose, entry::purpose);

		if (CompareUtils.isEmpty(fields()) || fields().contains(CodeSystem.Fields.CONTACT)) {
			ContactDetail contact = getContact(resource);
			if (contact != null) {
				entry.addContact(contact);
			}
		}
		
		// XXX: inclusion of the copyright field is pushed to each search request subclass as specific resource 
		// and builder subtypes are available there.
		//
		// includeIfFieldSelected(CodeSystem.Fields.COPYRIGHT, resource::getCopyright, entry::copyright);
		
		expandResourceSpecificFields(context, entry, resource);
		
		return entry.build();
	}

	private String getPublisher(ResourceFragment resource) {
		Map<String, Object> settings = resource.getSettings();
		if (settings == null) {
			return "";
		}
		
		return (String) settings.getOrDefault(CodeSystem.Fields.PUBLISHER, "");
	}

	private ContactDetail getContact(ResourceFragment resource) {
		if (StringUtils.isEmpty(resource.getContact())) {
			return null;
		}
		
		return ContactDetail.builder()
			.addTelecom(ContactPoint.builder()
				.system("url")
				.value(resource.getContact())
				.build())
			.build();
	}
	
	protected void expandResourceSpecificFields(RepositoryContext context, B entry, ResourceFragment resource) {
	}

	protected abstract B createResourceBuilder();

	protected final <C> void includeIfFieldSelected(String field, Supplier<C> getter, Consumer<C> setter) {
		if (CompareUtils.isEmpty(fields()) || fields().contains(field)) {
			setter.accept(getter.get());
		}
	}
	
	/**
	 * @since 8.0
	 */
	protected static class ResourceFragment {
		String id;
		String version;
		String description;
		String resourceType;
		Long createdAt;
		Long updatedAt;
		String toolingId;
		String url;
		String branchPath;
		Long effectiveTime;
		
		String resourceDescription;
		String title;
		String status;
		String contact;
		String copyright;
		String language;
		String purpose;
		String oid;
		Map<String, Object> settings;
		
		RevisionBranchPoint created;
		
		public final ResourceURI getResourceURI() {
			return ResourceURI.of(resourceType, id);
		}
		
		public String getId() {
			return id;
		}
		
		public String getVersion() {
			return version;
		}
		
		public Long getEffectiveTime() {
			return effectiveTime;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getResourceType() {
			return resourceType;
		}
		
		public Long getCreatedAt() {
			return createdAt;
		}
		
		public Long getUpdatedAt() {
			return updatedAt;
		}
		
		public String getToolingId() {
			return toolingId;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getBranchPath() {
			return branchPath;
		}
		
		public String getResourceDescription() {
			return resourceDescription;
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getStatus() {
			return status;
		}
		
		public String getContact() {
			return contact;
		}
		
		public String getCopyright() {
			return copyright;
		}
		
		public String getLanguage() {
			return language;
		}
		
		public String getPurpose() {
			return purpose;
		}
		
		public String getOid() {
			return oid;
		}
		
		public Map<String, Object> getSettings() {
			return settings;
		}
		
		public RevisionBranchPoint getCreated() {
			return created;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public void setVersion(String version) {
			this.version = version;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public void setResourceType(String resourceType) {
			this.resourceType = resourceType;
		}
		
		public void setCreatedAt(Long createdAt) {
			this.createdAt = createdAt;
		}
		
		public void setUpdatedAt(Long updatedAt) {
			this.updatedAt = updatedAt;
		}
		
		public void setToolingId(String toolingId) {
			this.toolingId = toolingId;
		}
		
		public void setUrl(String url) {
			this.url = url;
		}
		
		public void setBranchPath(String branchPath) {
			this.branchPath = branchPath;
		}
		
		public void setResourceDescription(String resourceDescription) {
			this.resourceDescription = resourceDescription;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public void setStatus(String status) {
			this.status = status;
		}
		
		public void setContact(String contact) {
			this.contact = contact;
		}
		
		public void setCopyright(String copyright) {
			this.copyright = copyright;
		}
		
		public void setLanguage(String language) {
			this.language = language;
		}
		
		public void setPurpose(String purpose) {
			this.purpose = purpose;
		}
		
		public void setOid(String oid) {
			this.oid = oid;
		}
		
		public void setSettings(Map<String, Object> settings) {
			this.settings = settings;
		}
		
		public void setCreated(RevisionBranchPoint created) {
			this.created = created;
		}
	}
}
