/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequestBuilder.*;
import static com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemSearchRequestBuilder.CODE_SYSTEM_CONTACT;
import static com.b2international.snowowl.fhir.core.request.codesystem.FhirCodeSystemSearchRequestBuilder.CODE_SYSTEM_IDENTIFIER;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.Bundle.BundleType;
import org.hl7.fhir.r5.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r5.model.Enumerations.PublicationStatus;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
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
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @since 8.0
 */
public abstract class FhirResourceSearchRequest<T extends CanonicalResource> extends SearchResourceRequest<RepositoryContext, Bundle> {

	private static final long serialVersionUID = 1L;
	
	// Element names that appear on FHIR resources but should not be used for field selection in ES queries
	private static final Set<String> COMMON_FHIR_ELEMENTS = ImmutableSet.of(
		CANONICAL_RESOURCE_NAME, 
		BASE_RESOURCE_META, 
		DOMAIN_RESOURCE_TEXT
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
	protected Bundle createEmptyResult(final int limit) {
		// XXX: limit is not represented in a FHIR search set
		final Bundle bundle = new Bundle(BundleType.SEARCHSET);
		final Meta meta = bundle.getMeta();
		meta.setLastUpdated(new Date());
		return bundle;
	}

	@Override
	protected final Bundle doExecute(RepositoryContext context) throws IOException {
		// Apply field selection
		final List<String> fields = replaceFieldsToLoad(fields());

		// Prepare filters
		final ExpressionBuilder resourceExpression = Expressions.bool()
			// Return resources and versions that match the resource type
			.filter(ResourceDocument.Expressions.resourceType(getResourceType())); 

		// Filter by ID and URL fields
		addIdFilter(resourceExpression, ids -> Expressions.bool()
			.should(ResourceDocument.Expressions.ids(ids))
			.should(ResourceDocument.Expressions.urls(ids))
			.build());

		// Apply _name filter to the id fields, we use the same value for both "id" and "name"
		addFilter(resourceExpression, OptionKey.NAME, String.class, ResourceDocument.Expressions::ids); 
		addFilter(resourceExpression, OptionKey.URL, String.class, ResourceDocument.Expressions::urls);
		addFilter(resourceExpression, OptionKey.VERSION, String.class, VersionDocument.Expressions::versions);

		if (containsKey(OptionKey.TITLE)) {
			final String title = getString(OptionKey.TITLE);
			final Expression titleExpression = TermFilter.match()
				.term(title)
				.build()
				.toExpression(ResourceDocument.Fields.TITLE);

			if (containsKey(OptionKey.NAME)) {
				// If there is a name filter as well, match by title only
				resourceExpression.must(titleExpression);
			} else {
				// Otherwise match both title and name (ie. the resource's identifier in Snow Owl)
				final List<String> titleAlternatives = List.of(title, title.toUpperCase(Locale.ENGLISH));
				resourceExpression.must(Expressions.bool()
					.should(titleExpression)
					.should(Expressions.boost(Expressions.matchAny(ResourceDocument.Fields.ID, titleAlternatives), 1000.0f))
					.build());
			}
		}

		// Search with partial document mapping (ResourceFragment) that is applicable to both resources and versions
		final Hits<ResourceFragment> internalResources = context.service(RevisionSearcher.class)
			.search(Query.select(ResourceFragment.class)
			.from(ResourceDocument.class, VersionDocument.class)
			.fields(fields)
			.where(resourceExpression.build())
			.searchAfter(searchAfter())
			.limit(limit())
			.sortBy(querySortBy(context))
			.build());

		// Retrieve information that is not present on on version fragments from resource documents, using point-in-time querying
		fillResourceOnlyProperties(context, internalResources, fields);

		final Bundle searchSet = createEmptyResult(0);

		// Stash searchAfter keys in user data as building full paging links here is a bit difficult
		searchSet.setUserData("currentPageId", searchAfter());
		searchSet.setUserData("nextPageId", internalResources.getSearchAfter());

		for (final ResourceFragment fragment : internalResources) {
			final BundleEntryComponent bundleEntry = searchSet.addEntry();
			bundleEntry.setResource(toFhirResource(context, fragment));
		}

		searchSet.setTotal(internalResources.getTotal());
		return searchSet;
	}

	private List<String> replaceFieldsToLoad(List<String> fields) {
		final Set<String> fieldsToLoad = newLinkedHashSet(fields);

		// If any fields are set, also add a basic set of fields regardless of whether they were requested or not
		if (!fieldsToLoad.isEmpty()) {
			fieldsToLoad.add(ResourceDocument.Fields.ID);
			fieldsToLoad.add(ResourceDocument.Fields.RESOURCE_TYPE);
			fieldsToLoad.add(ResourceDocument.Fields.TOOLING_ID);
			fieldsToLoad.add(ResourceDocument.Fields.CREATED_AT);
			fieldsToLoad.add(ResourceDocument.Fields.UPDATED_AT);
			fieldsToLoad.add(VersionDocument.Fields.VERSION);
		}

		// Remove all fields that are not part of the current indexed resource documents
		fieldsToLoad.removeAll(COMMON_FHIR_ELEMENTS);
		fieldsToLoad.removeAll(getExternalFhirElements());

		// Replace "publisher" with internal settings field (publisher is stored within resource metadata)
		if (fieldsToLoad.contains(CANONICAL_RESOURCE_PUBLISHER)) {
			fieldsToLoad.remove(CANONICAL_RESOURCE_PUBLISHER);
			fieldsToLoad.add(ResourceDocument.Fields.SETTINGS);
		}

		// Replace "identifier" with internal OID field
		if (fieldsToLoad.contains(CODE_SYSTEM_IDENTIFIER)) {
			fieldsToLoad.remove(CODE_SYSTEM_IDENTIFIER);
			fieldsToLoad.add(ResourceDocument.Fields.OID);
		}

		// For all supported FHIR Canonical Resources replace the incoming date property with the effectiveTime when requesting it
		if (fieldsToLoad.contains(CANONICAL_RESOURCE_DATE)) {
			fieldsToLoad.remove(CANONICAL_RESOURCE_DATE);
			fieldsToLoad.add(VersionDocument.Fields.EFFECTIVE_TIME);
		}

		// support any specific field selection changes
		configureFieldsToLoad(fieldsToLoad);

		return ImmutableList.copyOf(fieldsToLoad);
	}

	protected Collection<String> getExternalFhirElements() {
		return ImmutableSet.of();
	}

	/**
	 * Subclasses may optionally override the current field selection list of they
	 * know that an index field is not present or named differently in their
	 * specific model.
	 * 
	 * @param fieldsToLoad
	 */
	protected void configureFieldsToLoad(Set<String> fieldsToLoad) {
		return;
	}

	/**
	 * @return the Snow Owl resource type representation to search for the
	 * appropriate documents in the underlying index
	 */
	protected abstract String getResourceType();

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

	protected T toFhirResource(RepositoryContext context, ResourceFragment fragment) {
		final T resource = createEmptyResource();

		// Mandatory fields
		//////////////////////

		resource.setId(fragment.getId());
		resource.setStatus(getPublicationStatus(fragment));

		final Meta meta = resource.getMeta();

		// updatedAt returns version creation time (createdAt and updatedAt is the same) or latest updateAt value from the resource :gold:
		Optional.ofNullable(fragment.getUpdatedAt())
			// fall back to createdAt if updatedAt is not present
			.or(() -> Optional.ofNullable(fragment.getCreatedAt()))
			.ifPresent(lastUpdated -> meta.setLastUpdated(new Date(lastUpdated)));

		// Tooling ID has no "right" place on a FHIR resource, will store it in user data for now
		meta.setUserData("toolingId", fragment.getToolingId());

		// Optional fields
		/////////////////////

		// we are using the ID of the resource as machine readable name
		includeIfFieldSelected(CANONICAL_RESOURCE_NAME, fragment::getId, resource::setName);
		includeIfFieldSelected(CANONICAL_RESOURCE_TITLE, fragment::getTitle, resource::setTitle);
		includeIfFieldSelected(CANONICAL_RESOURCE_URL, fragment::getUrl, resource::setUrl);
		includeIfFieldSelected(METADATA_RESOURCE_VERSION, fragment::getVersion, resource::setVersion);
		includeIfFieldSelected(CANONICAL_RESOURCE_PUBLISHER, () -> getPublisher(fragment), resource::setPublisher);
		includeIfFieldSelected(RESOURCE_LANGUAGE, fragment::getLanguage, resource::setLanguage);
		includeIfFieldSelected(CANONICAL_RESOURCE_DATE, () -> getDate(fragment), resource::setDate);
		// XXX: use the resource's description in all cases
		includeIfFieldSelected(METADATA_RESOURCE_DESCRIPTION, fragment::getResourceDescription, resource::setDescription);
		includeIfFieldSelected(METADATA_RESOURCE_PURPOSE, fragment::getPurpose, resource::setPurpose);

		if (CompareUtils.isEmpty(fields()) || fields().contains(CODE_SYSTEM_CONTACT)) {
			ContactDetail contact = getContact(fragment);
			if (contact != null) {
				resource.addContact(contact);
			}
		}

		// XXX: inclusion of the copyright field is pushed to each search request subclass as specific resource and builder subtypes are available there
		// includeIfFieldSelected(CodeSystem.Fields.COPYRIGHT, resource::getCopyright, entry::copyright);

		// XXX: inclusion of an empty narrative is delegated to a narrative generation step in HAPI FHIR
		// includeIfFieldSelected(DOMAIN_RESOURCE_TEXT, ..., resource::setText);

		expandResourceSpecificFields(context, resource, fragment);
		return resource;
	}

	private PublicationStatus getPublicationStatus(ResourceFragment fragment) {
		return PublicationStatus.isValidCode(fragment.getStatus())
			? PublicationStatus.fromCode(fragment.getStatus())
			: PublicationStatus.UNKNOWN;
	}

	private String getPublisher(ResourceFragment fragment) {
		final Map<String, Object> settings = fragment.getSettings();
		if (settings == null) {
			return null;
		}

		return (String) settings.get(CodeSystem.Fields.PUBLISHER);
	}

	private Date getDate(ResourceFragment fragment) {
		final Long effectiveTime = fragment.getEffectiveTime();
		if (effectiveTime == null) {
			return null;
		}

		return new Date(effectiveTime);
	}

	private ContactDetail getContact(ResourceFragment fragment) {
		final String contact = fragment.getContact();
		if (StringUtils.isEmpty(contact)) {
			return null;
		}

		final ContactDetail contactDetail = new ContactDetail();
		final ContactPoint telecom = contactDetail.addTelecom();
		telecom.setSystem(ContactPointSystem.URL);
		telecom.setValue(contact);

		return contactDetail;
	}

	protected void expandResourceSpecificFields(RepositoryContext context, T resource, ResourceFragment fragment) {
		return;
	}

	protected abstract T createEmptyResource();

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
