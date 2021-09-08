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
package com.b2international.snowowl.core.request;

import static com.google.common.collect.Maps.newHashMap;

import java.util.*;

import com.b2international.commons.exceptions.AlreadyExistsException;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.CycleDetectedException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.bundle.Bundles;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.internal.ResourceDocument.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @since 8.0
 */
public abstract class BaseResourceUpdateRequest extends UpdateRequest<TransactionContext> {

	private static final long serialVersionUID = 1L;

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
	
	protected BaseResourceUpdateRequest(String componentId) {
		super(componentId);
	}

	@Override
	public final Boolean execute(TransactionContext context) {
		final ResourceDocument resource = context.lookup(componentId(), ResourceDocument.class);
		final ResourceDocument.Builder updated = ResourceDocument.builder(resource);

		boolean changed = false;

		changed |= updateSpecializedProperties(context, resource, updated);

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
			context.add(updated.build());
		}

		return changed;
	}

	private boolean updateBundle(TransactionContext context, String resourceId, String oldBundleId, Builder updated) {
		if (bundleId == null || bundleId.equals(oldBundleId)) {
			return false;
		}
		
		if (IComponent.ROOT_ID.equals(bundleId)) {
			updated.bundleAncestorIds(List.of(bundleId));
			updated.bundleId(bundleId);
			return true;
		}
		
		Bundles bundles = ResourceRequests.bundles()
			.prepareSearch()
			.filterById(bundleId)
			.one()
			.build()
			.execute(context);
				
		if (bundles.getTotal() == 0) {
			throw new NotFoundException("Bundle parent", bundleId).toBadRequestException();
		}

		Bundle parentBundle = bundles.first().get();
		if (parentBundle.getBundleId().equals(resourceId) || parentBundle.getBundleAncestorIds().contains(resourceId)) {
			throw new CycleDetectedException("Setting parent bundle ID to '" + bundleId + "' would create a loop.");
		}
		
		// Update the "direct parent" and ancestor identifiers on the resource
		updated.bundleAncestorIds(parentBundle.getBundleAncestorIdsForChild());
		updated.bundleId(parentBundle.getId());
		
		// Update ancestors on the resource and its descendants
		final Iterator<Resource> descendants = ResourceRequests.prepareSearch()
			.filterByBundleAncestorId(resourceId)
			.setLimit(5_000)
			.stream(context)
			.flatMap(Resources::stream)
			.iterator();
		
		final Multimap<String, Resource> resourcesByParent = Multimaps.index(descendants, Resource::getBundleId);
		final Map<String, List<String>> ancestorsForChildByParentId = newHashMap(Map.of(resourceId, ImmutableList.<String>builder()
			.addAll(parentBundle.getBundleAncestorIdsForChild())
			.add(parentBundle.getId())
			.build()));
		
		// Start with the immediate children of the current resource
		final Deque<Resource> toProcess = new ArrayDeque<>(resourcesByParent.get(resourceId));
		
		while (!toProcess.isEmpty()) {
			final Resource current = toProcess.removeFirst();
			final String currentId = current.getId();
			final ResourceDocument resource = context.lookup(currentId, ResourceDocument.class);
			final ResourceDocument.Builder currentBuilder = ResourceDocument.builder(resource);

			final String parentId = current.getBundleId();
			final List<String> ancestorIds = ancestorsForChildByParentId.get(parentId);
			currentBuilder.bundleAncestorIds(ancestorIds);
			context.add(currentBuilder.build());
			
			final Collection<Resource> children = resourcesByParent.get(currentId);
			if (!children.isEmpty()) {
				final List<String> nextAncestorIds = ImmutableList.<String>builder()
					.addAll(ancestorIds)
					.add(parentId)
					.build();

				ancestorsForChildByParentId.put(currentId, nextAncestorIds);
				toProcess.addAll(children);
			}
		}
		
		return true;
	}

	protected abstract boolean updateSpecializedProperties(TransactionContext context, ResourceDocument resource, Builder updated);
}
