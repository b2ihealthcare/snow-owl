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
package com.b2international.snowowl.core.request;

import static com.google.common.collect.Maps.newHashMap;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import com.b2international.commons.collections.Collections3;
import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.CycleDetectedException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.bundle.Bundles;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.b2international.snowowl.core.internal.ResourceRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 8.0
 */
public abstract class BaseResourceUpdateRequest extends UpdateRequest<TransactionContext> {

	private static final long serialVersionUID = 2L;

	@JsonProperty
	private String url;
	
	@JsonProperty
	private String title;
	
	@JsonProperty
	private String language;
	
	@JsonProperty
	private String description;
	
	@JsonProperty
	private String status;
	
	@JsonProperty
	private String copyright;
	
	@JsonProperty
	private String owner;
	
	@JsonProperty
	private String contact;
	
	@JsonProperty
	private String usage;
	
	@JsonProperty
	private String purpose;

	@JsonProperty
	private String bundleId;
	
	@JsonProperty
	private Map<String, Object> settings;

	// runtime fields
	private transient ResourceDocument resource;
	
	protected BaseResourceUpdateRequest(String componentId) {
		super(componentId);
	}
	
	protected final void setUrl(String url) {
		this.url = url;
	}
	
	protected final void setTitle(String title) {
		this.title = title;
	}
	
	protected final void setLanguage(String language) {
		this.language = language;
	}
	
	protected final void setDescription(String description) {
		this.description = description;
	}

	protected final void setStatus(String status) {
		this.status = status;
	}
	
	protected final void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
	protected final void setOwner(String owner) {
		this.owner = owner;
	}
	
	protected final void setContact(String contact) {
		this.contact = contact;
	}
	
	protected final void setUsage(String usage) {
		this.usage = usage;
	}
	
	protected final void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	protected final void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}
	
	protected final void setSettings(Map<String, Object> settings) {
		this.settings = settings;
	}
	
	protected final Map<String, Object> getSettings() {
		return settings;
	}
	
	@Override
	public final Boolean execute(TransactionContext context) {
		if (resource == null) {
			resource = context.lookup(componentId(), ResourceDocument.class);
		}
		final ResourceDocument.Builder updated = ResourceDocument.builder(resource);

		boolean changed = false;

		// url checked against all resources
		if (url != null && !url.equals(resource.getUrl())) {
			if (url.isBlank()) {
				throw new BadRequestException("Resource.url should not be empty string");
			}
			
			final boolean existingUrl = ResourceRequests.prepareSearch()
					.setLimit(0)
					.filterByUrl(url)
					.build()
					.execute(context)
					.getTotal() > 0;
					
			if (existingUrl) {
				throw new AlreadyExistsException("Resource", ResourceDocument.Fields.URL, url);
			}
			
			changed |= updateProperty(url, resource::getUrl, updated::url);
		}
		
		changed |= updateSpecializedProperties(context, resource, updated);
		changed |= updateSettings(resource, updated);

		changed |= updateBundle(context, resource.getId(), resource.getBundleId(), updated);
		
		changed |= updateProperty(title, resource::getTitle, updated::title);
		changed |= updateProperty(language, resource::getLanguage, updated::language);
		changed |= updateProperty(description, resource::getDescription, updated::description);
		changed |= updateProperty(status, resource::getStatus, updated::status);
		changed |= updateProperty(copyright, resource::getCopyright, updated::copyright);
		changed |= updateProperty(owner, resource::getOwner, updated::owner);
		changed |= updateProperty(contact, resource::getContact, updated::contact);
		changed |= updateProperty(usage, resource::getUsage, updated::usage);
		changed |= updateProperty(purpose, resource::getPurpose, updated::purpose);

		if (changed) {
			// make sure we null out the updatedAt property we before update
			updated.updatedAt(null);
			context.update(resource, updated.build());
		}

		return changed;
	}

	private boolean updateBundle(TransactionContext context, String resourceId, String oldBundleId, Builder updated) {
		if (bundleId == null || bundleId.equals(oldBundleId)) {
			return false;
		}
	
		final List<String> bundleAncestorIds = getBundleAncestorIds(context, resourceId);
		updated.bundleId(bundleId);
		updated.bundleAncestorIds(bundleAncestorIds);
		
		// Update bundle ancestor IDs on all descendants of the resource (their bundle ID does not change)
		final Iterator<Resource> descendants = ResourceRequests.prepareSearch()
			.filterByBundleAncestorId(resourceId)
			.setLimit(5_000)
			.stream(context)
			.flatMap(Resources::stream)
			.iterator();
		
		final Multimap<String, Resource> resourcesByParentId = Multimaps.index(descendants, Resource::getBundleId);
		
		// Calculate new ancestor ID list for direct children of this resource first
		final ImmutableList.Builder<String> ancestorIdsOfParent = ImmutableList.<String>builder().addAll(bundleAncestorIds);
		if (!IComponent.ROOT_ID.equals(bundleId)) {
			ancestorIdsOfParent.add(bundleId);
		}
				
		final Map<String, List<String>> newAncestorIdsByParentId = newHashMap(Map.of(resourceId, ancestorIdsOfParent.build()));
		
		// Start processing ancestor ID lists with the direct children of the resource
		final Deque<Map.Entry<String, Collection<Resource>>> toProcess = new ArrayDeque<>();
		toProcess.addLast(new AbstractMap.SimpleImmutableEntry<>(resourceId, resourcesByParentId.get(resourceId)));
		
		while (!toProcess.isEmpty()) {
			final Entry<String, Collection<Resource>> entry = toProcess.removeFirst();
			final String parentId = entry.getKey();
			final Collection<Resource> resources = entry.getValue();
			
			/*
			 * XXX: We will use the same ancestor ID list for all sibling resources. The
			 * call removes the entry from the map as it will never be read again after this
			 * iteration.
			 */
			final List<String> newAncestorIds = newAncestorIdsByParentId.remove(parentId);
			
			for (final Resource current : resources) {
				final String id = current.getId();
				final ResourceDocument resource = context.lookup(id, ResourceDocument.class);
				final ResourceDocument.Builder resourceBuilder = ResourceDocument.builder(resource);
				
				if (!Objects.equals(resource.getBundleAncestorIds(), newAncestorIds)) {
					resourceBuilder.bundleAncestorIds(newAncestorIds);
					context.update(resource, resourceBuilder.build());
				}
			
				final Collection<Resource> next = resourcesByParentId.get(id);
				if (!next.isEmpty()) {
					/*
					 * If the current resource has any children, make a note that we have to update
					 * bundleAncestorIds for them as well. The bundleId for these resources remains
					 * "id".
					 */
					final List<String> nextAncestorIds = ImmutableList.<String>builder()
						.addAll(newAncestorIds)
						.add(parentId)
						.build();
					
					newAncestorIdsByParentId.put(id, nextAncestorIds);
					toProcess.add(new AbstractMap.SimpleImmutableEntry<>(id, next));
				}
			}
		}
		
		return true;
	}
	
	private boolean updateSettings(final ResourceDocument resource, final ResourceDocument.Builder updated) {
		if (settings == null || settings.isEmpty()) {
			return false;
		}
		
		// Get mutable copy of existing settings, or an empty map for starters
		final Map<String, Object> updatedSettings = Optional.ofNullable(resource.getSettings())
				.map(Maps::newHashMap)
				.orElse(Maps.newHashMap());
		
		boolean changed = false;
		
		// Remove null values from map
		final Set<String> keysToRemove = Maps.filterValues(settings, v -> v == null).keySet();
		for (final String key : keysToRemove) {
			changed |= (updatedSettings.remove(key) != null);
		}

		// Merge (add or modify) non-null values
		final Set<String> keysToUpdate = Maps.filterValues(settings, v -> v != null).keySet();
		for (final String key : keysToUpdate) {
			changed |= updateProperty(settings.get(key), 			// value 
					() -> updatedSettings.get(key),                 // getter
					value -> updatedSettings.put(key, value));      // setter 
		}
		
		if (changed) {
			updated.settings(updatedSettings);
		}
		
		return changed;
	}

	private List<String> getBundleAncestorIds(final TransactionContext context, final String resourceId) {
		if (IComponent.ROOT_ID.equals(bundleId)) {
			return List.of(bundleId);
		}
		
		final Bundles bundles = ResourceRequests.bundles()
			.prepareSearch()
			.filterById(bundleId)
			.one()
			.build()
			.execute(context);
				
		if (bundles.getTotal() == 0) {
			throw new NotFoundException("Bundle parent", bundleId).toBadRequestException();
		}

		final Bundle parentBundle = bundles.first().get();
		if (parentBundle.getBundleId().equals(resourceId) || parentBundle.getBundleAncestorIds().contains(resourceId)) {
			throw new CycleDetectedException("Setting parent bundle ID to '" + bundleId + "' would create a loop.");
		}
			
		return parentBundle.getResourcePathSegments();
	}

	@OverridingMethodsMustInvokeSuper
	protected abstract boolean updateSpecializedProperties(TransactionContext context, ResourceDocument resource, Builder updated);

	@Override
	public final void collectAccessedResources(ServiceProvider context, Request<ServiceProvider, ?> req, List<String> accessedResources) {
		if (resource == null) {
			resource = context.service(ResourceRepository.class).read(searcher -> {
				return searcher.get(ResourceDocument.class, componentId());
			});
			if (resource == null) {
				throw new NotFoundException("Resource", componentId());
			}
		}
		accessedResources.add(componentId());
		// permission on any bundle is enough to update the contained resource
		accessedResources.add(resource.getBundleId());
		accessedResources.addAll(Collections3.toImmutableSet(resource.getBundleAncestorIds()));
	}
	
}
