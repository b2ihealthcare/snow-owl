/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.fhir.core.request;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.ForbiddenException;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.User;

/**
 * @since 8.11.0
 */
public abstract class FhirResourceUpdateRequest implements Request<RepositoryContext, FhirResourceUpdateResult> {

	protected final void checkAuthorization(RepositoryContext context, String bundleId) {
		final User user = context.service(User.class);

		if (!CompareUtils.isEmpty(user.getPermissions()) 
			&& !user.isAdministrator() 
			&& !user.hasPermission(Permission.requireAny(Permission.OPERATION_EDIT, Permission.ALL))) {
			
			throw new ForbiddenException("Operation not permitted for users without editing access.");
		}
	}
}
