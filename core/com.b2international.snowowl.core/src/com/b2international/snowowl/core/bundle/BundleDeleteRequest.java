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
package com.b2international.snowowl.core.bundle;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 8.0
 */
final class BundleDeleteRequest implements Request<TransactionContext, Boolean> {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	@NotNull
	private String resourceId;

	BundleDeleteRequest(final String resourceId) {
		this.resourceId = resourceId;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		try {
			final ResourceDocument bundleToDelete = context.lookup(resourceId, ResourceDocument.class);
			
			updateResourceBundles(context, bundleToDelete.getId(), bundleToDelete.getBundleId());
			
			context.delete(bundleToDelete);
		} catch (ComponentNotFoundException e) {
			// ignore, probably already deleted
		}
		return Boolean.TRUE;
	}
	
	/**
	 * <p>Pull up the deleted bundle's resources to its parent.<p>
	 * 
	 * <p>If <b>subBundle</b> is deleted in the tree below...</p>
	 * 
	 *<blockquote><pre>
	 * rootBundle
	 * 	|_ cs1
	 * 	|_ cs2
	 * 	|_ subBundle
	 * 		|_ cs3
	 * </blockquote></pre>
	 * 
	 * <p>...then <b>cs3's</b> bundle id is updated to <b>rootBundle's id</b> instead of being deleted with <b>subBundle</b>.<p>
	 * 
	 * <blockquote><pre>
	 * rootBundle
	 * 	|_ cs1
	 * 	|_ cs2
	 * 	|_ cs3
	 * </blockquote></pre>
	 * @param context 
	 */
	private void updateResourceBundles(TransactionContext context, final String id, final String bundleId) {
		ResourceRequests.prepareSearch()
			.filterByBundleId(id)
			.setLimit(10_000)
			.stream(context)
			.flatMap(Resources::stream)
			.forEach(resource -> {
				context.add(resource.toDocumentBuilder().bundleId(bundleId).build());
			});
	}
	
	public String getResourceId() {
		return resourceId;
	}

}
