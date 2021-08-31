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
package com.b2international.snowowl.fhir.core.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.TermFilter;
import com.b2international.snowowl.core.version.VersionDocument;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.codesystems.PublicationStatus;
import com.b2international.snowowl.fhir.core.model.*;
import com.b2international.snowowl.fhir.core.model.Bundle.Builder;
import com.b2international.snowowl.fhir.core.model.capabilitystatement.CapabilityStatement;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 8.0
 */
public abstract class FhirResourceSearchRequest<B extends MetadataResource.Builder<B, T>, T extends FhirResource> extends SearchResourceRequest<RepositoryContext, Bundle> {

	private static final long serialVersionUID = 1L;

	private static final Set<String> EXTERNAL_FHIR_RESOURCE_FIELDS = Set.of(
		MetadataResource.Fields.NAME,
		MetadataResource.Fields.DATE,
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
		
		// remove all fields that are not part of the current resource model
		fields.removeAll(EXTERNAL_FHIR_RESOURCE_FIELDS);
		fields.removeAll(getExternalFhirResourceFields());
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
		final ExpressionBuilder resourcesQuery = Expressions.builder()
				// the current resource type and versions of that resource type
				.filter(ResourceDocument.Expressions.resourceType(getResourceType())); 
		
		// resource and version doc has id field
		addIdFilter(resourcesQuery, ResourceDocument.Expressions::ids); 
		// apply _name filter to the id fields, we use the same value for both id and name
		addFilter(resourcesQuery, OptionKey.NAME, String.class, ResourceDocument.Expressions::ids); 
		addFilter(resourcesQuery, OptionKey.URL, String.class, ResourceDocument.Expressions::urls);
		addFilter(resourcesQuery, OptionKey.VERSION, String.class, VersionDocument.Expressions::versions);
		
		if (containsKey(OptionKey.TITLE)) {
			resourcesQuery.must(ResourceDocument.Expressions.defaultTitleDisjunctionQuery(TermFilter.defaultTermMatch(getString(OptionKey.TITLE))));
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
//				.after(internalCodeSystems.getSearchAfter())
				.total(internalResources.getTotal())
				.build();
	}
	

	protected final Builder prepareBundle() {
		return Bundle.builder(getResourceType())
				.type(BundleType.SEARCHSET)
				.meta(Meta.builder()
						.addTag(CompareUtils.isEmpty(fields()) ? null : Coding.CODING_SUBSETTED)
						.lastUpdated(Instant.builder().instant(java.time.Instant.now()).build())
						.build());
	}

	protected abstract String getResourceType();
	
	protected Set<String> getExternalFhirResourceFields() {
		return Set.of();
	}
	
	private void fillResourceOnlyProperties(RepositoryContext context, Hits<ResourceFragment> internalResources, List<String> fields) throws IOException {
		final Set<String> versionResources = internalResources.stream()
				.filter(fragment -> !CompareUtils.isEmpty(fragment.getVersion()))
				.map(fragment -> fragment.getResourceURI().getResourceId())
				.collect(Collectors.toSet());
		Map<String, ResourceFragment> internalResourcesById = new HashMap<>(internalResources.getHits().size());
		internalResources.forEach(fragment -> {
			internalResourcesById.put(fragment.getId(), fragment);
		});
		
		Set<String> missingCodeSystems = Sets.difference(versionResources, internalResourcesById.keySet());
		if (!missingCodeSystems.isEmpty()) {
			context.service(RevisionSearcher.class)
				.search(Query.select(ResourceFragment.class)
				.from(ResourceDocument.class)
				.fields(fields)
				.where(ResourceDocument.Expressions.ids(missingCodeSystems))
				.limit(missingCodeSystems.size())
				.build())
				.forEach(missingFragment -> {
					internalResourcesById.put(missingFragment.getId(), missingFragment);
				});
		}
		
		for (ResourceFragment versionFragment : internalResourcesById.values()) {
			if (!CompareUtils.isEmpty(versionFragment.getVersion())) {
				ResourceFragment versionCodeSystem = internalResourcesById.get(versionFragment.getResourceURI().getResourceId());
				versionFragment.setStatus(versionCodeSystem.getStatus());
				versionFragment.setOwner(versionCodeSystem.getOwner());
				versionFragment.setCopyright(versionCodeSystem.getCopyright());
				versionFragment.setLanguage(versionCodeSystem.getLanguage());
				versionFragment.setDescription(versionCodeSystem.getDescription());
				versionFragment.setPurpose(versionCodeSystem.getPurpose());
				versionFragment.setOid(versionCodeSystem.getOid());
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
						// createdAt returns version creation time or latest update of the resource :gold:
						.lastUpdated(Instant.builder().instant(resource.getCreatedAt()).build())
					.build()
				)
				.toolingId(resource.getToolingId()); 
		
		// optional fields
		// we are using the ID of the resource as machine readable name
		includeIfFieldSelected(CodeSystem.Fields.NAME, resource::getId, entry::name);
		includeIfFieldSelected(CodeSystem.Fields.TITLE, resource::getTitle, entry::title);
		includeIfFieldSelected(CodeSystem.Fields.URL, resource::getUrl, entry::url);
		includeIfFieldSelected(CodeSystem.Fields.TEXT, () -> Narrative.builder().div("<div></div>").status(NarrativeStatus.EMPTY).build(), entry::text);
		includeIfFieldSelected(CodeSystem.Fields.VERSION, resource::getVersion, entry::version);
		includeIfFieldSelected(CodeSystem.Fields.PUBLISHER, resource::getOwner, entry::publisher);
		includeIfFieldSelected(CodeSystem.Fields.LANGUAGE, resource::getLanguage, entry::language);
		includeIfFieldSelected(CodeSystem.Fields.DESCRIPTION, resource::getDescription, entry::description);
		includeIfFieldSelected(CodeSystem.Fields.PURPOSE, resource::getPurpose, entry::purpose);
		
		//includeIfFieldSelected(CodeSystem.Fields.COPYRIGHT, resource::getCopyright, entry::copyright);
		
		if (CompareUtils.isEmpty(CodeSystem.Fields.COPYRIGHT) || fields().contains(CodeSystem.Fields.COPYRIGHT)) {
			if (entry instanceof CodeSystem.Builder) {
				CodeSystem.Builder builder = (CodeSystem.Builder) entry;
				builder.copyright(resource.getCopyright());
			} else if (entry instanceof ValueSet.Builder) {
				ValueSet.Builder builder = (ValueSet.Builder) entry;
				builder.copyright(resource.getCopyright());
			} else if (entry instanceof ConceptMap.Builder) {
				ConceptMap.Builder builder = (ConceptMap.Builder) entry;
				builder.copyright(resource.getCopyright());
			} else if (entry instanceof CapabilityStatement.Builder) {
				CapabilityStatement.Builder builder = (CapabilityStatement.Builder) entry;
				builder.copyright(resource.getCopyright());
			}
		}
		
		expandResourceSpecificFields(context, entry, resource);
		
		return entry.build();
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
		
		String resourceType;
		
		String id;
		
		String url;
		
		String title;
		
		String toolingId;
		
		String branchPath;
		
		String version;
		
		Long createdAt;
		
		// Resource only fields, for Versions they got their values from the corresponding Resource
		
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
		
		public void setResourceType(String resourceType) {
			this.resourceType = resourceType;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public void setUrl(String url) {
			this.url = url;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public void setToolingId(String toolingId) {
			this.toolingId = toolingId;
		}
		
		public void setBranchPath(String branchPath) {
			this.branchPath = branchPath;
		}
		
		public void setVersion(String version) {
			this.version = version;
		}
		
		public void setCreatedAt(Long createdAt) {
			this.createdAt = createdAt;
		}
		
		public void setStatus(String status) {
			this.status = status;
		}
		
		public void setOwner(String owner) {
			this.owner = owner;
		}
		
		public void setCopyright(String copyright) {
			this.copyright = copyright;
		}
		
		public void setLanguage(String language) {
			this.language = language;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public void setPurpose(String purpose) {
			this.purpose = purpose;
		}
		
		public void setOid(String oid) {
			this.oid = oid;
		}
		
	}

}
