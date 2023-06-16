/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.fhir.core.request;

import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.authorization.AuthorizationService;
import com.b2international.snowowl.core.bundle.Bundle;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.google.common.collect.ImmutableSet;

/**
 * @since 8.11.0
 */
public abstract class FhirResourceUpdateRequest implements Request<RepositoryContext, FhirResourceUpdateResult> {

	protected final void checkAuthorization(RepositoryContext context, String bundleId) {
		final User user = context.service(User.class);
		final AuthorizationService service = context.optionalService(AuthorizationService.class).orElse(AuthorizationService.DEFAULT);
		
		// We require edit access to the bundle mentioned in the request OR any of its ancestors OR the "*" wildcard.
		final Set<String> resourceIds;
		
		if (!IComponent.ROOT_ID.equals(bundleId)) {
		
			// XXX: This request throws a NotFoundException if the user has no browsing permission for the bundle
			final Bundle bundle = ResourceRequests.bundles()
				.prepareGet(bundleId)
				.buildAsync()
				.execute(context);
		
			resourceIds = newHashSet(bundle.getId(), bundle.getBundleId(), Permission.ALL);
			resourceIds.addAll(bundle.getBundleAncestorIds());
			resourceIds.remove(IComponent.ROOT_ID);
			
		} else {
			resourceIds = ImmutableSet.of(Permission.ALL);
		}
		
		final Permission requiredPermission = Permission.requireAny(Permission.OPERATION_EDIT, resourceIds);
		service.checkPermission(context, user, List.of(requiredPermission));
	}
}
